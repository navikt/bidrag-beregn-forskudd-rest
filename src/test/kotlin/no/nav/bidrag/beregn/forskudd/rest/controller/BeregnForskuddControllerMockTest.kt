package no.nav.bidrag.beregn.forskudd.rest.controller

import no.nav.bidrag.beregn.forskudd.rest.BidragBeregnForskuddTest
import no.nav.bidrag.beregn.forskudd.rest.BidragBeregnForskuddTest.Companion.TEST_PROFILE
import no.nav.bidrag.beregn.forskudd.rest.TestUtil
import no.nav.bidrag.beregn.forskudd.rest.service.BeregnForskuddService
import no.nav.bidrag.commons.web.HttpResponse
import no.nav.bidrag.commons.web.test.HttpHeaderTestRestTemplate
import no.nav.bidrag.domene.enums.resultatkoder.ResultatKodeForskudd
import no.nav.bidrag.transport.behandling.beregning.felles.BeregnGrunnlag
import no.nav.bidrag.transport.behandling.beregning.forskudd.BeregnetForskuddResultat
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.YearMonth

@SpringBootTest(classes = [BidragBeregnForskuddTest::class], webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8096)
@ActiveProfiles(TEST_PROFILE)
@EnableMockOAuth2Server
@ExtendWith(SpringExtension::class)
internal class BeregnForskuddControllerMockTest {
    @Autowired
    private lateinit var httpHeaderTestRestTemplate: HttpHeaderTestRestTemplate

    @LocalServerPort
    private val port = 0

    @MockBean
    lateinit var beregnForskuddServiceMock: BeregnForskuddService

    @Test
    @DisplayName("Skal returnere forskudd resultat")
    fun skalReturnereForskuddResultat() {
        `when`(beregnForskuddServiceMock.beregn(any(BeregnGrunnlag::class.java)))
            .thenReturn(HttpResponse.from(HttpStatus.OK, TestUtil.dummyForskuddResultat()))

        val url = "http://localhost:$port/beregn/forskudd"
        val request = initHttpEntity(TestUtil.byggDummyForskuddGrunnlag())
        val responseEntity = httpHeaderTestRestTemplate.exchange(url, HttpMethod.POST, request, BeregnetForskuddResultat::class.java)
        val forskuddResultat = responseEntity.body
        assertAll(
            { assertThat(responseEntity.statusCode)?.isEqualTo(HttpStatus.OK) },
            { assertThat(forskuddResultat)?.isNotNull() },
            { assertThat(forskuddResultat?.beregnetForskuddPeriodeListe)?.isNotNull() },
            { assertThat(forskuddResultat?.beregnetForskuddPeriodeListe)?.hasSize(1) },
            {
                assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.periode?.fom)
                    .isEqualTo(YearMonth.parse("2017-01"))
            },
            {
                assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.periode?.til)
                    .isEqualTo(YearMonth.parse("2019-01"))
            },
            {
                assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.resultat?.belop?.toInt())
                    .isEqualTo(100)
            },
            {
                assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.resultat?.kode)
                    .isEqualTo(ResultatKodeForskudd.FORHOYET_FORSKUDD_100_PROSENT)
            },
            { assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.resultat?.regel).isEqualTo("REGEL 1") },
        )
    }

    @Test
    @DisplayName("Skal returnere 500 Internal Server Error når kall til servicen feiler")
    fun skalReturnere500InternalServerErrorNaarKallTilServicenFeiler() {
        `when`(beregnForskuddServiceMock.beregn(any(BeregnGrunnlag::class.java)))
            .thenReturn(HttpResponse.from(HttpStatus.INTERNAL_SERVER_ERROR, BeregnetForskuddResultat()))

        val url = "http://localhost:$port/beregn/forskudd"
        val request = initHttpEntity(TestUtil.byggDummyForskuddGrunnlag())
        val responseEntity = httpHeaderTestRestTemplate.exchange(url, HttpMethod.POST, request, BeregnetForskuddResultat::class.java)
        val forskuddResultat = responseEntity.body
        assertAll(
            { assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR) },
            { assertThat(forskuddResultat).isEqualTo(BeregnetForskuddResultat()) },
        )
    }

    private fun <T> initHttpEntity(body: T): HttpEntity<T> {
        val httpHeaders = HttpHeaders()
        return HttpEntity(body, httpHeaders)
    }

    companion object MockitoHelper {
        fun <T> any(type: Class<T>): T = Mockito.any(type)
    }
}
