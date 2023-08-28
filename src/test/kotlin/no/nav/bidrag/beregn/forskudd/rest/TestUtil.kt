package no.nav.bidrag.beregn.forskudd.rest

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.beregn.felles.dto.AvvikCore
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnetForskuddResultatCore
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatBeregningCore
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatPeriodeCore
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddGrunnlag
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnetForskuddResultat
import no.nav.bidrag.beregn.forskudd.rest.dto.http.Grunnlag
import no.nav.bidrag.beregn.forskudd.rest.dto.http.Periode
import no.nav.bidrag.beregn.forskudd.rest.dto.http.ResultatBeregning
import no.nav.bidrag.beregn.forskudd.rest.dto.http.ResultatPeriode
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Map

object TestUtil {
    private const val INNTEKT_REFERANSE_1 = "INNTEKT_REFERANSE_1"
    private const val SIVILSTAND_REFERANSE_ENSLIG = "SIVILSTAND_REFERANSE_ENSLIG"
    private const val BARN_REFERANSE_1 = "BARN_REFERANSE_1"
    private const val SOKNADBARN_REFERANSE = "SOKNADBARN_REFERANSE"
    private const val BOSTATUS_REFERANSE_MED_FORELDRE_1 = "BOSTATUS_REFERANSE_MED_FORELDRE_1"
    fun byggDummyForskuddGrunnlag(): BeregnForskuddGrunnlag {
        return byggDummyForskuddGrunnlag("")
    }

    fun byggForskuddGrunnlagUtenBeregnDatoFra(): BeregnForskuddGrunnlag {
        return byggDummyForskuddGrunnlag("beregnDatoFra")
    }

    fun byggForskuddGrunnlagUtenBeregnDatoTil(): BeregnForskuddGrunnlag {
        return byggDummyForskuddGrunnlag("beregnDatoTil")
    }

    fun byggForskuddGrunnlagUtenGrunnlagListe(): BeregnForskuddGrunnlag {
        return byggDummyForskuddGrunnlag("grunnlagListe")
    }

    fun byggForskuddGrunnlagUtenReferanse(): BeregnForskuddGrunnlag {
        return byggDummyForskuddGrunnlag("referanse")
    }

    fun byggForskuddGrunnlagUtenType(): BeregnForskuddGrunnlag {
        return byggDummyForskuddGrunnlag("type")
    }

    fun byggForskuddGrunnlagUtenInnhold(): BeregnForskuddGrunnlag {
        return byggDummyForskuddGrunnlag("innhold")
    }

