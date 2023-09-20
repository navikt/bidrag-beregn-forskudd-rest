package no.nav.bidrag.beregn.forskudd.rest

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnetForskuddResultatCore
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatBeregningCore
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatPeriodeCore
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall
import no.nav.bidrag.domain.enums.GrunnlagType
import no.nav.bidrag.domain.enums.resultatkoder.ResultatKodeForskudd
import no.nav.bidrag.transport.beregning.felles.BeregnGrunnlag
import no.nav.bidrag.transport.beregning.felles.Grunnlag
import no.nav.bidrag.transport.beregning.felles.Periode
import no.nav.bidrag.transport.beregning.forskudd.BeregnetForskuddResultat
import no.nav.bidrag.transport.beregning.forskudd.ResultatBeregning
import no.nav.bidrag.transport.beregning.forskudd.ResultatPeriode
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Map

object TestUtil {
    private const val INNTEKT_REFERANSE_1 = "INNTEKT_REFERANSE_1"
    private const val SIVILSTAND_REFERANSE_ENSLIG = "SIVILSTAND_REFERANSE_ENSLIG"
    private const val BARN_REFERANSE_1 = "BARN_REFERANSE_1"
    private const val SOKNADBARN_REFERANSE = "SOKNADBARN_REFERANSE"
    private const val BOSTATUS_REFERANSE_MED_FORELDRE_1 = "BOSTATUS_REFERANSE_MED_FORELDRE_1"

    fun byggDummyForskuddGrunnlag(): BeregnGrunnlag {
        return byggDummyForskuddGrunnlag("")
    }

    fun byggForskuddGrunnlagUtenBeregnDatoFra(): BeregnGrunnlag {
        return byggDummyForskuddGrunnlag("beregnDatoFra")
    }

    fun byggForskuddGrunnlagUtenBeregnDatoTil(): BeregnGrunnlag {
        return byggDummyForskuddGrunnlag("beregnDatoTil")
    }

    fun byggForskuddGrunnlagUtenGrunnlagListe(): BeregnGrunnlag {
        return byggDummyForskuddGrunnlag("grunnlagListe")
    }

    fun byggForskuddGrunnlagUtenReferanse(): BeregnGrunnlag {
        return byggDummyForskuddGrunnlag("referanse")
    }

    fun byggForskuddGrunnlagUtenType(): BeregnGrunnlag {
        return byggDummyForskuddGrunnlag("type")
    }

    fun byggForskuddGrunnlagUtenInnhold(): BeregnGrunnlag {
        return byggDummyForskuddGrunnlag("innhold")
    }

    // Bygger opp BeregnGrunnlag
    private fun byggDummyForskuddGrunnlag(nullVerdi: String): BeregnGrunnlag {
        val mapper = ObjectMapper()
        val beregnDatoFra = if (nullVerdi == "beregnDatoFra") null else LocalDate.parse("2017-01-01")
        val beregnDatoTil = if (nullVerdi == "beregnDatoTil") null else LocalDate.parse("2020-01-01")
        val referanse = if (nullVerdi == "referanse") null else "Mottatt_BM_Inntekt_AG_20201201"
        val type = if (nullVerdi == "type") null else GrunnlagType.INNTEKT
        val innhold =
            if (nullVerdi == "innhold") {
                null
            } else {
                mapper.valueToTree<JsonNode>(
                    Map.of(
                        "rolle", "BM",
                        "datoFom", "2017-01-01",
                        "datoTil", "2020-01-01",
                        "inntektType", "INNTEKTTYPE",
                        "belop", 290000
                    )
                )
            }
        val grunnlagListe =
            if (nullVerdi == "grunnlagListe") {
                null
            } else {
                listOf(
                    Grunnlag(
                        referanse = referanse,
                        type = type,
                        innhold = innhold
                    )
                )
            }
        return BeregnGrunnlag(beregnDatoFra = beregnDatoFra, beregnDatoTil = beregnDatoTil, grunnlagListe = grunnlagListe)
    }

