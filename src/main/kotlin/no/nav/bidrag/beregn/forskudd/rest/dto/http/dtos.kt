package no.nav.bidrag.beregn.forskudd.rest.dto.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.media.Schema
import no.nav.bidrag.beregn.felles.dto.PeriodeCore
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnetForskuddResultatCore
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatBeregningCore
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatPeriodeCore
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException
import java.math.BigDecimal
import java.time.LocalDate


// Grunnlag
@Schema(description = "Grunnlaget for en forskuddsberegning")
data class BeregnForskuddGrunnlag(
  @Schema(description = "Beregn forskudd fra-dato") val beregnDatoFra: LocalDate? = null,
  @Schema(description = "Beregn forskudd til-dato") val beregnDatoTil: LocalDate? = null,
  @Schema(description = "Periodisert liste over grunnlagselementer") val grunnlagListe: List<Grunnlag>? = null
) {

  fun valider() {
    if (beregnDatoFra == null) throw UgyldigInputException("beregnDatoFra kan ikke være null")
    if (beregnDatoTil == null) throw UgyldigInputException("beregnDatoTil kan ikke være null")
    if (grunnlagListe != null) grunnlagListe.map { it.valider() } else throw UgyldigInputException("grunnlagListe kan ikke være null")
  }
}

@Schema(description = "Grunnlag")
data class Grunnlag(
  @Schema(description = "Referanse") val referanse: String? = null,
  @Schema(description = "Type") val type: String? = null,
  @Schema(description = "Innhold") val innhold: JsonNode? = null
) {

  fun valider() {
    if (referanse == null) throw UgyldigInputException("referanse kan ikke være null")
    if (type == null) throw UgyldigInputException("type kan ikke være null")
    if (innhold == null) throw UgyldigInputException("innhold kan ikke være null")
  }
}

// Resultat
@Schema(description = "Resultatet av en forskuddsberegning")
data class BeregnetForskuddResultat(
  @Schema(description = "Periodisert liste over resultat av forskuddsberegning") var beregnetForskuddPeriodeListe: List<ResultatPeriode> = emptyList(),
  @Schema(description = "Liste over grunnlag brukt i beregning") var grunnlagListe: List<ResultatGrunnlag> = emptyList()
) {

  constructor(beregnetForskuddResultat: BeregnetForskuddResultatCore, grunnlagListe: List<ResultatGrunnlag>) : this(
    beregnetForskuddPeriodeListe = beregnetForskuddResultat.beregnetForskuddPeriodeListe.map { ResultatPeriode(it) },
    grunnlagListe = grunnlagListe
  )
}

@Schema(description = "Resultatet av en beregning for en gitt periode")
data class ResultatPeriode(
  @Schema(description = "Beregnet resultat periode") var periode: Periode = Periode(),
  @Schema(description = "Beregnet resultat innhold") var resultat: ResultatBeregning = ResultatBeregning(),
  @Schema(description = "Beregnet grunnlag innhold") var grunnlagReferanseListe: List<String> = emptyList()
) {

  constructor(resultatPeriode: ResultatPeriodeCore) : this(
    periode = Periode(resultatPeriode.periode),
    resultat = ResultatBeregning(resultatPeriode.resultat),
    grunnlagReferanseListe = resultatPeriode.grunnlagReferanseListe
  )
}

@Schema(description = "Resultatet av en beregning")
data class ResultatBeregning(
  @Schema(description = "Resultat beløp") var belop: BigDecimal = BigDecimal.ZERO,
  @Schema(description = "Resultat kode") var kode: String = "",
  @Schema(description = "Resultat regel") var regel: String = ""
) {

  constructor(resultatBeregning: ResultatBeregningCore) : this(
    belop = resultatBeregning.belop,
    kode = resultatBeregning.kode,
    regel = resultatBeregning.regel
  )
}

@Schema(description = "Grunnlaget for en beregning")
data class ResultatGrunnlag(
  @Schema(description = "Referanse") val referanse: String = "",
  @Schema(description = "Type") val type: String = "",
  @Schema(description = "Innhold") val innhold: JsonNode = ObjectMapper().createObjectNode()
)

// Felles
@Schema(description = "Periode (fra-til dato")
data class Periode(
  @Schema(description = "Fra-og-med-dato") var datoFom: LocalDate? = null,
  @Schema(description = "Til-dato") var datoTil: LocalDate? = null
) {

  constructor(periode: PeriodeCore) : this(
    datoFom = periode.datoFom,
    datoTil = periode.datoTil
  )

  fun valider() {
    if (datoFom == null) throw UgyldigInputException("datoFom kan ikke være null")
  }
}
