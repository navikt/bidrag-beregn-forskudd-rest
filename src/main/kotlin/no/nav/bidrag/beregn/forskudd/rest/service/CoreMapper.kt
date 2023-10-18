package no.nav.bidrag.beregn.forskudd.rest.service

import com.fasterxml.jackson.databind.JsonNode
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.BarnIHusstandenPeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore
import no.nav.bidrag.beregn.forskudd.core.dto.BostatusPeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.InntektPeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.SivilstandPeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.SoknadBarnCore
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException
import no.nav.bidrag.domain.enums.GrunnlagType
import no.nav.bidrag.domain.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domain.enums.sjablon.SjablonTallNavn
import no.nav.bidrag.transport.beregning.felles.BeregnGrunnlag
import no.nav.bidrag.transport.beregning.felles.Grunnlag
import java.time.LocalDate
import java.time.format.DateTimeParseException

object CoreMapper {

    fun mapGrunnlagTilCore(beregnForskuddGrunnlag: BeregnGrunnlag, sjablontallListe: List<Sjablontall>): BeregnForskuddGrunnlagCore {
        // Lager en map for sjablontall (id og navn)
        val sjablontallMap = HashMap<String, SjablonTallNavn>()
        SjablonTallNavn.entries.forEach {
            sjablontallMap[it.id] = it
        }
        var soknadbarnCore: SoknadBarnCore? = null
        val bostatusPeriodeCoreListe = mutableListOf<BostatusPeriodeCore>()
        val inntektPeriodeCoreListe = mutableListOf<InntektPeriodeCore>()
        val sivilstandPeriodeCoreListe = mutableListOf<SivilstandPeriodeCore>()
        val barnIHusstandenPeriodeCoreListe = mutableListOf<BarnIHusstandenPeriodeCore>()

        // Mapper grunnlagstyper til input for core
        beregnForskuddGrunnlag.grunnlagListe!!.forEach {
            when (it.type) {
                GrunnlagType.SOKNADSBARN_INFO -> soknadbarnCore = mapSoknadsbarn(it.innhold!!, it.referanse!!)
                GrunnlagType.BOSTATUS -> bostatusPeriodeCoreListe.add(mapBostatus(it))
                GrunnlagType.INNTEKT -> inntektPeriodeCoreListe.add(mapInntekt(it))
                GrunnlagType.SIVILSTAND -> sivilstandPeriodeCoreListe.add(mapSivilstand(it))
                GrunnlagType.BARN_I_HUSSTAND -> barnIHusstandenPeriodeCoreListe.add(mapBarnIHusstanden(it))
                else -> throw UgyldigInputException("Grunnlagstype ${it.type!!.value} er ikke gyldig")
            }
        }

        val antallSoknadsbarn = beregnForskuddGrunnlag.grunnlagListe!!.count { it.type == GrunnlagType.SOKNADSBARN_INFO }

        // Validerer at alle nødvendige grunnlag er med
        validerGrunnlag(
            merEnnEttSoknadsbarn = antallSoknadsbarn > 1,
            soknadbarnGrunnlag = soknadbarnCore != null,
            bostatusGrunnlag = bostatusPeriodeCoreListe.isNotEmpty(),
            inntektGrunnlag = inntektPeriodeCoreListe.isNotEmpty(),
            sivilstandGrunnlag = sivilstandPeriodeCoreListe.isNotEmpty()
        )

        val sjablonPeriodeCoreListe = mapSjablonVerdier(
            beregnDatoFra = beregnForskuddGrunnlag.beregnDatoFra!!,
            beregnDatoTil = beregnForskuddGrunnlag.beregnDatoTil!!,
            sjablonSjablontallListe = sjablontallListe,
            sjablontallMap = sjablontallMap
        )

        return BeregnForskuddGrunnlagCore(
            beregnDatoFra = beregnForskuddGrunnlag.beregnDatoFra!!,
            beregnDatoTil = beregnForskuddGrunnlag.beregnDatoTil!!,
            soknadBarn = soknadbarnCore!!,
            bostatusPeriodeListe = bostatusPeriodeCoreListe,
            inntektPeriodeListe = inntektPeriodeCoreListe,
            sivilstandPeriodeListe = sivilstandPeriodeCoreListe,
            barnIHusstandenPeriodeListe = barnIHusstandenPeriodeCoreListe,
            sjablonPeriodeListe = sjablonPeriodeCoreListe
        )
    }

    private fun validerGrunnlag(
        merEnnEttSoknadsbarn: Boolean,
        soknadbarnGrunnlag: Boolean,
        bostatusGrunnlag: Boolean,
        inntektGrunnlag: Boolean,
        sivilstandGrunnlag: Boolean
    ) {
        when {
            merEnnEttSoknadsbarn -> {
                throw UgyldigInputException("Det er kun tillatt med en forekomst av SOKNADSBARN_INFO i input")
            }

            !soknadbarnGrunnlag -> {
                throw UgyldigInputException("Grunnlagstype SOKNADSBARN_INFO mangler i input")
            }

            !bostatusGrunnlag -> {
                throw UgyldigInputException("Grunnlagstype BOSTATUS mangler i input")
            }

            !inntektGrunnlag -> {
                throw UgyldigInputException("Grunnlagstype INNTEKT mangler i input")
            }

            !sivilstandGrunnlag -> {
                throw UgyldigInputException("Grunnlagstype SIVILSTAND mangler i input")
            }
        }
    }

