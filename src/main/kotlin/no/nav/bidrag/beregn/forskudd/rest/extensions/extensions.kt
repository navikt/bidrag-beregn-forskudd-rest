package no.nav.bidrag.beregn.forskudd.rest.extensions

import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException
import no.nav.bidrag.transport.beregning.forskudd.rest.request.BeregnForskuddGrunnlag
import no.nav.bidrag.transport.beregning.forskudd.rest.request.Grunnlag

fun BeregnForskuddGrunnlag.validerBeregnForskuddGrunnlag() {
    if (beregnDatoFra == null) throw UgyldigInputException("beregnDatoFra kan ikke være null")
    if (beregnDatoTil == null) throw UgyldigInputException("beregnDatoTil kan ikke være null")
    grunnlagListe?.map { it.validerGrunnlag() } ?: throw UgyldigInputException("grunnlagListe kan ikke være null")
}

fun Grunnlag.validerGrunnlag() {
    if (referanse == null) throw UgyldigInputException("referanse kan ikke være null")
    if (type == null) throw UgyldigInputException("type kan ikke være null")
    if (innhold == null) throw UgyldigInputException("innhold kan ikke være null")
}