    // Bygger opp fullt BeregnGrunnlag
    @JvmOverloads
    fun byggForskuddGrunnlag(
        datoFom: String = "2017-01-01",
        datoTom: String = "2020-01-01",
        fodselsdato: String = "2006-12-01",
        antall: String = "1.0",
        belop: String = "290000"
    ): BeregnGrunnlag {
        val mapper = ObjectMapper()
        val barnIHusstandInnhold = mapper.valueToTree<JsonNode>(
            Map.of(
                "datoFom",
                datoFom,
                "datoTil",
                datoTom,
                "antall",
                antall
            )
        )
        val soknadsbarnInnhold = mapper.valueToTree<JsonNode>(
            Map.of(
                "soknadsbarnId",
                1,
                "fodselsdato",
                fodselsdato
            )
        )
        val bostatusInnhold = mapper.valueToTree<JsonNode>(
            Map.of(
                "datoFom",
                datoFom,
                "datoTil",
                datoTom,
                "rolle",
                "SOKNADSBARN",
                "bostatusKode",
                "MED_FORELDRE"
            )
        )
        val inntektInnhold = mapper.valueToTree<JsonNode>(
            Map.of(
                "datoFom", datoFom,
                "datoTil", datoTom,
                "rolle", "BIDRAGSMOTTAKER",
                "inntektType", "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
                "belop", belop
            )
        )
        val sivilstandInnhold = mapper.valueToTree<JsonNode>(
            Map.of(
                "datoFom",
                datoFom,
                "datoTil",
                datoTom,
                "rolle",
                "BIDRAGSMOTTAKER",
                "sivilstandKode",
                "GIFT"
            )
        )

        val beregnDatoFra = LocalDate.parse("2017-01-01")
        val beregnDatoTil = LocalDate.parse("2020-01-01")
        val grunnlagListe = mutableListOf<Grunnlag>()

        grunnlagListe.add(Grunnlag(referanse = "Mottatt_BarnIHusstand", type = GrunnlagType.BARN_I_HUSSTAND, innhold = barnIHusstandInnhold))
        grunnlagListe.add(Grunnlag(referanse = "Mottatt_BarnIHusstand", type = GrunnlagType.SOKNADSBARN_INFO, innhold = soknadsbarnInnhold))
        grunnlagListe.add(Grunnlag(referanse = "Mottatt_Bostatus_20170101", type = GrunnlagType.BOSTATUS, innhold = bostatusInnhold))
        grunnlagListe.add(Grunnlag(referanse = "Mottatt_Inntekt_AG_20170101", type = GrunnlagType.INNTEKT, innhold = inntektInnhold))
        grunnlagListe.add(Grunnlag(referanse = "Mottatt_Sivilstand_20201201", type = GrunnlagType.SIVILSTAND, innhold = sivilstandInnhold))

        return BeregnGrunnlag(beregnDatoFra = beregnDatoFra, beregnDatoTil = beregnDatoTil, grunnlagListe = grunnlagListe)
    }

    // Bygger opp BeregnForskuddResultatCore
    fun dummyForskuddResultatCore(): BeregnetForskuddResultatCore {
        val beregnetForskuddPeriodeListe = mutableListOf<ResultatPeriodeCore>()
        beregnetForskuddPeriodeListe.add(
            ResultatPeriodeCore(
                periode = PeriodeCore(datoFom = LocalDate.parse("2017-01-01"), datoTil = LocalDate.parse("2019-01-01")),
                resultat = ResultatBeregningCore(
                    belop = BigDecimal.valueOf(100),
                    kode = ResultatKodeForskudd.FORHOYET_FORSKUDD_100_PROSENT.name,
                    regel = "REGEL 1"
                ),
                grunnlagReferanseListe = listOf(
                    INNTEKT_REFERANSE_1,
                    SIVILSTAND_REFERANSE_ENSLIG,
                    BARN_REFERANSE_1,
                    SOKNADBARN_REFERANSE,
                    BOSTATUS_REFERANSE_MED_FORELDRE_1
                )
            )
        )

        return BeregnetForskuddResultatCore(
            beregnetForskuddPeriodeListe = beregnetForskuddPeriodeListe,
            sjablonListe = emptyList(),
            avvikListe = emptyList()
        )
    }

    // Bygger opp BeregnForskuddResultatCore med avvik
    fun dummyForskuddResultatCoreMedAvvik(): BeregnetForskuddResultatCore {
        val avvikListe = mutableListOf<AvvikCore>()
        avvikListe.add(AvvikCore(avvikTekst = "beregnDatoFra kan ikke være null", avvikType = "NULL_VERDI_I_DATO"))
        avvikListe.add(
            AvvikCore(
                avvikTekst = "periodeDatoTil må være etter periodeDatoFra i bidragMottakInntektPeriodeListe: periodeDatoFra=2018-04-01, periodeDatoTil=2018-03-01",
                avvikType = "DATO_FRA_ETTER_DATO_TIL"
            )
        )

        return BeregnetForskuddResultatCore(beregnetForskuddPeriodeListe = emptyList(), sjablonListe = emptyList(), avvikListe = avvikListe)
    }

