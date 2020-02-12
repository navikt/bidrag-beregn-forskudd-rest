package no.nav.bidrag.beregning.forskudd.dto.http

import no.nav.bidrag.beregning.forskudd.dto.BeregnForskuddDto

data class BeregnForskuddGrunnlag(
        var test: String? = null
) {
    fun hentCore(): BeregnForskuddDto {
        val beregnForskuddDto = BeregnForskuddDto(test = "svada", svada = "lada")
        beregnForskuddDto.test  = test
        return beregnForskuddDto
    }
}

data class BeregnForskuddResultat(
        var test: String? = null
)