    // Bygger opp BeregnForskuddGrunnlag
    private fun byggDummyForskuddGrunnlag(nullVerdi: String): BeregnForskuddGrunnlag {
        val mapper = ObjectMapper()
        val beregnDatoFra = if (nullVerdi == "beregnDatoFra") null else LocalDate.parse("2017-01-01")
        val beregnDatoTil = if (nullVerdi == "beregnDatoTil") null else LocalDate.parse("2020-01-01")
        val referanse = if (nullVerdi == "referanse") null else "Mottatt_BM_Inntekt_AG_20201201"
        val type = if (nullVerdi == "type") null else "Inntekt"
        val innhold = if (nullVerdi == "innhold") null else mapper.valueToTree<JsonNode>(
            Map.of(
                "rolle", "BM",
                "datoFom", "2017-01-01",
                "datoTil", "2020-01-01",
                "inntektType", "INNTEKTTYPE",
                "belop", 290000
            )
        )
        val grunnlagListe: List<Grunnlag>?
        grunnlagListe = if (nullVerdi == "grunnlagListe") {
            null
        } else {
            listOf(
                Grunnlag(
                    referanse,
                    type,
                    innhold
                )
            )
        }
        return BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, grunnlagListe)
    }

    // Bygger opp fullt BeregnForskuddGrunnlag
    @JvmOverloads
    fun byggForskuddGrunnlag(
        datoFom: String = "2017-01-01",
        datoTom: String = "2020-01-01",
        fodselsdato: String? = "2006-12-01",
        antall: String = "1.0",
        belop: String = "290000"
    ): BeregnForskuddGrunnlag {
        val mapper = ObjectMapper()
        val barnIHusstandInnhold = mapper.valueToTree<JsonNode>(
            Map.of(
                "datoFom", datoFom,
                "datoTil", datoTom,
                "antall", antall
            )
        )
        val soknadsbarnInnhold = mapper.valueToTree<JsonNode>(
            Map.of(
                "soknadsbarnId", 1,
                "fodselsdato", fodselsdato
            )
        )
        val bostatusInnhold = mapper.valueToTree<JsonNode>(
            Map.of(
                "datoFom", datoFom,
                "datoTil", datoTom,
                "rolle", "SOKNADSBARN",
                "bostatusKode", "MED_FORELDRE"
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
                "datoFom", datoFom,
                "datoTil", datoTom,
                "rolle", "BIDRAGSMOTTAKER",
                "sivilstandKode", "GIFT"
            )
        )
        val beregnDatoFra = LocalDate.parse("2017-01-01")
        val beregnDatoTil = LocalDate.parse("2020-01-01")
        val grunnlagListe: MutableList<Grunnlag> = ArrayList()
        grunnlagListe.add(Grunnlag("Mottatt_BarnIHusstand", "BARN_I_HUSSTAND", barnIHusstandInnhold))
        grunnlagListe.add(Grunnlag("Mottatt_BarnIHusstand", "SOKNADSBARN_INFO", soknadsbarnInnhold))
        grunnlagListe.add(Grunnlag("Mottatt_Bostatus_20170101", "BOSTATUS", bostatusInnhold))
        grunnlagListe.add(Grunnlag("Mottatt_Inntekt_AG_20170101", "INNTEKT", inntektInnhold))
        grunnlagListe.add(Grunnlag("Mottatt_Sivilstand_20201201", "SIVILSTAND", sivilstandInnhold))
        return BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, grunnlagListe)
    }

    // Bygger opp BeregnForskuddResultatCore
    fun dummyForskuddResultatCore(): BeregnetForskuddResultatCore {
        val beregnetForskuddPeriodeListe = ArrayList<ResultatPeriodeCore>()
        beregnetForskuddPeriodeListe.add(
            ResultatPeriodeCore(
                PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-01-01")),
                ResultatBeregningCore(BigDecimal.valueOf(100), "INNVILGET_100_PROSENT", "REGEL 1"),
                java.util.List.of(
                    INNTEKT_REFERANSE_1,
                    SIVILSTAND_REFERANSE_ENSLIG,
                    BARN_REFERANSE_1,
                    SOKNADBARN_REFERANSE,
                    BOSTATUS_REFERANSE_MED_FORELDRE_1
                )
            )
        )
        return BeregnetForskuddResultatCore(beregnetForskuddPeriodeListe, emptyList(), emptyList())
    }

    // Bygger opp BeregnForskuddResultatCore med avvik
    fun dummyForskuddResultatCoreMedAvvik(): BeregnetForskuddResultatCore {
        val avvikListe = ArrayList<AvvikCore>()
        avvikListe.add(AvvikCore("beregnDatoFra kan ikke være null", "NULL_VERDI_I_DATO"))
        avvikListe.add(
            AvvikCore(
                "periodeDatoTil må være etter periodeDatoFra i bidragMottakInntektPeriodeListe: periodeDatoFra=2018-04-01, periodeDatoTil=2018-03-01",
                "DATO_FRA_ETTER_DATO_TIL"
            )
        )
        return BeregnetForskuddResultatCore(emptyList(), emptyList(), avvikListe)
    }

    // Bygger opp BeregnForskuddResultat
    fun dummyForskuddResultat(): BeregnetForskuddResultat {
        val beregnetForskuddPeriodeListe = ArrayList<ResultatPeriode>()
        beregnetForskuddPeriodeListe.add(
            ResultatPeriode(
                Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-01-01")),
                ResultatBeregning(BigDecimal.valueOf(100), "INNVILGET_100_PROSENT", "REGEL 1"),
                java.util.List.of(
                    INNTEKT_REFERANSE_1,
                    SIVILSTAND_REFERANSE_ENSLIG,
                    BARN_REFERANSE_1,
                    SOKNADBARN_REFERANSE,
                    BOSTATUS_REFERANSE_MED_FORELDRE_1
                )
            )
        )
        return BeregnetForskuddResultat(beregnetForskuddPeriodeListe, emptyList())
    }

    // Bygger opp liste av sjablonverdier
    fun dummySjablonSjablontallListe(): List<Sjablontall> {
        val sjablonSjablontallListe = ArrayList<Sjablontall>()
        sjablonSjablontallListe.add(Sjablontall("0005", LocalDate.parse("2015-07-01"), LocalDate.parse("2016-06-30"), BigDecimal.valueOf(1490)))
        sjablonSjablontallListe.add(Sjablontall("0005", LocalDate.parse("2016-07-01"), LocalDate.parse("2017-06-30"), BigDecimal.valueOf(1530)))
        sjablonSjablontallListe.add(Sjablontall("0005", LocalDate.parse("2017-07-01"), LocalDate.parse("2018-06-30"), BigDecimal.valueOf(1570)))
        sjablonSjablontallListe.add(Sjablontall("0005", LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30"), BigDecimal.valueOf(1600)))
        sjablonSjablontallListe.add(Sjablontall("0005", LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30"), BigDecimal.valueOf(1640)))
        sjablonSjablontallListe.add(Sjablontall("0005", LocalDate.parse("2020-07-01"), LocalDate.parse("9999-12-31"), BigDecimal.valueOf(1670)))
        sjablonSjablontallListe.add(Sjablontall("0013", LocalDate.parse("2003-01-01"), LocalDate.parse("9999-12-31"), BigDecimal.valueOf(320)))
        sjablonSjablontallListe.add(Sjablontall("0033", LocalDate.parse("2015-07-01"), LocalDate.parse("2016-06-30"), BigDecimal.valueOf(241600)))
        sjablonSjablontallListe.add(Sjablontall("0033", LocalDate.parse("2016-07-01"), LocalDate.parse("2017-06-30"), BigDecimal.valueOf(264200)))
        sjablonSjablontallListe.add(Sjablontall("0033", LocalDate.parse("2017-07-01"), LocalDate.parse("2018-06-30"), BigDecimal.valueOf(271000)))
        sjablonSjablontallListe.add(Sjablontall("0033", LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30"), BigDecimal.valueOf(270200)))
        sjablonSjablontallListe.add(Sjablontall("0033", LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30"), BigDecimal.valueOf(277600)))
        sjablonSjablontallListe.add(Sjablontall("0033", LocalDate.parse("2020-07-01"), LocalDate.parse("9999-12-31"), BigDecimal.valueOf(297500)))
        sjablonSjablontallListe.add(Sjablontall("0034", LocalDate.parse("2015-07-01"), LocalDate.parse("2016-06-30"), BigDecimal.valueOf(370200)))
        sjablonSjablontallListe.add(Sjablontall("0034", LocalDate.parse("2016-07-01"), LocalDate.parse("2017-06-30"), BigDecimal.valueOf(399100)))
        sjablonSjablontallListe.add(Sjablontall("0034", LocalDate.parse("2017-07-01"), LocalDate.parse("2018-06-30"), BigDecimal.valueOf(408200)))
        sjablonSjablontallListe.add(Sjablontall("0034", LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30"), BigDecimal.valueOf(419700)))
        sjablonSjablontallListe.add(Sjablontall("0034", LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30"), BigDecimal.valueOf(430000)))
        sjablonSjablontallListe.add(Sjablontall("0034", LocalDate.parse("2020-07-01"), LocalDate.parse("9999-12-31"), BigDecimal.valueOf(468500)))
        sjablonSjablontallListe.add(Sjablontall("0035", LocalDate.parse("2015-07-01"), LocalDate.parse("2016-06-30"), BigDecimal.valueOf(314800)))
        sjablonSjablontallListe.add(Sjablontall("0035", LocalDate.parse("2016-07-01"), LocalDate.parse("2017-06-30"), BigDecimal.valueOf(328700)))
        sjablonSjablontallListe.add(Sjablontall("0035", LocalDate.parse("2017-07-01"), LocalDate.parse("2018-06-30"), BigDecimal.valueOf(335900)))
        sjablonSjablontallListe.add(Sjablontall("0035", LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30"), BigDecimal.valueOf(336500)))
        sjablonSjablontallListe.add(Sjablontall("0035", LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30"), BigDecimal.valueOf(344900)))
        sjablonSjablontallListe.add(Sjablontall("0035", LocalDate.parse("2020-07-01"), LocalDate.parse("9999-12-31"), BigDecimal.valueOf(360800)))
        sjablonSjablontallListe.add(Sjablontall("0036", LocalDate.parse("2015-07-01"), LocalDate.parse("2016-06-30"), BigDecimal.valueOf(58400)))
        sjablonSjablontallListe.add(Sjablontall("0036", LocalDate.parse("2016-07-01"), LocalDate.parse("2017-06-30"), BigDecimal.valueOf(60200)))
        sjablonSjablontallListe.add(Sjablontall("0036", LocalDate.parse("2017-07-01"), LocalDate.parse("2018-06-30"), BigDecimal.valueOf(61100)))
        sjablonSjablontallListe.add(Sjablontall("0036", LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30"), BigDecimal.valueOf(61700)))
        sjablonSjablontallListe.add(Sjablontall("0036", LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30"), BigDecimal.valueOf(62700)))
        sjablonSjablontallListe.add(Sjablontall("0036", LocalDate.parse("2020-07-01"), LocalDate.parse("9999-12-31"), BigDecimal.valueOf(69100)))

        // Ikke i bruk for forskudd
        sjablonSjablontallListe.add(Sjablontall("0028", LocalDate.parse("2015-07-01"), LocalDate.parse("2016-06-30"), BigDecimal.valueOf(74250)))
        sjablonSjablontallListe.add(Sjablontall("0028", LocalDate.parse("2016-07-01"), LocalDate.parse("2017-06-30"), BigDecimal.valueOf(76250)))
        sjablonSjablontallListe.add(Sjablontall("0028", LocalDate.parse("2017-07-01"), LocalDate.parse("2018-06-30"), BigDecimal.valueOf(78300)))
        sjablonSjablontallListe.add(Sjablontall("0028", LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30"), BigDecimal.valueOf(54750)))
        sjablonSjablontallListe.add(Sjablontall("0028", LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30"), BigDecimal.valueOf(56550)))
        sjablonSjablontallListe.add(Sjablontall("0028", LocalDate.parse("2020-07-01"), LocalDate.parse("9999-12-31"), BigDecimal.valueOf(51300)))
        return sjablonSjablontallListe
    }
}
