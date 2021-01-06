package no.nav.bidrag.beregn.forskudd.rest.dto.http

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.felles.dto.SjablonNavnVerdiCore
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddResultatCore
import no.nav.bidrag.beregn.forskudd.core.dto.BostatusPeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.InntektCore
import no.nav.bidrag.beregn.forskudd.core.dto.InntektPeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatBeregningCore
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatGrunnlagCore
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatPeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.SivilstandPeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.SoknadBarnCore
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException
import java.math.BigDecimal
import java.time.LocalDate


// Grunnlag
@ApiModel(value = "Grunnlaget for en forskuddsberegning")
data class BeregnForskuddGrunnlag(
    @ApiModelProperty(value = "Beregn forskudd fra-dato") var beregnDatoFra: LocalDate? = null,
    @ApiModelProperty(value = "Beregn forskudd til-dato") var beregnDatoTil: LocalDate? = null,
    @ApiModelProperty(value = "Informasjon om søknadsbarnet") var soknadBarn: SoknadBarn? = null,
    @ApiModelProperty(
        value = "Periodisert liste over bidragmottakers inntekter") val bidragMottakerInntektPeriodeListe: List<InntektPeriode>? = null,
    @ApiModelProperty(
        value = "Periodisert liste over bidragmottakers sivilstand") val bidragMottakerSivilstandPeriodeListe: List<SivilstandPeriode>? = null,
    @ApiModelProperty(
        value = "Periodisert liste over barn i bidragsmottakers husholdning") val bidragMottakerBarnPeriodeListe: List<Periode>? = null
) {
  fun tilCore() = BeregnForskuddGrunnlagCore(
      beregnDatoFra = if (beregnDatoFra != null) beregnDatoFra!! else throw UgyldigInputException("beregnDatoFra kan ikke være null"),
      beregnDatoTil = if (beregnDatoTil != null) beregnDatoTil!! else throw UgyldigInputException("beregnDatoTil kan ikke være null"),
      soknadBarn = if (soknadBarn != null) soknadBarn!!.tilCore() else throw UgyldigInputException("soknadBarn kan ikke være null"),

      bidragMottakerInntektPeriodeListe = if (bidragMottakerInntektPeriodeListe != null) bidragMottakerInntektPeriodeListe.map { it.tilCore() }
      else throw UgyldigInputException("bidragMottakerInntektPeriodeListe kan ikke være null"),

      bidragMottakerSivilstandPeriodeListe = if (bidragMottakerSivilstandPeriodeListe != null) bidragMottakerSivilstandPeriodeListe.map { it.tilCore() }
      else throw UgyldigInputException("bidragMottakerSivilstandPeriodeListe kan ikke være null"),

      bidragMottakerBarnPeriodeListe = if (bidragMottakerBarnPeriodeListe != null) bidragMottakerBarnPeriodeListe.map { it.tilCore() } else emptyList(),

      sjablonPeriodeListe = emptyList()
  )
}

@ApiModel(value = "Søknadsbarnets fødselsdato og bostatus")
data class SoknadBarn(
    @ApiModelProperty(value = "Søknadsbarnets fødselsdato") var soknadBarnFodselsdato: LocalDate? = null,
    @ApiModelProperty(value = "Periodisert liste over søknadsbarnets bostatus") val soknadBarnBostatusPeriodeListe: List<BostatusPeriode>? = null
) {
  fun tilCore() = SoknadBarnCore(
      soknadBarnFodselsdato = if (soknadBarnFodselsdato != null) soknadBarnFodselsdato!!
      else throw UgyldigInputException("soknadBarnFodselsdato kan ikke være null"),

      soknadBarnBostatusPeriodeListe = if (soknadBarnBostatusPeriodeListe != null) soknadBarnBostatusPeriodeListe.map { it.tilCore() }
      else throw UgyldigInputException("soknadBarnBostatusPeriodeListe kan ikke være null")
  )
}

@ApiModel(value = "Søknadsbarnets bostatus")
data class BostatusPeriode(
    @ApiModelProperty(value = "Søknadsbarnets bostatus fra-til-dato") var bostatusDatoFraTil: Periode? = null,
    @ApiModelProperty(value = "Søknadsbarnets bostatuskode") var bostatusKode: String? = null
) {
  fun tilCore() = BostatusPeriodeCore(
      bostatusDatoFraTil = if (bostatusDatoFraTil != null) bostatusDatoFraTil!!.tilCore() else throw UgyldigInputException(
          "bostatusDatoFraTil kan ikke være null"),
      bostatusKode = if (bostatusKode != null) bostatusKode!! else throw UgyldigInputException("bostatusKode kan ikke være null")
  )
}

@ApiModel(value = "Bidragsmottakers inntekt")
data class InntektPeriode(
    @ApiModelProperty(value = "Bidragsmottakers inntekt fra-til-dato") var inntektDatoFraTil: Periode? = null,
    @ApiModelProperty(value = "Bidragsmottakers inntekt type") var inntektType: String? = null,
    @ApiModelProperty(value = "Bidragsmottakers inntekt beløp") var inntektBelop: BigDecimal? = null
) {
  fun tilCore() = InntektPeriodeCore(
      inntektDatoFraTil = if (inntektDatoFraTil != null) inntektDatoFraTil!!.tilCore() else throw UgyldigInputException(
          "inntektDatoFraTil kan ikke være null"),
      inntektType = if (inntektType != null) inntektType!! else throw UgyldigInputException("inntektType kan ikke være null"),
      inntektBelop = if (inntektBelop != null) inntektBelop!! else throw UgyldigInputException("inntektBelop kan ikke være null")
  )
}

