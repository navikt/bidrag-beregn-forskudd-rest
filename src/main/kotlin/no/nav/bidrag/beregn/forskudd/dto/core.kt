package no.nav.bidrag.beregn.forskudd.dto

import java.math.BigDecimal
import java.time.LocalDate

data class BeregnForskuddDto(var beregnDatoFra: LocalDate? = null,
                             var beregnDatoTil: LocalDate? = null,
                             var soknadBarn: List<SoknadBarn> = emptyList(),
                             var bidragMottakerInntektPeriodeListe: List<BidragMottakerInntektPeriodeListe> = emptyList(),
                             var bidragMottakerSivilstandPeriodeListe: List<BidragMottakerSivilstandPeriodeListe> = emptyList(),
                             var bidragMottakerBarnPeriodeListe: List<BidragMottakerBarnPeriodeListe> = emptyList()
)

data class SoknadBarn(
        var soknadBarnFodselsdato: LocalDate? = null,
        var bostatusPeriode: List<BostatusPeriode> = emptyList()
)
data class BostatusPeriode(
        var datoFra: LocalDate? = null,
        var datoTil: LocalDate? = null,
        var bostedStatusKode: String? = null
)

data class BidragMottakerInntektPeriodeListe(
        var datoFra: LocalDate? = null,
        var datoTil: LocalDate? = null,
        var belop: BigDecimal? = null
)

data class BidragMottakerSivilstandPeriodeListe(
        var datoFra: LocalDate? = null,
        var datoTil: LocalDate? = null,
        var sivilstandKode: String? = null
)

data class BidragMottakerBarnPeriodeListe(
        var datoFra: LocalDate? = null,
        var datoTil: LocalDate? = null
)