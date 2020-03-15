package no.nav.bidrag.beregn.forskudd.rest.dto.http

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import no.nav.bidrag.beregn.forskudd.core.dto.*
import java.math.BigDecimal
import java.time.LocalDate

@ApiModel(value = "Grunnlaget for en forskuddsberegning")
data class BeregnForskuddGrunnlag(
    @ApiModelProperty(value = "Beregn forskudd fra-dato") var beregnDatoFra: LocalDate? = null,
    @ApiModelProperty(value = "Beregn forskudd til-dato") var beregnDatoTil: LocalDate? = null,
    @ApiModelProperty(value = "Søknadsbarnets fødselsdato og liste over bostatus") var soknadBarn: SoknadBarn? = null,
    @ApiModelProperty(value = "Periodisert liste over bidragmottakers inntekter") var bidragMottakerInntektPeriodeListe: List<BidragMottakerInntektPeriode> = emptyList(),
    @ApiModelProperty(value = "Periodisert liste over bidragmottakers inntekter") var bidragMottakerSivilstandPeriodeListe: List<BidragMottakerSivilstandPeriode> = emptyList(),
    @ApiModelProperty(value = "Periodisert liste over barn i bidragsmottakers husholdning") var bidragMottakerBarnPeriodeListe: List<Periode?> = emptyList()
) {
  fun tilCore() = ForskuddPeriodeGrunnlagDto(
      beregnDatoFra = beregnDatoFra,
      beregnDatoTil = beregnDatoTil,
      soknadBarn = soknadBarn?.tilCore(),
      bidragMottakerInntektPeriodeListe = bidragMottakerInntektPeriodeListe.map { it.tilCore() },
      bidragMottakerSivilstandPeriodeListe = bidragMottakerSivilstandPeriodeListe.map { it.tilCore() },
      bidragMottakerBarnPeriodeListe = bidragMottakerBarnPeriodeListe.map { it?.tilCore() }
  )
}

@ApiModel(value = "Søknadsbarnets fødselsdato og bostatus")
data class SoknadBarn(
    @ApiModelProperty(value = "Søknadsbarn fødselsdato") var soknadBarnFodselsdato: LocalDate? = null,
    @ApiModelProperty(value = "Periodisert liste over søknadsbarnets bostatus") var bostatusPeriode: List<BostatusPeriode?> = emptyList()
) {
  fun tilCore() = SoknadBarnDto(
      soknadBarnFodselsdato = soknadBarnFodselsdato,
      bostatusPeriode = bostatusPeriode.map { it?.tilCore() }
  )
}

@ApiModel(value = "Søknadsbarnets bostatus")
data class BostatusPeriode(
    @ApiModelProperty(value = "Søknadsbarn bostedstatus fra-til-dato") var datoFraTil: Periode? = null,
    @ApiModelProperty(value = "Søknadsbarn bostedsstatuskode") var bostedStatusKode: String? = null
) {
  fun tilCore() = BostatusPeriodeDto(
      datoFraTil = datoFraTil?.tilCore(),
      bostedStatusKode = bostedStatusKode
  )
}

@ApiModel(value = "Bidragsmottakers inntekt")
data class BidragMottakerInntektPeriode(
    @ApiModelProperty(value = "Bidragsmottaker inntekt fra-til-dato") var datoFraTil: Periode? = null,
    @ApiModelProperty(value = "Bidragsmottaker inntekt") var belop: BigDecimal? = null
) {
  fun tilCore() = BidragMottakerInntektPeriodeDto(
      datoFraTil = datoFraTil?.tilCore(),
      belop = belop
  )
}

@ApiModel(value = "Bidragsmottakers sivilstand")
data class BidragMottakerSivilstandPeriode(
    @ApiModelProperty(value = "Bidragsmottaker sivilstand fra-til-dato") var datoFraTil: Periode? = null,
    @ApiModelProperty(value = "Bidragsmottaker sivilstand") var sivilstandKode: String? = null
) {
  fun tilCore() = BidragMottakerSivilstandPeriodeDto(
      datoFraTil = datoFraTil?.tilCore(),
      sivilstandKode = sivilstandKode
  )
}

@ApiModel(value = "Periode (fra-til dato)")
data class Periode(
    @ApiModelProperty(value = "Fra-dato") var datoFra: LocalDate? = null,
    @ApiModelProperty(value = "Til-dato") var datoTil: LocalDate? = null
) {
  fun tilCore() = PeriodeDto(
      datoFra = datoFra,
      datoTil = datoTil
  )
}


// Resultat
@ApiModel(value = "Liste med resultat av en forskuddsberegning")
data class BeregnForskuddResultat(
    @ApiModelProperty(value = "Periodisert liste over resultat av forskuddsberegning") var periodeResultatListe: List<BidragPeriodeResultat> = emptyList()
) {
  constructor(forskuddPeriodeResultatDto: ForskuddPeriodeResultatDto) : this(
      periodeResultatListe = forskuddPeriodeResultatDto.periodeResultatListe.map { BidragPeriodeResultat(it) }
  )
}

@ApiModel(value = "Perioderesultat av forskuddsberegning")
data class BidragPeriodeResultat(
    @ApiModelProperty(value = "Beregning resultat fra-til-dato") var datoFraTil: ResultatPeriode? = null,
    @ApiModelProperty(value = "Beregning resultat") var forskuddBeregningResultat: ForskuddBeregningResultat
) {
  constructor(periodeResultatDto: PeriodeResultatDto) : this(
      datoFraTil = ResultatPeriode(periodeResultatDto.datoFraTil),
      forskuddBeregningResultat = ForskuddBeregningResultat(periodeResultatDto.forskuddBeregningResultat)
  )
}

@ApiModel(value = "Periode (fra-til dato)")
data class ResultatPeriode(
    @ApiModelProperty(value = "Fra-dato") var datoFra: LocalDate? = null,
    @ApiModelProperty(value = "Til-dato") var datoTil: LocalDate? = null
) {
  constructor(periodeDto : PeriodeDto?) : this (
      datoFra = periodeDto?.datoFra,
      datoTil = periodeDto?.datoTil
  )
}

@ApiModel(value = "Beregning resultat beløp, resultatkode og beskrivelse")
data class ForskuddBeregningResultat(
    @ApiModelProperty(value = "Beløp") var belop: BigDecimal? = null,
    @ApiModelProperty(value = "Resultatkode") var resultatKode: String? = null,
    @ApiModelProperty(value = "Resultatbeskrivelse") var resultatBeskrivelse: String? = null
) {
  constructor(forskuddBeregningResultatDto: ForskuddBeregningResultatDto) : this(
      belop = forskuddBeregningResultatDto.belop,
      resultatKode = forskuddBeregningResultatDto.resultatKode,
      resultatBeskrivelse = forskuddBeregningResultatDto.resultatBeskrivelse
  )
}