package no.nav.bidrag.beregn.forskudd.rest.consumer

import no.nav.bidrag.beregn.forskudd.rest.TestUtil
import no.nav.bidrag.beregn.forskudd.rest.exception.SjablonConsumerException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@ExtendWith(MockitoExtension::class)
@SuppressWarnings("unchecked")
@DisplayName("SjablonConsumerTest")
internal class SjablonConsumerTest {
    @InjectMocks
    private lateinit var sjablonConsumer: SjablonConsumer

    @Mock
    private lateinit var restTemplateMock: RestTemplate

    @Test
    @DisplayName("Skal hente liste av Sjablontall når respons fra tjenesten er OK")
    fun skalHenteListeAvSjablontallNaarResponsFraTjenestenErOk() {
        `when`(
            restTemplateMock.exchange(
                anyString(),
                eq(HttpMethod.GET),
                eq(null),
                any<ParameterizedTypeReference<List<Sjablontall>>>(),
            ),
        )
            .thenReturn(ResponseEntity(TestUtil.dummySjablonSjablontallListe(), HttpStatus.OK))

        val sjablonResponse = sjablonConsumer.hentSjablonSjablontall()

        assertAll(
            { assertThat(sjablonResponse).isNotNull() },
            { assertThat(sjablonResponse.responseEntity.statusCode).isNotNull() },
            { assertThat(sjablonResponse.responseEntity.statusCode).isEqualTo(HttpStatus.OK) },
            { assertThat(sjablonResponse.responseEntity.body).isNotNull() },
            { assertThat(sjablonResponse.responseEntity.body?.size).isEqualTo(TestUtil.dummySjablonSjablontallListe().size) },
            {
                assertThat(
                    sjablonResponse.responseEntity.body?.get(0)?.typeSjablon,
                ).isEqualTo(TestUtil.dummySjablonSjablontallListe()[0].typeSjablon)
            },
        )
    }

    @Test
    @DisplayName("Skal kaste SjablonConsumerException når respons fra tjenesten ikke er OK for Sjablontall")
    fun skalKasteRestClientExceptionNaarResponsFraTjenestenIkkeErOkForSjablontall() {
        `when`(restTemplateMock.exchange(anyString(), eq(HttpMethod.GET), eq(null), any<ParameterizedTypeReference<List<Sjablontall>>>()))
            .thenThrow(HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
        assertThatExceptionOfType(SjablonConsumerException::class.java).isThrownBy { sjablonConsumer.hentSjablonSjablontall() }
    }
}
