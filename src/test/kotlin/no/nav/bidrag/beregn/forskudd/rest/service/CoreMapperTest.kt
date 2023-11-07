package no.nav.bidrag.beregn.forskudd.rest.service

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException
import no.nav.bidrag.domene.enums.Grunnlagstype
import no.nav.bidrag.transport.behandling.beregning.felles.BeregnGrunnlag
import no.nav.bidrag.transport.behandling.beregning.felles.Grunnlag
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class CoreMapperTest {
    @Test
    @Disabled // Gir null pointer exception i BeregnGrunnlag.hentInnholdBasertPå..... Bør vurdere å ha flere kontroller?
    @DisplayName("Skal kaste UgyldigInputException når fødselsdato er null")
    fun mapFødselsdatoNull() {
        val mapper = ObjectMapper()
        val innhold = mapper.readTree("{\"ident\": \"11111111111\"," + "\"navn\": \"Søknadsbarn\"," + "\"fødselsdato\": null}")
        val beregnForskuddGrunnlag = BeregnGrunnlag(
            grunnlagListe = listOf(
                Grunnlag(
                    navn = "Søknadsbarn_referanse",
                    type = Grunnlagstype.PERSON,
                    grunnlagsreferanseListe = emptyList(),
                    innhold = innhold
                )
            )
        )

        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java)
            .isThrownBy { CoreMapper.mapGrunnlagTilCore(beregnForskuddGrunnlag, emptyList()) }
            .withMessage("fødselsdato mangler i objekt av type SOKNADSBARN_INFO")
    }

    @Test
    @Disabled
    @DisplayName("Skal kaste UgyldigInputException når fodselsdato ikke er spesifisiert")
    fun mapFodselsdatoEmpty() {
        val mapper = ObjectMapper()
        val innhold = mapper.readTree("{}")
        val beregnForskuddGrunnlag = BeregnGrunnlag(
            grunnlagListe = listOf(Grunnlag("123", Grunnlagstype.SOKNADSBARN_INFO, innhold = innhold))
        )

        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java)
            .isThrownBy { CoreMapper.mapGrunnlagTilCore(beregnForskuddGrunnlag, emptyList()) }
            .withMessage("fødselsdato mangler i objekt av type SOKNADSBARN_INFO")
    }
}
