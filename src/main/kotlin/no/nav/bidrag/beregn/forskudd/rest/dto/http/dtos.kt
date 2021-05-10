package no.nav.bidrag.beregn.forskudd.rest.dto.http

import io.swagger.v3.oas.annotations.media.Schema
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
@Schema(description = "Grunnlaget for en forskuddsberegning")
data class BeregnForskuddGrunnlag(
    @Schema(description = "Beregn forskudd fra-dato") var beregnDatoFra: LocalDate? = null,
    @Schema(description = "Beregn forskudd til-dato") var beregnDatoTil: LocalDate? = null,
    @Schema(description = "Informasjon om søknadsbarnet") var soknadBarn: SoknadBarn? = null,
    @Schema(description = "Periodisert liste over bidragmottakers inntekter") val bidragMottakerInntektPeriodeListe: List<InntektPeriode>? = null,
    @Schema(description = "Periodisert liste over bidragmottakers sivilstand") val bidragMottakerSivilstandPeriodeListe: List<SivilstandPeriode>? = null,
    @Schema(description = "Periodisert liste over barn i bidragsmottakers husholdning") val bidragMottakerBarnPeriodeListe: List<Periode>? = null
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

@Schema(description = "Søknadsbarnets fødselsdato og bostatus")
data class SoknadBarn(
    @Schema(description = "Søknadsbarnets fødselsdato") var soknadBarnFodselsdato: LocalDate? = null,
    @Schema(description = "Periodisert liste over søknadsbarnets bostatus") val soknadBarnBostatusPeriodeListe: List<BostatusPeriode>? = null
) {
    fun tilCore() = SoknadBarnCore(
        soknadBarnFodselsdato = if (soknadBarnFodselsdato != null) soknadBarnFodselsdato!!
        else throw UgyldigInputException("soknadBarnFodselsdato kan ikke være null"),

        soknadBarnBostatusPeriodeListe = if (soknadBarnBostatusPeriodeListe != null) soknadBarnBostatusPeriodeListe.map { it.tilCore() }
        else throw UgyldigInputException("soknadBarnBostatusPeriodeListe kan ikke være null")
    )
}

@Schema(description = "Søknadsbarnets bostatus")
data class BostatusPeriode(
    @Schema(description = "Søknadsbarnets bostatus fra-til-dato") var bostatusDatoFraTil: Periode? = null,
    @Schema(description = "Søknadsbarnets bostatuskode") var bostatusKode: String? = null
) {
    fun tilCore() = BostatusPeriodeCore(
        bostatusDatoFraTil = if (bostatusDatoFraTil != null) bostatusDatoFraTil!!.tilCore() else throw UgyldigInputException(
            "bostatusDatoFraTil kan ikke være null"
        ),
        bostatusKode = if (bostatusKode != null) bostatusKode!! else throw UgyldigInputException("bostatusKode kan ikke være null")
    )
}

@Schema(description = "Bidragsmottakers inntekt")
data class InntektPeriode(
    @Schema(description = "Bidragsmottakers inntekt fra-til-dato") var inntektDatoFraTil: Periode? = null,
    @Schema(description = "Bidragsmottakers inntekt type") var inntektType: String? = null,
    @Schema(description = "Bidragsmottakers inntekt beløp") var inntektBelop: BigDecimal? = null
) {
    fun tilCore() = InntektPeriodeCore(
        inntektDatoFraTil = if (inntektDatoFraTil != null) inntektDatoFraTil!!.tilCore() else throw UgyldigInputException(
            "inntektDatoFraTil kan ikke være null"
        ),
        inntektType = if (inntektType != null) inntektType!! else throw UgyldigInputException("inntektType kan ikke være null"),
        inntektBelop = if (inntektBelop != null) inntektBelop!! else throw UgyldigInputException("inntektBelop kan ikke være null")
    )
}

@Schema(description = "Bidragsmottakers sivilstand")
data class SivilstandPeriode(
    @Schema(description = "Bidragsmottakers sivilstand fra-til-dato") var sivilstandDatoFraTil: Periode? = null,
    @Schema(description = "Bidragsmottakers sivilstandkode") var sivilstandKode: String? = null
) {
    fun tilCore() = SivilstandPeriodeCore(
        sivilstandDatoFraTil = if (sivilstandDatoFraTil != null) sivilstandDatoFraTil!!.tilCore() else throw UgyldigInputException(
            "sivilstandDatoFraTil kan ikke være null"
        ),
        sivilstandKode = if (sivilstandKode != null) sivilstandKode!! else throw UgyldigInputException("sivilstandKode kan ikke være null")
    )
}


// Resultat
@Schema(description = "Resultatet av en forskuddsberegning")
data class BeregnForskuddResultat(
    @Schema(
        description = "Periodisert liste over resultat av forskuddsberegning"
    ) var resultatPeriodeListe: List<ResultatPeriode> = emptyList()
) {
    constructor(beregnForskuddResultat: BeregnForskuddResultatCore) : this(
        resultatPeriodeListe = beregnForskuddResultat.resultatPeriodeListe.map { ResultatPeriode(it) }
    )
}

@Schema(description = "Resultatet av en beregning for en gitt periode")
data class ResultatPeriode(
    @Schema(description = "Beregning resultat fra-til-dato") var resultatDatoFraTil: Periode = Periode(),
    @Schema(description = "Beregning resultat innhold") var resultatBeregning: ResultatBeregning = ResultatBeregning(),
    @Schema(description = "Beregning grunnlag innhold") var resultatGrunnlag: ResultatGrunnlag = ResultatGrunnlag()
) {
    constructor(resultatPeriode: ResultatPeriodeCore) : this(
        resultatDatoFraTil = Periode(resultatPeriode.resultatDatoFraTil),
        resultatBeregning = ResultatBeregning(resultatPeriode.resultatBeregning),
        resultatGrunnlag = ResultatGrunnlag(resultatPeriode.resultatGrunnlag)
    )
}

@Schema(description = "Resultatet av en beregning")
data class ResultatBeregning(
    @Schema(description = "Resultatbeløp") var resultatBelop: Int = 0,
    @Schema(description = "Resultatkode") var resultatKode: String = "",
    @Schema(description = "Resultatbeskrivelse") var resultatBeskrivelse: String = ""
) {
    constructor(resultatBeregning: ResultatBeregningCore) : this(
        resultatBelop = resultatBeregning.resultatBelop.intValueExact(),
        resultatKode = resultatBeregning.resultatKode,
        resultatBeskrivelse = resultatBeregning.resultatBeskrivelse
    )
}

@Schema(description = "Grunnlaget for en beregning")
data class ResultatGrunnlag(
    @Schema(description = "Liste over bidragsmottakers inntekter") var bidragMottakerInntektListe: List<Inntekt> = emptyList(),
    @Schema(description = "Bidragsmottakers sivilstand") var bidragMottakerSivilstandKode: String = "",
    @Schema(description = "Antall barn i husstanden") var antallBarnIHusstand: Int = 0,
    @Schema(description = "Søknadsbarnets alder") var soknadBarnAlder: Int = 0,
    @Schema(description = "Søknadsbarnets bostatus") var soknadBarnBostatusKode: String = "",
    @Schema(description = "Liste over sjablonperioder") var sjablonListe: List<Sjablon> = emptyList()
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

@Schema(description = "Inntekttype og -beløp")
data class Inntekt(
    @Schema(description = "Inntekt type") var inntektType: String = "",
    @Schema(description = "Inntekt beløp") var inntektBelop: BigDecimal = BigDecimal.ZERO
) {
    constructor(inntekt: InntektCore) : this(
        inntektType = inntekt.inntektType,
        inntektBelop = inntekt.inntektBelop
    )
}

@Schema(description = "Sjablonnavn og -verdi")
data class Sjablon(
    @Schema(description = "Sjablonnavn") var sjablonNavn: String = "",
    @Schema(description = "Sjablonverdi") var sjablonVerdi: BigDecimal = BigDecimal.ZERO
) {
    constructor(sjablon: SjablonNavnVerdiCore) : this(
        sjablonNavn = sjablon.sjablonNavn,
        sjablonVerdi = sjablon.sjablonVerdi
    )
}


// Felles
@Schema(description = "Periode (fra-til dato)")
data class Periode(
    @Schema(description = "Fra-dato") var periodeDatoFra: LocalDate? = null,
    @Schema(description = "Til-dato") var periodeDatoTil: LocalDate? = null
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
