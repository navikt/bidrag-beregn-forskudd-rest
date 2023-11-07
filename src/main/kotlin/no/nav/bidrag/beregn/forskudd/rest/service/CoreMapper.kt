package no.nav.bidrag.beregn.forskudd.rest.service

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
import no.nav.bidrag.domene.enums.Bostatuskode
import no.nav.bidrag.domene.enums.Grunnlagstype
import no.nav.bidrag.domene.enums.sjablon.SjablonInnholdNavn
import no.nav.bidrag.domene.enums.sjablon.SjablonTallNavn
import no.nav.bidrag.transport.behandling.beregning.felles.BeregnGrunnlag
import no.nav.bidrag.transport.behandling.beregning.felles.grunnlag.InntektRapporteringPeriode
import no.nav.bidrag.transport.behandling.beregning.felles.hentInnholdBasertPåNavn
import no.nav.bidrag.transport.behandling.beregning.felles.hentInnholdBasertPåReferanse
import no.nav.bidrag.transport.behandling.felles.grunnlag.BostatusPeriode
import no.nav.bidrag.transport.behandling.felles.grunnlag.Person
import no.nav.bidrag.transport.behandling.felles.grunnlag.SivilstandPeriode
import java.time.LocalDate

object CoreMapper {

    fun mapGrunnlagTilCore(beregnForskuddGrunnlag: BeregnGrunnlag, sjablontallListe: List<Sjablontall>): BeregnForskuddGrunnlagCore {
        // Lager en map for sjablontall (id og navn)
        val sjablontallMap = HashMap<String, SjablonTallNavn>()
        SjablonTallNavn.entries.forEach {
            sjablontallMap[it.id] = it
        }

        // Mapper grunnlagstyper til input for core
        val soknadbarnCore = mapSoknadsbarnNy(beregnForskuddGrunnlag)
        val bostatusPeriodeCoreListe = mapBostatusNy(beregnForskuddGrunnlag)
        val inntektPeriodeCoreListe = mapInntektNy(beregnForskuddGrunnlag)
        val sivilstandPeriodeCoreListe = mapSivilstandNy(beregnForskuddGrunnlag)
        val barnIHusstandenPeriodeCoreListe = mapBarnIHusstandenNy(beregnForskuddGrunnlag)

        // Validerer at alle nødvendige grunnlag er med
//        validerGrunnlag(
//            merEnnEttSoknadsbarn = antallSoknadsbarn > 1,
//            merEnnEttSoknadsbarn = false,
//            soknadbarnGrunnlag = soknadbarnCore != null,
//            bostatusGrunnlag = bostatusPeriodeCoreListe.isNotEmpty(),
//            inntektGrunnlag = inntektPeriodeCoreListe.isNotEmpty(),
//            sivilstandGrunnlag = sivilstandPeriodeCoreListe.isNotEmpty()
//        )

        val sjablonPeriodeCoreListe = mapSjablonVerdier(
            beregnDatoFra = beregnForskuddGrunnlag.periode!!.fomDato.verdi,
            beregnDatoTil = beregnForskuddGrunnlag.periode!!.tilDato!!.verdi,
            sjablonSjablontallListe = sjablontallListe,
            sjablontallMap = sjablontallMap
        )

        return BeregnForskuddGrunnlagCore(
            beregnDatoFra = beregnForskuddGrunnlag.periode!!.fomDato.verdi,
            beregnDatoTil = beregnForskuddGrunnlag.periode!!.tilDato!!.verdi,
            soknadBarn = soknadbarnCore!!,
            bostatusPeriodeListe = bostatusPeriodeCoreListe,
            inntektPeriodeListe = inntektPeriodeCoreListe,
            sivilstandPeriodeListe = sivilstandPeriodeCoreListe,
            barnIHusstandenPeriodeListe = barnIHusstandenPeriodeCoreListe,
            sjablonPeriodeListe = sjablonPeriodeCoreListe
        )
    }

