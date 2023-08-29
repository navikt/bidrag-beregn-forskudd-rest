package no.nav.bidrag.beregn.forskudd.rest.service

import com.fasterxml.jackson.databind.JsonNode
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn.values
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException
import no.nav.bidrag.transport.beregning.felles.PeriodeCore
import no.nav.bidrag.transport.beregning.felles.SjablonInnholdCore
import no.nav.bidrag.transport.beregning.felles.SjablonPeriodeCore
import no.nav.bidrag.transport.beregning.forskudd.core.request.BarnIHusstandenPeriodeCore
import no.nav.bidrag.transport.beregning.forskudd.core.request.BeregnForskuddGrunnlagCore
import no.nav.bidrag.transport.beregning.forskudd.core.request.BostatusPeriodeCore
import no.nav.bidrag.transport.beregning.forskudd.core.request.InntektPeriodeCore
import no.nav.bidrag.transport.beregning.forskudd.core.request.SivilstandPeriodeCore
import no.nav.bidrag.transport.beregning.forskudd.core.request.SoknadBarnCore
import no.nav.bidrag.transport.beregning.forskudd.rest.request.BeregnForskuddGrunnlag
import no.nav.bidrag.transport.beregning.forskudd.rest.request.Grunnlag
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeParseException

object CoreMapper {
    private const val SOKNADSBARN_TYPE = "SOKNADSBARN_INFO"
    private const val BOSTATUS_TYPE = "BOSTATUS"
    private const val INNTEKT_TYPE = "INNTEKT"
    private const val SIVILSTAND_TYPE = "SIVILSTAND"
    private const val BARN_I_HUSSTAND_TYPE = "BARN_I_HUSSTAND"
    fun mapGrunnlagTilCore(beregnForskuddGrunnlag: BeregnForskuddGrunnlag, sjablontallListe: List<Sjablontall>): BeregnForskuddGrunnlagCore {

        // Lager en map for sjablontall (id og navn)
        val sjablontallMap = HashMap<String?, SjablonTallNavn>()
        for (sjablonTallNavn in values()) {
            sjablontallMap[sjablonTallNavn.id] = sjablonTallNavn
        }
        var soknadbarnCore: SoknadBarnCore? = null
        val bostatusPeriodeCoreListe = ArrayList<BostatusPeriodeCore>()
        val inntektPeriodeCoreListe = ArrayList<InntektPeriodeCore>()
        val sivilstandPeriodeCoreListe = ArrayList<SivilstandPeriodeCore>()
        val barnIHusstandenPeriodeCoreListe = ArrayList<BarnIHusstandenPeriodeCore>()

        // Mapper grunnlagstyper til input for core
        for (grunnlag in beregnForskuddGrunnlag.grunnlagListe!!) {
            when (grunnlag.type) {
                SOKNADSBARN_TYPE -> soknadbarnCore = mapSoknadsbarn(grunnlag)
                BOSTATUS_TYPE -> bostatusPeriodeCoreListe.add(mapBostatus(grunnlag))
                INNTEKT_TYPE -> inntektPeriodeCoreListe.add(mapInntekt(grunnlag))
                SIVILSTAND_TYPE -> sivilstandPeriodeCoreListe.add(mapSivilstand(grunnlag))
                BARN_I_HUSSTAND_TYPE -> barnIHusstandenPeriodeCoreListe.add(mapBarnIHusstanden(grunnlag))
            }
        }
        val antallSoknadsbarn = beregnForskuddGrunnlag.grunnlagListe?.stream()
            ?.filter { (_, type): Grunnlag -> type == SOKNADSBARN_TYPE }
            ?.count()

        // Validerer at alle nødvendige grunnlag er med
        if (antallSoknadsbarn != null) {
            validerGrunnlag(
                antallSoknadsbarn > 1, soknadbarnCore != null, bostatusPeriodeCoreListe.isNotEmpty(), inntektPeriodeCoreListe.isNotEmpty(),
                sivilstandPeriodeCoreListe.isNotEmpty()
            )
        }
        val sjablonPeriodeCoreListe = mapSjablonVerdier(
            beregnForskuddGrunnlag.beregnDatoFra, beregnForskuddGrunnlag.beregnDatoTil,
            sjablontallListe, sjablontallMap
        )
        return BeregnForskuddGrunnlagCore(
            beregnForskuddGrunnlag.beregnDatoFra!!, beregnForskuddGrunnlag.beregnDatoTil!!, soknadbarnCore!!,
            bostatusPeriodeCoreListe, inntektPeriodeCoreListe, sivilstandPeriodeCoreListe, barnIHusstandenPeriodeCoreListe, sjablonPeriodeCoreListe
        )
    }