    // Bygger opp BeregnForskuddResultat
    fun dummyForskuddResultat(): BeregnetForskuddResultat {
        val beregnetForskuddPeriodeListe = mutableListOf<ResultatPeriode>()
        beregnetForskuddPeriodeListe.add(
            ResultatPeriode(
                periode = Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-01-01")),
                resultat = ResultatBeregning(
                    belop = BigDecimal.valueOf(100),
                    kode = ResultatKodeForskudd.FORHOYET_FORSKUDD_100_PROSENT,
                    regel = "REGEL 1"
                ),
                grunnlagReferanseListe = listOf(
                    INNTEKT_REFERANSE_1,
                    SIVILSTAND_REFERANSE_ENSLIG,
                    BARN_REFERANSE_1,
                    SOKNADBARN_REFERANSE,
                    BOSTATUS_REFERANSE_MED_FORELDRE_1
                )
            )
        )

        return BeregnetForskuddResultat(beregnetForskuddPeriodeListe = beregnetForskuddPeriodeListe, grunnlagListe = emptyList())
    }

    // Bygger opp liste av sjablonverdier
    fun dummySjablonSjablontallListe(): List<Sjablontall> {
        val sjablonSjablontallListe = mutableListOf<Sjablontall>()
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0005",
                datoFom = LocalDate.parse("2015-07-01"),
                datoTom = LocalDate.parse("2016-06-30"),
                verdi = BigDecimal.valueOf(1490)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0005",
                datoFom = LocalDate.parse("2016-07-01"),
                datoTom = LocalDate.parse("2017-06-30"),
                verdi = BigDecimal.valueOf(1530)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0005",
                datoFom = LocalDate.parse("2017-07-01"),
                datoTom = LocalDate.parse("2018-06-30"),
                verdi = BigDecimal.valueOf(1570)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0005",
                datoFom = LocalDate.parse("2018-07-01"),
                datoTom = LocalDate.parse("2019-06-30"),
                verdi = BigDecimal.valueOf(1600)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0005",
                datoFom = LocalDate.parse("2019-07-01"),
                datoTom = LocalDate.parse("2020-06-30"),
                verdi = BigDecimal.valueOf(1640)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0005",
                datoFom = LocalDate.parse("2020-07-01"),
                datoTom = LocalDate.parse("9999-12-31"),
                verdi = BigDecimal.valueOf(1670)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0013",
                datoFom = LocalDate.parse("2003-01-01"),
                datoTom = LocalDate.parse("9999-12-31"),
                verdi = BigDecimal.valueOf(320)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0033",
                datoFom = LocalDate.parse("2015-07-01"),
                datoTom = LocalDate.parse("2016-06-30"),
                verdi = BigDecimal.valueOf(241600)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0033",
                datoFom = LocalDate.parse("2016-07-01"),
                datoTom = LocalDate.parse("2017-06-30"),
                verdi = BigDecimal.valueOf(264200)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0033",
                datoFom = LocalDate.parse("2017-07-01"),
                datoTom = LocalDate.parse("2018-06-30"),
                verdi = BigDecimal.valueOf(271000)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0033",
                datoFom = LocalDate.parse("2018-07-01"),
                datoTom = LocalDate.parse("2019-06-30"),
                verdi = BigDecimal.valueOf(270200)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0033",
                datoFom = LocalDate.parse("2019-07-01"),
                datoTom = LocalDate.parse("2020-06-30"),
                verdi = BigDecimal.valueOf(277600)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0033",
                datoFom = LocalDate.parse("2020-07-01"),
                datoTom = LocalDate.parse("9999-12-31"),
                verdi = BigDecimal.valueOf(297500)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0034",
                datoFom = LocalDate.parse("2015-07-01"),
                datoTom = LocalDate.parse("2016-06-30"),
                verdi = BigDecimal.valueOf(370200)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0034",
                datoFom = LocalDate.parse("2016-07-01"),
                datoTom = LocalDate.parse("2017-06-30"),
                verdi = BigDecimal.valueOf(399100)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0034",
                datoFom = LocalDate.parse("2017-07-01"),
                datoTom = LocalDate.parse("2018-06-30"),
                verdi = BigDecimal.valueOf(408200)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0034",
                datoFom = LocalDate.parse("2018-07-01"),
                datoTom = LocalDate.parse("2019-06-30"),
                verdi = BigDecimal.valueOf(419700)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0034",
                datoFom = LocalDate.parse("2019-07-01"),
                datoTom = LocalDate.parse("2020-06-30"),
                verdi = BigDecimal.valueOf(430000)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0034",
                datoFom = LocalDate.parse("2020-07-01"),
                datoTom = LocalDate.parse("9999-12-31"),
                verdi = BigDecimal.valueOf(468500)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0035",
                datoFom = LocalDate.parse("2015-07-01"),
                datoTom = LocalDate.parse("2016-06-30"),
                verdi = BigDecimal.valueOf(314800)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0035",
                datoFom = LocalDate.parse("2016-07-01"),
                datoTom = LocalDate.parse("2017-06-30"),
                verdi = BigDecimal.valueOf(328700)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0035",
                datoFom = LocalDate.parse("2017-07-01"),
                datoTom = LocalDate.parse("2018-06-30"),
                verdi = BigDecimal.valueOf(335900)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0035",
                datoFom = LocalDate.parse("2018-07-01"),
                datoTom = LocalDate.parse("2019-06-30"),
                verdi = BigDecimal.valueOf(336500)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0035",
                datoFom = LocalDate.parse("2019-07-01"),
                datoTom = LocalDate.parse("2020-06-30"),
                verdi = BigDecimal.valueOf(344900)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0035",
                datoFom = LocalDate.parse("2020-07-01"),
                datoTom = LocalDate.parse("9999-12-31"),
                verdi = BigDecimal.valueOf(360800)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0036",
                datoFom = LocalDate.parse("2015-07-01"),
                datoTom = LocalDate.parse("2016-06-30"),
                verdi = BigDecimal.valueOf(58400)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0036",
                datoFom = LocalDate.parse("2016-07-01"),
                datoTom = LocalDate.parse("2017-06-30"),
                verdi = BigDecimal.valueOf(60200)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0036",
                datoFom = LocalDate.parse("2017-07-01"),
                datoTom = LocalDate.parse("2018-06-30"),
                verdi = BigDecimal.valueOf(61100)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0036",
                datoFom = LocalDate.parse("2018-07-01"),
                datoTom = LocalDate.parse("2019-06-30"),
                verdi = BigDecimal.valueOf(61700)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0036",
                datoFom = LocalDate.parse("2019-07-01"),
                datoTom = LocalDate.parse("2020-06-30"),
                verdi = BigDecimal.valueOf(62700)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0036",
                datoFom = LocalDate.parse("2020-07-01"),
                datoTom = LocalDate.parse("9999-12-31"),
                verdi = BigDecimal.valueOf(69100)
            )
        )

        // Ikke i bruk for forskudd
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0028",
                datoFom = LocalDate.parse("2015-07-01"),
                datoTom = LocalDate.parse("2016-06-30"),
                verdi = BigDecimal.valueOf(74250)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0028",
                datoFom = LocalDate.parse("2016-07-01"),
                datoTom = LocalDate.parse("2017-06-30"),
                verdi = BigDecimal.valueOf(76250)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0028",
                datoFom = LocalDate.parse("2017-07-01"),
                datoTom = LocalDate.parse("2018-06-30"),
                verdi = BigDecimal.valueOf(78300)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0028",
                datoFom = LocalDate.parse("2018-07-01"),
                datoTom = LocalDate.parse("2019-06-30"),
                verdi = BigDecimal.valueOf(54750)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0028",
                datoFom = LocalDate.parse("2019-07-01"),
                datoTom = LocalDate.parse("2020-06-30"),
                verdi = BigDecimal.valueOf(56550)
            )
        )
        sjablonSjablontallListe.add(
            Sjablontall(
                typeSjablon = "0028",
                datoFom = LocalDate.parse("2020-07-01"),
                datoTom = LocalDate.parse("9999-12-31"),
                verdi = BigDecimal.valueOf(51300)
            )
        )

        return sjablonSjablontallListe
    }
}