    private fun mapSoknadsbarnNy(beregnForskuddGrunnlag: BeregnGrunnlag): SoknadBarnCore? {
        val soknadsbarnGrunnlag = beregnForskuddGrunnlag.hentInnholdBasertPåNavn(
            grunnlagType = Grunnlagstype.PERSON,
            clazz = Person::class.java,
            navn = beregnForskuddGrunnlag.søknadsbarnReferanse!!
        )

        return if (soknadsbarnGrunnlag.isEmpty() || soknadsbarnGrunnlag.count() > 1) {
            null
        } else {
            SoknadBarnCore(
                referanse = soknadsbarnGrunnlag[0].navn,
                fodselsdato = soknadsbarnGrunnlag[0].innhold.fødselsdato.verdi
            )
        }
    }

    private fun mapBostatusNy(beregnForskuddGrunnlag: BeregnGrunnlag): List<BostatusPeriodeCore> {
        val bostatusGrunnlag = beregnForskuddGrunnlag.hentInnholdBasertPåReferanse(
            grunnlagType = Grunnlagstype.BOSTATUS_PERIODE,
            clazz = BostatusPeriode::class.java,
            referanse = beregnForskuddGrunnlag.søknadsbarnReferanse!!
        )

        return bostatusGrunnlag.map {
            BostatusPeriodeCore(
                referanse = it.navn,
                periode = PeriodeCore(
                    datoFom = it.innhold.periode.toDatoperiode().fom,
                    datoTil = it.innhold.periode.toDatoperiode().til
                ),
                kode = it.innhold.bostatus.name
            )
        }
    }

    private fun mapInntektNy(beregnForskuddGrunnlag: BeregnGrunnlag): List<InntektPeriodeCore> {
        val inntektGrunnlag = beregnForskuddGrunnlag.hentInnholdBasertPåNavn(
            grunnlagType = Grunnlagstype.BEREGNING_INNTEKT_RAPPORTERING_PERIODE,
            clazz = InntektRapporteringPeriode::class.java
        )

        return inntektGrunnlag.map {
            InntektPeriodeCore(
                referanse = it.navn,
                periode = PeriodeCore(
                    datoFom = it.innhold.periode.toDatoperiode().fom,
                    datoTil = it.innhold.periode.toDatoperiode().til
                ),
                type = it.innhold.inntektRapportering.name,
                belop = it.innhold.beløp
            )
        }
    }

    private fun mapSivilstandNy(beregnForskuddGrunnlag: BeregnGrunnlag): List<SivilstandPeriodeCore> {
        val sivilstandGrunnlag = beregnForskuddGrunnlag.hentInnholdBasertPåNavn(
            grunnlagType = Grunnlagstype.SIVILSTAND_PERIODE,
            clazz = SivilstandPeriode::class.java
        )

        return sivilstandGrunnlag.map {
            SivilstandPeriodeCore(
                referanse = it.navn,
                periode = PeriodeCore(
                    datoFom = it.innhold.periode.toDatoperiode().fom,
                    datoTil = it.innhold.periode.toDatoperiode().til
                ),
                kode = it.innhold.sivilstand.name
            )
        }
    }

    private fun mapBarnIHusstandenNy(beregnForskuddGrunnlag: BeregnGrunnlag): List<BarnIHusstandenPeriodeCore> {
        val barnIHusstandenGrunnlag = beregnForskuddGrunnlag.hentInnholdBasertPåNavn(
            grunnlagType = Grunnlagstype.BOSTATUS_PERIODE,
            clazz = BostatusPeriode::class.java
        )

        return barnIHusstandenGrunnlag
            .filter { it.innhold.bostatus == Bostatuskode.MED_FORELDER || it.innhold.bostatus == Bostatuskode.DOKUMENTERT_SKOLEGANG }
            .map {
                BarnIHusstandenPeriodeCore(
                    referanse = it.navn,
                    periode = PeriodeCore(
                        datoFom = it.innhold.periode.toDatoperiode().fom,
                        datoTil = it.innhold.periode.toDatoperiode().til
                    )
                )
            }
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
}