    private fun mapSoknadsbarn(grunnlagInnhold: JsonNode, referanse: String): SoknadBarnCore {
        val fodselsdato = grunnlagInnhold.getOrThrow("fodselsdato", "fødselsdato mangler i objekt av type ${GrunnlagType.SOKNADSBARN_INFO.value}")
        return SoknadBarnCore(
            referanse = referanse,
            fodselsdato = formaterDato(dato = fodselsdato, datoType = "fodselsdato", grunnlagType = GrunnlagType.SOKNADSBARN_INFO.value)
        )
    }

    private fun mapBostatus(grunnlag: Grunnlag): BostatusPeriodeCore {
        val bostatusKode = grunnlag.innhold!!.getOrThrow("bostatusKode", "bostatusKode mangler i objekt av type ${GrunnlagType.BOSTATUS.value}")
        return BostatusPeriodeCore(
            referanse = grunnlag.referanse!!,
            periode = mapPeriode(grunnlagInnhold = grunnlag.innhold!!, grunnlagType = grunnlag.type!!.value),
            kode = bostatusKode
        )
    }

    private fun mapInntekt(grunnlag: Grunnlag): InntektPeriodeCore {
        val inntektType = grunnlag.innhold!!.getOrThrow("inntektType", "inntektType mangler i objekt av type ${GrunnlagType.INNTEKT.value}")
        val belop = grunnlag.innhold!!.getOrThrow("belop", "belop mangler i objekt av type ${GrunnlagType.INNTEKT.value}")
        return InntektPeriodeCore(
            referanse = grunnlag.referanse!!,
            periode = mapPeriode(grunnlagInnhold = grunnlag.innhold!!, grunnlagType = grunnlag.type!!.value),
            type = inntektType,
            belop = formaterBelop(belop = belop, grunnlagType = GrunnlagType.INNTEKT.value)
        )
    }

    private fun mapSivilstand(grunnlag: Grunnlag): SivilstandPeriodeCore {
        val sivilstandKode = grunnlag.innhold!!.getOrThrow("sivilstandKode", "sivilstandKode mangler i objekt av type ${GrunnlagType.SIVILSTAND.value}")
        return SivilstandPeriodeCore(
            referanse = grunnlag.referanse!!,
            periode = mapPeriode(grunnlagInnhold = grunnlag.innhold!!, grunnlagType = grunnlag.type!!.value),
            kode = sivilstandKode
        )
    }

    private fun mapBarnIHusstanden(grunnlag: Grunnlag): BarnIHusstandenPeriodeCore {
        val antall = grunnlag.innhold!!.getOrThrow("antall", "antall mangler i objekt av type ${GrunnlagType.BARN_I_HUSSTAND.value}")
        return BarnIHusstandenPeriodeCore(
            referanse = grunnlag.referanse!!,
            periode = mapPeriode(grunnlagInnhold = grunnlag.innhold!!, grunnlagType = grunnlag.type!!.value),
            antall = formaterAntall(antall = antall, grunnlagType = GrunnlagType.BARN_I_HUSSTAND.value)
        )
    }

    private fun mapPeriode(grunnlagInnhold: JsonNode, grunnlagType: String): PeriodeCore {
        val datoFom = grunnlagInnhold.getOrThrow("datoFom", "datoFom mangler i objekt av type $grunnlagType")
        val datoTil = if (grunnlagInnhold["datoTil"] != null && !grunnlagInnhold["datoTil"].isNull) {
            formaterDato(dato = grunnlagInnhold["datoTil"].asText(), datoType = "datoTil", grunnlagType = grunnlagType)
        } else {
            null
        }

        return PeriodeCore(
            datoFom = formaterDato(dato = datoFom, datoType = "datoFom", grunnlagType = grunnlagType),
            datoTil = datoTil
        )
    }

    // Plukker ut aktuelle sjabloner og flytter inn i inputen til core-modulen
    private fun mapSjablonVerdier(
        beregnDatoFra: LocalDate,
        beregnDatoTil: LocalDate,
        sjablonSjablontallListe: List<Sjablontall>,
        sjablontallMap: HashMap<String, SjablonTallNavn>
    ): List<SjablonPeriodeCore> {
        return sjablonSjablontallListe
            .filter { !(it.datoFom!!.isAfter(beregnDatoTil) || it.datoTom!!.isBefore(beregnDatoFra)) }
            .filter { (sjablontallMap.getOrDefault(it.typeSjablon, SjablonTallNavn.DUMMY)).forskudd }
            .map {
                SjablonPeriodeCore(
                    periode = PeriodeCore(it.datoFom!!, it.datoTom),
                    navn = sjablontallMap.getOrDefault(it.typeSjablon, SjablonTallNavn.DUMMY).navn,
                    nokkelListe = emptyList(),
                    innholdListe = listOf(SjablonInnholdCore(navn = SjablonInnholdNavn.SJABLON_VERDI.navn, verdi = it.verdi!!))
                )
            }
    }

    private fun formaterDato(dato: String, datoType: String, grunnlagType: String): LocalDate =
        try {
            LocalDate.parse(dato)
        } catch (e: DateTimeParseException) {
            throw UgyldigInputException("Dato $dato av type $datoType i objekt av type $grunnlagType har feil format")
        }

    private fun formaterBelop(belop: String, grunnlagType: String) =
        belop.toBigDecimalOrNull() ?: throw UgyldigInputException("belop $belop i objekt av type $grunnlagType har feil format")

    private fun formaterAntall(antall: String, grunnlagType: String) =
        antall.toDoubleOrNull() ?: throw UgyldigInputException("antall $antall i objekt av type $grunnlagType har feil format")
}

private fun JsonNode.getOrThrow(felt: String, message: String): String {
    val value = this[felt]
    if (value == null || value.isNull) throw UgyldigInputException(message)

    return value.asText()
}
