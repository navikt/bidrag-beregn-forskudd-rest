package no.nav.bidrag.beregn.forskudd.dto.http

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import no.nav.bidrag.beregn.forskudd.dto.BeregnForskuddDto

@ApiModel(value = "Grunnlaget til en forskuddsberegning")
data class BeregnForskuddGrunnlag(
        @ApiModelProperty(value = "test prop") var test: String? = null
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