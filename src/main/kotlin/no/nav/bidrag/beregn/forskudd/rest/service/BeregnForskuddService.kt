package no.nav.bidrag.beregn.forskudd.rest.service

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.beregn.forskudd.core.ForskuddCore
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnetForskuddResultatCore
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatPeriodeCore
import no.nav.bidrag.beregn.forskudd.rest.SECURE_LOGGER
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException
import no.nav.bidrag.commons.web.HttpResponse
import no.nav.bidrag.commons.web.HttpResponse.Companion.from
import no.nav.bidrag.domene.enums.Grunnlagstype
import no.nav.bidrag.domene.enums.resultatkoder.ResultatKodeForskudd
import no.nav.bidrag.domene.tid.ÅrMånedsperiode
import no.nav.bidrag.transport.behandling.beregning.felles.BeregnGrunnlag
import no.nav.bidrag.transport.behandling.beregning.felles.Grunnlag
import no.nav.bidrag.transport.behandling.beregning.felles.valider
import no.nav.bidrag.transport.behandling.beregning.forskudd.BeregnetForskuddResultat
import no.nav.bidrag.transport.behandling.beregning.forskudd.ResultatBeregning
import no.nav.bidrag.transport.behandling.beregning.forskudd.ResultatPeriode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class BeregnForskuddService(private val sjablonConsumer: SjablonConsumer, private val forskuddCore: ForskuddCore) {

    fun beregn(grunnlag: BeregnGrunnlag): HttpResponse<BeregnetForskuddResultat> {
        if (SECURE_LOGGER.isDebugEnabled) {
            SECURE_LOGGER.debug("Mottatt følgende request: {}", grunnlag)
        }

        // Kontroll av inputdata
        try {
            grunnlag.valider()
        } catch (e: IllegalArgumentException) {
            throw UgyldigInputException("Ugyldig input ved beregning av forskudd: " + e.message)
        }

        // Henter sjabloner
        val sjablonSjablontallResponse: HttpResponse<List<Sjablontall>> = sjablonConsumer.hentSjablonSjablontall()

        if (sjablonSjablontallResponse.responseEntity.body.isNullOrEmpty()) {
            LOGGER.error("Klarte ikke å hente sjabloner")
            return from(httpStatus = HttpStatus.OK, body = BeregnetForskuddResultat())
        }

        val sjablonTallListe = sjablonSjablontallResponse.responseEntity.body!!

        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("Antall sjabloner hentet av type Sjablontall: ${sjablonSjablontallResponse.responseEntity.body!!.size}")
        }

        // Lager input-grunnlag til core-modulen
        val grunnlagTilCore = CoreMapper.mapGrunnlagTilCore(
            beregnForskuddGrunnlag = grunnlag,
            sjablontallListe = sjablonTallListe
        )

        if (SECURE_LOGGER.isDebugEnabled) {
            SECURE_LOGGER.debug("Forskudd - grunnlag for beregning: {}", grunnlagTilCore)
        }

        // Kaller core-modulen for beregning av forskudd
        val resultatFraCore = try {
            forskuddCore.beregnForskudd(grunnlagTilCore)
        } catch (e: Exception) {
            throw UgyldigInputException("Ugyldig input ved beregning av forskudd: " + e.message)
        }

        if (resultatFraCore.avvikListe.isNotEmpty()) {
            val avvikTekst = resultatFraCore.avvikListe.joinToString("; ") { it.avvikTekst }
            LOGGER.warn("Ugyldig input ved beregning av forskudd. Følgende avvik ble funnet: $avvikTekst")
            SECURE_LOGGER.warn("Ugyldig input ved beregning av forskudd. Følgende avvik ble funnet: $avvikTekst")
            SECURE_LOGGER.info(
                "Forskudd - grunnlag for beregning: " + System.lineSeparator() +
                    "beregnDatoFra= " + grunnlagTilCore.beregnDatoFra + System.lineSeparator() +
                    "beregnDatoTil= " + grunnlagTilCore.beregnDatoTil + System.lineSeparator() +
                    "soknadBarn= " + grunnlagTilCore.soknadBarn + System.lineSeparator() +
                    "barnIHusstandenPeriodeListe= " + grunnlagTilCore.barnIHusstandenPeriodeListe + System.lineSeparator() +
                    "inntektPeriodeListe= " + grunnlagTilCore.inntektPeriodeListe + System.lineSeparator() +
                    "sivilstandPeriodeListe= " + grunnlagTilCore.sivilstandPeriodeListe + System.lineSeparator()
            )
            throw UgyldigInputException("Ugyldig input ved beregning av forskudd. Følgende avvik ble funnet: $avvikTekst")
        }

        if (SECURE_LOGGER.isDebugEnabled) {
            SECURE_LOGGER.debug("Forskudd - resultat av beregning: {}", resultatFraCore.beregnetForskuddPeriodeListe)
        }

        val grunnlagReferanseListe = lagGrunnlagReferanseListe(forskuddGrunnlag = grunnlag, resultatFraCore = resultatFraCore)

        val respons = BeregnetForskuddResultat(
            beregnetForskuddPeriodeListe = mapFraResultatPeriodeCore(resultatFraCore.beregnetForskuddPeriodeListe),
            grunnlagListe = grunnlagReferanseListe
        )

        if (SECURE_LOGGER.isDebugEnabled) {
            SECURE_LOGGER.debug("Returnerer følgende respons: {}", respons)
        }

        return from(httpStatus = HttpStatus.OK, body = respons)
    }

    private fun mapFraResultatPeriodeCore(resultatPeriodeCoreListe: List<ResultatPeriodeCore>) =
        resultatPeriodeCoreListe.map {
            ResultatPeriode(
                periode = ÅrMånedsperiode(fom = it.periode.datoFom, til = it.periode.datoTil),
                resultat = ResultatBeregning(
                    belop = it.resultat.belop,
                    kode = ResultatKodeForskudd.valueOf(it.resultat.kode),
                    regel = it.resultat.regel
                ),
                grunnlagReferanseListe = it.grunnlagReferanseListe
            )
        }

    // Lager en liste over resultatgrunnlag som inneholder:
    //   - mottatte grunnlag som er brukt i beregningen
    //   - sjabloner som er brukt i beregningen
    private fun lagGrunnlagReferanseListe(forskuddGrunnlag: BeregnGrunnlag, resultatFraCore: BeregnetForskuddResultatCore): List<Grunnlag> {
        val mapper = ObjectMapper()
        val resultatGrunnlagListe = mutableListOf<Grunnlag>()
        val grunnlagReferanseListe = resultatFraCore.beregnetForskuddPeriodeListe
            .flatMap { it.grunnlagReferanseListe }
            .distinct()

        // Matcher mottatte grunnlag med grunnlag som er brukt i beregningen
        resultatGrunnlagListe.addAll(
            forskuddGrunnlag.grunnlagListe!!
                .filter { grunnlagReferanseListe.contains(it.referanse) }
                .map { Grunnlag(referanse = it.referanse, type = it.type, innhold = it.innhold) }
        )

        // Danner grunnlag basert på liste over sjabloner som er brukt i beregningen
        resultatGrunnlagListe.addAll(
            resultatFraCore.sjablonListe
                .map {
                    val map = LinkedHashMap<String, Any>()
                    map["datoFom"] = mapDato(it.periode.datoFom)
                    map["datoTil"] = mapDato(it.periode.datoTil!!)
                    map["sjablonNavn"] = it.navn
                    map["sjablonVerdi"] = it.verdi.toInt()
                    Grunnlag(referanse = it.referanse, type = Grunnlagstype.SJABLON, innhold = mapper.valueToTree(map))
                }
        )

        return resultatGrunnlagListe
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(BeregnForskuddService::class.java)

        // Unngå å legge ut datoer høyere enn 9999-12-31
        private fun mapDato(dato: LocalDate): String {
            return if (dato.isAfter(LocalDate.parse("9999-12-31"))) "9999-12-31" else dato.toString()
        }
    }
}
