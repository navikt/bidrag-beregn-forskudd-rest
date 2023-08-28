package no.nav.bidrag.beregn.forskudd.rest.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.OK
import java.time.LocalDate
import no.nav.bidrag.beregn.forskudd.rest.BidragBeregnForskuddTest
import no.nav.bidrag.beregn.forskudd.rest.TestUtil
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddGrunnlag
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnetForskuddResultat
import no.nav.bidrag.beregn.forskudd.rest.service.BeregnForskuddService
import no.nav.bidrag.commons.web.HttpResponse
import no.nav.bidrag.commons.web.test.HttpHeaderTestRestTemplate
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

@DisplayName("BeregnForskuddControllerTest")
@SpringBootTest(classes = [BidragBeregnForskuddTest::class], webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8096)
internal class BeregnForskuddControllerMockTest {
    @Autowired
    private val httpHeaderTestRestTemplate: HttpHeaderTestRestTemplate? = null

    @LocalServerPort
    private val port = 0

    @MockBean
    private val beregnForskuddServiceMock: BeregnForskuddService? = null
    @Test
    @DisplayName("Skal returnere forskudd resultat")
    fun skalReturnereForskuddResultat() {
        `when`(beregnForskuddServiceMock?.beregn(any(BeregnForskuddGrunnlag::class.java)))
            .thenReturn(HttpResponse.Companion.from(OK, TestUtil.dummyForskuddResultat()))
        val url = "http://localhost:$port/beregn/forskudd"
        val request = initHttpEntity(TestUtil.byggDummyForskuddGrunnlag())
        val responseEntity = httpHeaderTestRestTemplate?.exchange(url, HttpMethod.POST, request, BeregnetForskuddResultat::class.java)
        val forskuddResultat = responseEntity?.body
        assertAll(
            { assertThat(responseEntity?.statusCode)?.isEqualTo(OK) },
            { assertThat(forskuddResultat)?.isNotNull() },
            { assertThat(forskuddResultat?.beregnetForskuddPeriodeListe)?.isNotNull() },
            { assertThat(forskuddResultat?.beregnetForskuddPeriodeListe)?.hasSize(1) },
            {
                assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.periode?.datoFom)
                    .isEqualTo(LocalDate.parse("2017-01-01"))
            },
            {
                assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.periode?.datoTil)
                    .isEqualTo(LocalDate.parse("2019-01-01"))
            },
            {
                assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.resultat?.belop?.toInt())
                    .isEqualTo(100)
            },
            {
                assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.resultat?.kode)
                    .isEqualTo("INNVILGET_100_PROSENT")
            },
            { assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.resultat?.regel).isEqualTo("REGEL 1") }
        )
    }

    @Test
    @DisplayName("Skal returnere 500 Internal Server Error når kall til servicen feiler")
    fun skalReturnere500InternalServerErrorNaarKallTilServicenFeiler() {
        `when`(beregnForskuddServiceMock?.beregn(any(BeregnForskuddGrunnlag::class.java))).thenReturn(
            HttpResponse.Companion.from(
                INTERNAL_SERVER_ERROR,
                BeregnetForskuddResultat()
            )
        )
        val url = "http://localhost:$port/beregn/forskudd"
        val request = initHttpEntity(TestUtil.byggDummyForskuddGrunnlag())
        val responseEntity = httpHeaderTestRestTemplate?.exchange(url, HttpMethod.POST, request, BeregnetForskuddResultat::class.java)
        val forskuddResultat = responseEntity?.body
        assertAll(
            { assertThat(responseEntity?.statusCode).isEqualTo(INTERNAL_SERVER_ERROR) },
            { assertThat(forskuddResultat).isNull() }
        )
    }

    private fun <T> initHttpEntity(body: T): HttpEntity<T> {
        val httpHeaders = HttpHeaders()
        return HttpEntity(body, httpHeaders)
    }
}
