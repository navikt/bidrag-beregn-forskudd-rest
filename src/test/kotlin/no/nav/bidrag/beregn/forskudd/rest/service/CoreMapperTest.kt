package no.nav.bidrag.beregn.forskudd.rest.service

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException
import no.nav.bidrag.domain.enums.GrunnlagType
import no.nav.bidrag.transport.beregning.felles.BeregnGrunnlag
import no.nav.bidrag.transport.beregning.felles.Grunnlag
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal  class CoreMapperTest {
    @Test
    @DisplayName("Skal kaste UgyldigInputException når fodselsdato er null")
    fun mapFodselsdatoNull() {
        val mapper = ObjectMapper()
        val innhold = mapper.readTree("{\"fodselsdato\": null}")
        val beregnForskuddGrunnlag = BeregnGrunnlag(
            grunnlagListe = listOf(Grunnlag("123", GrunnlagType.SOKNADSBARN_INFO, innhold = innhold))
        )

        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java).isThrownBy { CoreMapper.mapGrunnlagTilCore(beregnForskuddGrunnlag, emptyList()) }
            .withMessage("fødselsdato mangler i objekt av type SOKNADSBARN_INFO")
    }

    @Test
    @DisplayName("Skal kaste UgyldigInputException når fodselsdato ikke er spesifisiert")
    fun mapFodselsdatoEmpty() {
        val mapper = ObjectMapper()
        val innhold = mapper.readTree("{}")
        val beregnForskuddGrunnlag = BeregnGrunnlag(
            grunnlagListe = listOf(Grunnlag("123", GrunnlagType.SOKNADSBARN_INFO, innhold = innhold))
        )

        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java).isThrownBy { CoreMapper.mapGrunnlagTilCore(beregnForskuddGrunnlag, emptyList()) }
            .withMessage("fødselsdato mangler i objekt av type SOKNADSBARN_INFO")
    }
}