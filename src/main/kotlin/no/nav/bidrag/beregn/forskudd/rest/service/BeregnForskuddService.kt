package no.nav.bidrag.beregn.forskudd.rest.service

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.beregn.forskudd.core.ForskuddCore
import no.nav.bidrag.beregn.forskudd.rest.SECURE_LOGGER
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException
import no.nav.bidrag.commons.web.HttpResponse
import no.nav.bidrag.commons.web.HttpResponse.Companion.from
import no.nav.bidrag.transport.beregning.felles.SjablonResultatGrunnlagCore
import no.nav.bidrag.transport.beregning.forskudd.core.response.BeregnetForskuddResultatCore
import no.nav.bidrag.transport.beregning.forskudd.rest.request.BeregnForskuddGrunnlag
import no.nav.bidrag.transport.beregning.forskudd.rest.request.Grunnlag
import no.nav.bidrag.transport.beregning.forskudd.rest.response.BeregnetForskuddResultat
import no.nav.bidrag.transport.beregning.forskudd.rest.response.ResultatGrunnlag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class BeregnForskuddService(private val sjablonConsumer: SjablonConsumer, private val forskuddCore: ForskuddCore) {
    fun beregn(grunnlag: BeregnForskuddGrunnlag): HttpResponse<BeregnetForskuddResultat> {
        // Kontroll av inputdata
        grunnlag.valider()

        // Henter sjabloner
        val sjablonSjablontallResponse: HttpResponse<List<Sjablontall>?> = sjablonConsumer.hentSjablonSjablontall()
        if (sjablonSjablontallResponse.responseEntity.body != null) {
            if (LOGGER.isDebugEnabled) {
                LOGGER.debug("Antall sjabloner hentet av type Sjablontall: {}", sjablonSjablontallResponse.responseEntity.body!!.size)
            }

            // Lager input-grunnlag til core-modulen
            val grunnlagTilCore = CoreMapper.mapGrunnlagTilCore(grunnlag, sjablonSjablontallResponse.responseEntity.body!!)
            val resultatFraCore: BeregnetForskuddResultatCore

            // Kaller core-modulen for beregning av forskudd
            if (SECURE_LOGGER.isDebugEnabled) {
                SECURE_LOGGER.debug("Forskudd - grunnlag for beregning: {}", grunnlagTilCore)
            }
            resultatFraCore = try {
                forskuddCore.beregnForskudd(grunnlagTilCore)
            } catch (e: Exception) {
                throw UgyldigInputException("Ugyldig input ved beregning av forskudd: " + e.message)
            }
            if (!resultatFraCore.avvikListe.isEmpty()) {
                val avvikTekst = resultatFraCore.avvikListe.joinToString("; ") { it.avvikTekst }
                LOGGER.error("Ugyldig input ved beregning av forskudd. Følgende avvik ble funnet: $avvikTekst")
                SECURE_LOGGER.error("Ugyldig input ved beregning av forskudd. Følgende avvik ble funnet: $avvikTekst")
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
            val grunnlagReferanseListe = lagGrunnlagReferanseListe(grunnlag, resultatFraCore)
            return from(HttpStatus.OK, BeregnetForskuddResultat(resultatFraCore, grunnlagReferanseListe))
        } else {
            LOGGER.error("Klarte ikke å hente sjabloner")
        }
        SECURE_LOGGER.error("Klarte ikke å hente sjabloner")
        return from(HttpStatus.OK, BeregnetForskuddResultat())
    }

    // Lager en liste over resultatgrunnlag som inneholder:
    //   - mottatte grunnlag som er brukt i beregningen
    //   - sjabloner som er brukt i beregningen
    private fun lagGrunnlagReferanseListe(
        forskuddGrunnlag: BeregnForskuddGrunnlag,
        resultatFraCore: BeregnetForskuddResultatCore
    ): List<ResultatGrunnlag> {
        val mapper = ObjectMapper()
        val resultatGrunnlagListe = ArrayList<ResultatGrunnlag>()
        val grunnlagReferanseListe = resultatFraCore.beregnetForskuddPeriodeListe
            .flatMap { it.grunnlagReferanseListe }
            .distinct()

        // Matcher mottatte grunnlag med grunnlag som er brukt i beregningen
        resultatGrunnlagListe.addAll(
            forskuddGrunnlag.grunnlagListe!!.stream()
                .filter { (referanse): Grunnlag -> grunnlagReferanseListe.contains(referanse) }
                .map { (referanse, type, innhold): Grunnlag ->
                    ResultatGrunnlag(
                        referanse!!,
                        type!!,
                        innhold!!
                    )
                }
                .toList()
        )

        // Danner grunnlag basert på liste over sjabloner som er brukt i beregningen
        resultatGrunnlagListe.addAll(
            resultatFraCore.sjablonListe.stream()
                .map { (referanse, periode, navn, verdi): SjablonResultatGrunnlagCore ->
                    val map = LinkedHashMap<String, Any>()
                    map["datoFom"] = mapDato(periode.datoFom)
                    map["datoTil"] = mapDato(periode.datoTil)
                    map["sjablonNavn"] = navn
                    map["sjablonVerdi"] = verdi.toInt()
                    ResultatGrunnlag(referanse, "SJABLON", mapper.valueToTree(map))
                }
                .toList()
        )
        return resultatGrunnlagListe
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(BeregnForskuddService::class.java)

        // Unngå å legge ut datoer høyere enn 9999-12-31
        private fun mapDato(dato: LocalDate?): String {
            return if (dato!!.isAfter(LocalDate.parse("9999-12-31"))) "9999-12-31" else dato.toString()
        }
    }
}

fun BeregnForskuddGrunnlag.valider() {
    if (beregnDatoFra == null) throw UgyldigInputException("beregnDatoFra kan ikke være null")
    if (beregnDatoTil == null) throw UgyldigInputException("beregnDatoTil kan ikke være null")
    grunnlagListe?.map { it.valider() } ?: throw UgyldigInputException("grunnlagListe kan ikke være null")
}

fun Grunnlag.valider() {
    if (referanse == null) throw UgyldigInputException("referanse kan ikke være null")
    if (type == null) throw UgyldigInputException("type kan ikke være null")
    if (innhold == null) throw UgyldigInputException("innhold kan ikke være null")
}