@ApiModel(value = "Bidragsmottakers sivilstand")
data class SivilstandPeriode(
    @ApiModelProperty(value = "Bidragsmottakers sivilstand fra-til-dato") var sivilstandDatoFraTil: Periode? = null,
    @ApiModelProperty(value = "Bidragsmottakers sivilstandkode") var sivilstandKode: String? = null
) {
  fun tilCore() = SivilstandPeriodeCore(
      sivilstandDatoFraTil = if (sivilstandDatoFraTil != null) sivilstandDatoFraTil!!.tilCore() else throw UgyldigInputException(
          "sivilstandDatoFraTil kan ikke være null"),
      sivilstandKode = if (sivilstandKode != null) sivilstandKode!! else throw UgyldigInputException("sivilstandKode kan ikke være null")
  )
}


// Resultat
@ApiModel(value = "Resultatet av en forskuddsberegning")
data class BeregnForskuddResultat(
    @ApiModelProperty(
        value = "Periodisert liste over resultat av forskuddsberegning") var resultatPeriodeListe: List<ResultatPeriode> = emptyList()
) {
  constructor(beregnForskuddResultat: BeregnForskuddResultatCore) : this(
      resultatPeriodeListe = beregnForskuddResultat.resultatPeriodeListe.map { ResultatPeriode(it) }
  )
}

@ApiModel(value = "Resultatet av en beregning for en gitt periode")
data class ResultatPeriode(
    @ApiModelProperty(value = "Beregning resultat fra-til-dato") var resultatDatoFraTil: Periode,
    @ApiModelProperty(value = "Beregning resultat innhold") var resultatBeregning: ResultatBeregning,
    @ApiModelProperty(value = "Beregning grunnlag innhold") var resultatGrunnlag: ResultatGrunnlag
) {
  constructor(resultatPeriode: ResultatPeriodeCore) : this(
      resultatDatoFraTil = Periode(resultatPeriode.resultatDatoFraTil),
      resultatBeregning = ResultatBeregning(resultatPeriode.resultatBeregning),
      resultatGrunnlag = ResultatGrunnlag(resultatPeriode.resultatGrunnlag)
  )
}

@ApiModel(value = "Resultatet av en beregning")
data class ResultatBeregning(
    @ApiModelProperty(value = "Resultatbeløp") var resultatBelop: BigDecimal,
    @ApiModelProperty(value = "Resultatkode") var resultatKode: String,
    @ApiModelProperty(value = "Resultatbeskrivelse") var resultatBeskrivelse: String
) {
  constructor(resultatBeregning: ResultatBeregningCore) : this(
      resultatBelop = resultatBeregning.resultatBelop,
      resultatKode = resultatBeregning.resultatKode,
      resultatBeskrivelse = resultatBeregning.resultatBeskrivelse
  )
}

@ApiModel(value = "Grunnlaget for en beregning")
data class ResultatGrunnlag(
    @ApiModelProperty(value = "Liste over bidragsmottakers inntekter") var bidragMottakerInntektListe: List<Inntekt> = emptyList(),
    @ApiModelProperty(value = "Bidragsmottakers sivilstand") var bidragMottakerSivilstandKode: String,
    @ApiModelProperty(value = "Antall barn i husstanden") var antallBarnIHusstand: Int,
    @ApiModelProperty(value = "Søknadsbarnets alder") var soknadBarnAlder: Int,
    @ApiModelProperty(value = "Søknadsbarnets bostatus") var soknadBarnBostatusKode: String,
    @ApiModelProperty(value = "Liste over sjablonperioder") var sjablonListe: List<Sjablon> = emptyList()
) {
  constructor(resultatGrunnlag: ResultatGrunnlagCore) : this(
      bidragMottakerInntektListe = resultatGrunnlag.bidragMottakerInntektListe.map { Inntekt(it) },
      bidragMottakerSivilstandKode = resultatGrunnlag.bidragMottakerSivilstandKode,
      antallBarnIHusstand = resultatGrunnlag.antallBarnIHusstand,
      soknadBarnAlder = resultatGrunnlag.soknadBarnAlder,
      soknadBarnBostatusKode = resultatGrunnlag.soknadBarnBostatusKode,
      sjablonListe = resultatGrunnlag.sjablonListe.map { Sjablon(it) }
  )
}

@ApiModel(value = "Inntekttype og -beløp")
data class Inntekt(
    @ApiModelProperty(value = "Inntekt type") var inntektType: String,
    @ApiModelProperty(value = "Inntekt beløp") var inntektBelop: BigDecimal
) {
  constructor(inntekt: InntektCore) : this(
      inntektType = inntekt.inntektType,
      inntektBelop = inntekt.inntektBelop
  )
}

@ApiModel(value = "Sjablonnavn og -verdi")
data class Sjablon(
    @ApiModelProperty(value = "Sjablonnavn") var sjablonNavn: String,
    @ApiModelProperty(value = "Sjablonverdi") var sjablonVerdi: BigDecimal
) {
  constructor(sjablon: SjablonNavnVerdiCore) : this(
      sjablonNavn = sjablon.sjablonNavn,
      sjablonVerdi = sjablon.sjablonVerdi
  )
}


// Felles
@ApiModel(value = "Periode (fra-til dato)")
data class Periode(
    @ApiModelProperty(value = "Fra-dato") var periodeDatoFra: LocalDate? = null,
    @ApiModelProperty(value = "Til-dato") var periodeDatoTil: LocalDate? = null
) {
  constructor(periode: PeriodeCore) : this(
      periodeDatoFra = periode.periodeDatoFra,
      periodeDatoTil = periode.periodeDatoTil
  )

  fun tilCore() = PeriodeCore(
      periodeDatoFra = if (periodeDatoFra != null) periodeDatoFra!! else throw UgyldigInputException("periodeDatoFra kan ikke være null"),
      periodeDatoTil = periodeDatoTil
  )
}