    private fun validerGrunnlag(
        merEnnEttSoknadsbarn: Boolean, soknadbarnGrunnlag: Boolean, bostatusGrunnlag: Boolean, inntektGrunnlag: Boolean,
        sivilstandGrunnlag: Boolean
    ) {
        if (merEnnEttSoknadsbarn) {
            throw UgyldigInputException("Det er kun tillatt med en forekomst av SOKNADSBARN_INFO i input")
        } else if (!soknadbarnGrunnlag) {
            throw UgyldigInputException("Grunnlagstype SOKNADSBARN_INFO mangler i input")
        } else if (!bostatusGrunnlag) {
            throw UgyldigInputException("Grunnlagstype BOSTATUS mangler i input")
        } else if (!inntektGrunnlag) {
            throw UgyldigInputException("Grunnlagstype INNTEKT mangler i input")
        } else if (!sivilstandGrunnlag) {
            throw UgyldigInputException("Grunnlagstype SIVILSTAND mangler i input")
        }
    }

    private fun mapSoknadsbarn(grunnlag: Grunnlag): SoknadBarnCore {
        val fodselsdato = (if (grunnlag.innhold!!["fodselsdato"] != null) grunnlag.innhold!!["fodselsdato"].asText() else null)
            ?: throw UgyldigInputException("fødselsdato mangler i objekt av type " + SOKNADSBARN_TYPE)
        return SoknadBarnCore(grunnlag.referanse!!, formaterDato(fodselsdato, "fodselsdato", SOKNADSBARN_TYPE)!!)
    }

    private fun mapBostatus(grunnlag: Grunnlag): BostatusPeriodeCore {
        val bostatusKode = (if (grunnlag.innhold!!["bostatusKode"] != null) grunnlag.innhold!!["bostatusKode"].asText() else null)
            ?: throw UgyldigInputException("bostatusKode mangler i objekt av type " + BOSTATUS_TYPE)
        return BostatusPeriodeCore(grunnlag.referanse!!, mapPeriode(grunnlag.innhold, grunnlag.type), bostatusKode)
    }

    private fun mapInntekt(grunnlag: Grunnlag): InntektPeriodeCore {
        val inntektType = (if (grunnlag.innhold!!["inntektType"] != null) grunnlag.innhold!!["inntektType"].asText() else null)
            ?: throw UgyldigInputException("inntektType mangler i objekt av type " + INNTEKT_TYPE)
        val belop = (if (grunnlag.innhold!!["belop"] != null) grunnlag.innhold!!["belop"].asText() else null)
            ?: throw UgyldigInputException("belop mangler i objekt av type " + INNTEKT_TYPE)
        return InntektPeriodeCore(
            grunnlag.referanse!!, mapPeriode(grunnlag.innhold, grunnlag.type), inntektType,
            formaterBelop(belop, INNTEKT_TYPE)!!
        )
    }

    private fun mapSivilstand(grunnlag: Grunnlag): SivilstandPeriodeCore {
        val sivilstandKode = (if (grunnlag.innhold!!["sivilstandKode"] != null) grunnlag.innhold!!["sivilstandKode"].asText() else null)
            ?: throw UgyldigInputException("sivilstandKode mangler i objekt av type " + SIVILSTAND_TYPE)
        return SivilstandPeriodeCore(grunnlag.referanse!!, mapPeriode(grunnlag.innhold, grunnlag.type), sivilstandKode)
    }

