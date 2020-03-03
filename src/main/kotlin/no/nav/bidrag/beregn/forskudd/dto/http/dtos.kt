package no.nav.bidrag.beregn.forskudd.dto.http

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import no.nav.bidrag.beregn.forskudd.dto.BeregnForskuddGrunnlagDto
import java.math.BigDecimal
import java.time.LocalDate

@ApiModel(value = "Grunnlaget for en forskuddsberegning")
data class BeregnForskuddGrunnlag(
    @ApiModelProperty(value = "Beregn forskudd fra-dato") var beregnDatoFra: LocalDate? = null,
    @ApiModelProperty(value = "Beregn forskudd til-dato") var beregnDatoTil: LocalDate? = null,
    @ApiModelProperty(value = "Søknadsbarnets fødselsdato og liste over bostatus") var soknadBarn: List<SoknadBarn> = emptyList(),
    @ApiModelProperty(value = "Periodisert liste over bidragmottakers inntekter") var bidragMottakerInntektPeriodeListe: List<BidragMottakerInntektPeriodeListe> = emptyList(),
    @ApiModelProperty(value = "Periodisert liste over bidragmottakers inntekter") var bidragMottakerSivilstandPeriodeListe: List<BidragMottakerSivilstandPeriodeListe> = emptyList(),
    @ApiModelProperty(value = "Periodisert liste over barn i bidragsmottakers husholdning") var bidragMottakerBarnPeriodeListe: List<BidragMottakerBarnPeriodeListe?> = emptyList()
) {
  fun hentCore() = BeregnForskuddGrunnlagDto(
      beregnDatoFra = beregnDatoFra,
      beregnDatoTil = beregnDatoTil,
      soknadBarn = soknadBarn.map { it.hentCore() },
      bidragMottakerInntektPeriodeListe = bidragMottakerInntektPeriodeListe.map { it.hentCore() },
      bidragMottakerSivilstandPeriodeListe = bidragMottakerSivilstandPeriodeListe.map { it.hentCore() },
      bidragMottakerBarnPeriodeListe = bidragMottakerBarnPeriodeListe.map { it?.hentCore() }
  )
}

@ApiModel(value = "Søknadsbarnets fødselsdato og bostatus")
data class SoknadBarn(
    @ApiModelProperty(value = "Søknadsbarnets fødselsdato") var soknadBarnFodselsdato: LocalDate? = null,
    @ApiModelProperty(value = "Periodisert liste over søknadsbarnets bostatus") var bostatusPeriode: List<BostatusPeriode?> = emptyList()
) {
  fun hentCore() = no.nav.bidrag.beregn.forskudd.dto.SoknadBarn(
      soknadBarnFodselsdato = soknadBarnFodselsdato,
      bostatusPeriode = bostatusPeriode.map { it?.hentCore() }
  )
}

@ApiModel(value = "Periodisert liste over søknadsbarnets bostatus")
data class BostatusPeriode(
    @ApiModelProperty(value = "Bostatus fra-dato") var datoFra: LocalDate? = null,
    @ApiModelProperty(value = "Bostatus til-dato") var datoTil: LocalDate? = null,
    @ApiModelProperty(value = "Bostedsstatuskode") var bostedStatusKode: String? = null
) {
  fun hentCore() = no.nav.bidrag.beregn.forskudd.dto.BostatusPeriode(
      datoFra = datoFra,
      datoTil = datoTil,
      bostedStatusKode = bostedStatusKode
  )
}

@ApiModel(value = "Periodisert liste over bidragsmottakers inntekt")
data class BidragMottakerInntektPeriodeListe(
    @ApiModelProperty(value = "Bidragsmottaker inntekt fra-dato") var datoFra: LocalDate? = null,
    @ApiModelProperty(value = "Bidragsmottaker inntekt til-dato") var datoTil: LocalDate? = null,
    @ApiModelProperty(value = "Bidragsmottaker inntekt") var belop: BigDecimal? = null
) {
  fun hentCore() = no.nav.bidrag.beregn.forskudd.dto.BidragMottakerInntektPeriodeListe(
      datoFra = datoFra,
      datoTil = datoTil,
      belop = belop
  )
}

@ApiModel(value = "Periodisert liste over bidragsmottakers sivilstand")
data class BidragMottakerSivilstandPeriodeListe(
    @ApiModelProperty(value = "Sivilstand fra-dato") var datoFra: LocalDate? = null,
    @ApiModelProperty(value = "Sivilstand til-dato") var datoTil: LocalDate? = null,
    @ApiModelProperty(value = "Sivilstand") var sivilstandKode: String? = null
) {
  fun hentCore() = no.nav.bidrag.beregn.forskudd.dto.BidragMottakerSivilstandPeriodeListe(
      datoFra = datoFra,
      datoTil = datoTil,
      sivilstandKode = sivilstandKode
  )
}

@ApiModel(value = "Periodisert liste over barn i bidragsmottakers husholdning")
data class BidragMottakerBarnPeriodeListe(
    @ApiModelProperty(value = "Barn i husholdning fra-dato") var datoFra: LocalDate? = null,
    @ApiModelProperty(value = "Barn i husholdning til-dato") var datoTil: LocalDate? = null
) {
  fun hentCore() = no.nav.bidrag.beregn.forskudd.dto.BidragMottakerBarnPeriodeListe(
      datoFra = datoFra,
      datoTil = datoTil
  )
}

@ApiModel(value = "Resultatet av en forskuddsberegning")
data class BeregnForskuddResultat(
    @ApiModelProperty(value = "Periodisert liste over resultat av forskuddsberegning") var bidragPeriodeResultatListe: List<BidragPeriodeResultat?> = emptyList()
) {
//  fun fraCore() = BeregnForskuddResultatDto(
//      beregnDatoFra = beregnDatoFra,
//      beregnDatoTil = beregnDatoTil,
//      soknadBarn = soknadBarn.map { it.hentCore() },
//      bidragMottakerInntektPeriodeListe = bidragMottakerInntektPeriodeListe.map { it.hentCore() },
//      bidragMottakerSivilstandPeriodeListe = bidragMottakerSivilstandPeriodeListe.map { it.hentCore() },
//      bidragMottakerBarnPeriodeListe = bidragMottakerBarnPeriodeListe.map { it?.hentCore() }
}

@ApiModel(value = "Periodisert liste over barn i bidragsmottakers husholdning")
data class BidragPeriodeResultat(
    @ApiModelProperty(value = "Periode fra-dato") var datoFra: LocalDate? = null,
    @ApiModelProperty(value = "Periode til-dato") var datoTil: LocalDate? = null,
    @ApiModelProperty(value = "Beregning resultat") var forskuddBeregningResultat: ForskuddBeregningResultat? = null
) {
//  fun hentCore() = no.nav.bidrag.beregn.forskudd.dto.BidragMottakerBarnPeriodeListe(
//      datoFra = datoFra,
//      datoTil = datoTil
}

@ApiModel(value = "Periodisert liste over barn i bidragsmottakers husholdning")
data class ForskuddBeregningResultat(
    @ApiModelProperty(value = "Beløp") var belop: BigDecimal? = null,
    @ApiModelProperty(value = "Resultatkode") var resultatKode: String? = null,
    @ApiModelProperty(value = "Resultatbeskrivelse") var resultatBeskrivelse: String? = null
) {
//  fun hentCore() = no.nav.bidrag.beregn.forskudd.dto.BidragMottakerBarnPeriodeListe(
//      datoFra = datoFra,
//      datoTil = datoTil
}
