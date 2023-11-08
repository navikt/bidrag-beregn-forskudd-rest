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
        val soknadbarnCore = mapSoknadsbarn(beregnForskuddGrunnlag)
        val bostatusPeriodeCoreListe = mapBostatus(beregnForskuddGrunnlag)
        val inntektPeriodeCoreListe = mapInntekt(beregnForskuddGrunnlag)
        val sivilstandPeriodeCoreListe = mapSivilstand(beregnForskuddGrunnlag)
        val barnIHusstandenPeriodeCoreListe = mapBarnIHusstanden(beregnForskuddGrunnlag)

        // Validerer at alle nødvendige grunnlag er med
        validerGrunnlag(
            soknadbarnGrunnlag = soknadbarnCore != null,
            bostatusGrunnlag = bostatusPeriodeCoreListe.isNotEmpty(),
            inntektGrunnlag = inntektPeriodeCoreListe.isNotEmpty(),
            sivilstandGrunnlag = sivilstandPeriodeCoreListe.isNotEmpty(),
            barnIHusstandenGrunnlag = barnIHusstandenPeriodeCoreListe.isNotEmpty()
        )

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

    private fun mapSoknadsbarn(beregnForskuddGrunnlag: BeregnGrunnlag): SoknadBarnCore? {
        try {
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
        } catch (e: Exception) {
            throw UgyldigInputException("Ugyldig input ved beregning av forskudd. Innhold i Grunnlagstype.PERSON er ikke gyldig: " + e.message)
        }
    }

    private fun mapBostatus(beregnForskuddGrunnlag: BeregnGrunnlag): List<BostatusPeriodeCore> {
        try {
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
        } catch (e: Exception) {
            throw UgyldigInputException(
                "Ugyldig input ved beregning av forskudd. Innhold i Grunnlagstype.BOSTATUS_PERIODE er ikke gyldig: " + e.message
            )
        }
    }

    private fun mapInntekt(beregnForskuddGrunnlag: BeregnGrunnlag): List<InntektPeriodeCore> {
        try {
            val inntektGrunnlag = beregnForskuddGrunnlag.hentInnholdBasertPåNavn(
                grunnlagType = Grunnlagstype.BEREGNING_INNTEKT_RAPPORTERING_PERIODE,
                clazz = InntektRapporteringPeriode::class.java
            )

            return inntektGrunnlag
                .filter { it.innhold.valgt }
                .map {
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
        } catch (e: Exception) {
            throw UgyldigInputException(
                "Ugyldig input ved beregning av forskudd. Innhold i Grunnlagstype.BEREGNING_INNTEKT_RAPPORTERING_PERIODE er ikke gyldig: " + e.message
            )
        }
    }

    private fun mapSivilstand(beregnForskuddGrunnlag: BeregnGrunnlag): List<SivilstandPeriodeCore> {
        try {
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
        } catch (e: Exception) {
            throw UgyldigInputException(
                "Ugyldig input ved beregning av forskudd. Innhold i Grunnlagstype.SIVILSTAND_PERIODE er ikke gyldig: " + e.message
            )
        }
    }

    private fun mapBarnIHusstanden(beregnForskuddGrunnlag: BeregnGrunnlag): List<BarnIHusstandenPeriodeCore> {
        try {
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
        } catch (e: Exception) {
            throw UgyldigInputException(
                "Ugyldig input ved beregning av forskudd. Innhold i Grunnlagstype.BOSTATUS_PERIODE er ikke gyldig: " + e.message
            )
        }
    }

    private fun validerGrunnlag(
        soknadbarnGrunnlag: Boolean,
        bostatusGrunnlag: Boolean,
        inntektGrunnlag: Boolean,
        sivilstandGrunnlag: Boolean,
        barnIHusstandenGrunnlag: Boolean
    ) {
        when {
            !soknadbarnGrunnlag -> {
                throw UgyldigInputException("Søknadsbarn mangler i input")
            }

            !bostatusGrunnlag -> {
                throw UgyldigInputException("Bostatus mangler i input")
            }

            !inntektGrunnlag -> {
                throw UgyldigInputException("Inntekt mangler i input")
            }

            !sivilstandGrunnlag -> {
                throw UgyldigInputException("Sivilstand mangler i input")
            }

            !barnIHusstandenGrunnlag -> {
                throw UgyldigInputException("Barn i husstanden mangler i input")
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