    private fun mapBarnIHusstanden(grunnlag: Grunnlag): BarnIHusstandenPeriodeCore {
        val antall = (if (grunnlag.innhold!!["antall"] != null) grunnlag.innhold!!["antall"].asText() else null)
            ?: throw UgyldigInputException("antall mangler i objekt av type " + BARN_I_HUSSTAND_TYPE)
        return BarnIHusstandenPeriodeCore(
            grunnlag.referanse!!, mapPeriode(grunnlag.innhold, grunnlag.type),
            formaterAntall(antall, BARN_I_HUSSTAND_TYPE)!!
        )
    }

    private fun mapPeriode(grunnlagInnhold: JsonNode?, grunnlagType: String?): PeriodeCore {
        val datoFom = (if (grunnlagInnhold!!["datoFom"] != null) grunnlagInnhold["datoFom"].asText() else null)
            ?: throw UgyldigInputException("datoFom mangler i objekt av type $grunnlagType")
        val datoTil = if (grunnlagInnhold["datoTil"] != null) grunnlagInnhold["datoTil"].asText() else null
        return PeriodeCore(formaterDato(datoFom, "datoFom", grunnlagType)!!, formaterDato(datoTil, "datoTil", grunnlagType))
    }

    // Plukker ut aktuelle sjabloner og flytter inn i inputen til core-modulen
    private fun mapSjablonVerdier(
        beregnDatoFra: LocalDate?, beregnDatoTil: LocalDate?,
        sjablonSjablontallListe: List<Sjablontall>,
        sjablontallMap: HashMap<String?, SjablonTallNavn>
    ): List<SjablonPeriodeCore> {
        return sjablonSjablontallListe
            .stream()
            .filter { (_, datoFom, datoTom): Sjablontall -> !datoFom!!.isAfter(beregnDatoTil) && !datoTom!!.isBefore(beregnDatoFra) }
            .filter { (typeSjablon): Sjablontall -> filtrerSjablonTall(sjablontallMap.getOrDefault(typeSjablon, SjablonTallNavn.DUMMY)) }
            .map { (typeSjablon, datoFom, datoTom, verdi): Sjablontall ->
                SjablonPeriodeCore(
                    PeriodeCore(datoFom!!, datoTom),
                    sjablontallMap.getOrDefault(typeSjablon, SjablonTallNavn.DUMMY).navn,
                    emptyList(),
                    listOf(SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.navn, verdi!!))
                )
            }
            .toList()
    }

    // Sjekker om en type SjablonTall er i bruk for forskudd
    private fun filtrerSjablonTall(sjablonTallNavn: SjablonTallNavn): Boolean {
        return sjablonTallNavn.forskudd
    }

    private fun formaterDato(dato: String?, datoType: String, grunnlagType: String?): LocalDate? {
        return if (dato == null || dato == "null") {
            null
        } else try {
            LocalDate.parse(dato)
        } catch (e: DateTimeParseException) {
            throw UgyldigInputException("Dato $dato av type $datoType i objekt av type $grunnlagType har feil format")
        }
    }

    private fun formaterBelop(belop: String?, grunnlagType: String): BigDecimal? {
        return if (belop == null || belop == "null") {
            null
        } else try {
            BigDecimal(belop)
        } catch (e: NumberFormatException) {
            throw UgyldigInputException("belop $belop i objekt av type $grunnlagType har feil format")
        }
    }

    private fun formaterAntall(antall: String?, grunnlagType: String): Double? {
        return if (antall == null || antall == "null") {
            null
        } else try {
            antall.toDouble()
        } catch (e: NumberFormatException) {
            throw UgyldigInputException("antall $antall i objekt av type $grunnlagType har feil format")
        }
    }
}
