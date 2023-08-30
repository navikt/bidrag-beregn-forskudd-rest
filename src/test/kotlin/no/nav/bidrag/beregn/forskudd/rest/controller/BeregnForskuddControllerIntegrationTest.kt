package no.nav.bidrag.beregn.forskudd.rest.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.fail
import org.springframework.http.HttpStatus.OK
import java.nio.file.Files
import java.nio.file.Paths
import no.nav.bidrag.beregn.forskudd.rest.BidragBeregnForskuddTest
import no.nav.bidrag.beregn.forskudd.rest.BidragBeregnForskuddTest.Companion.TEST_PROFILE
import no.nav.bidrag.beregn.forskudd.rest.consumer.wiremock_stub.SjablonApiStub
import no.nav.bidrag.commons.web.test.HttpHeaderTestRestTemplate
import no.nav.bidrag.transport.beregning.forskudd.rest.response.BeregnetForskuddResultat
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [BidragBeregnForskuddTest::class], webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8096)
@EnableMockOAuth2Server
@ActiveProfiles(TEST_PROFILE)
internal class BeregnForskuddControllerIntegrationTest {
    @Autowired
    private val httpHeaderTestRestTemplate: HttpHeaderTestRestTemplate? = null

    @Autowired
    private val sjablonApiStub: SjablonApiStub? = null

    @LocalServerPort
    private val port = 0
    private var url: String? = null
    private var filnavn: String? = null
    private var forventetForskuddBelop: Int? = null
    private var forventetForskuddResultatkode: String? = null
    private var forventetForskuddRegel: String? = null

    /*
  Beskrivelse av regler

  REGEL 1
  Betingelse 1	Søknadsbarn alder er høyere enn eller lik 18 år
  Resultatkode	AVSLAG

  REGEL 2
  Betingelse 1	Søknadsbarn alder er høyere enn eller lik 11 år
  Betingelse 2	Søknadsbarn bostedsstatus er BOR_IKKE_MED_FORELDRE
  Resultatkode	FORHOYET_FORSKUDD_11_AAR_125_PROSENT

  REGEL 3
  Betingelse 1	Søknadsbarn alder er lavere enn 11 år
  Betingelse 2	Søknadsbarn bostedsstatus er BOR_IKKE_MED_FORELDRE
  Resultatkode	FORHOYET_FORSKUDD_100_PROSENT

  REGEL 4
  Betingelse 1	Bidragsmottakers inntekt er høyere enn 0005 x 0013
  Resultatkode	AVSLAG

  REGEL 5
  Betingelse 1	Bidragsmottakers inntekt er lavere enn eller lik 0033
  Betingelse 2	Søknadsbarn alder er høyere enn eller lik 11 år
  Resultatkode	FORHOYET_FORSKUDD_11_AAR_125_PROSENT

  REGEL 6
  Betingelse 1	Bidragsmottakers inntekt er lavere enn eller lik 0033
  Betingelse 2	Søknadsbarn alder er lavere enn 11 år
  Resultatkode	FORHOYET_FORSKUDD_100_PROSENT

  REGEL 7
  Betingelse 1	Bidragsmottakers inntekt er lavere enn eller lik 0034
  Betingelse 2	Bidragsmottakers sivilstand er ENSLIG
  Betingelse 3	Antall barn i husstand er 1
  Resultatkode	ORDINAERT_FORSKUDD_75_PROSENT

  REGEL 8
  Betingelse 1	Bidragsmottakers inntekt er høyere enn 0034
  Betingelse 2	Bidragsmottakers sivilstand er ENSLIG
  Betingelse 3	Antall barn i husstand er 1
  Resultatkode	REDUSERT_FORSKUDD_50_PROSENT

  REGEL 9
  Betingelse 1	Bidragsmottakers inntekt er lavere enn eller lik 0034 + (0036 x antall barn utover ett)
  Betingelse 2	Bidragsmottakers sivilstand er ENSLIG
  Betingelse 3	Antall barn i husstand er mer enn 1
  Resultatkode	ORDINAERT_FORSKUDD_75_PROSENT

  REGEL 10
  Betingelse 1	Bidragsmottakers inntekt er høyere enn 0034 + (0036 x antall barn utover ett)
  Betingelse 2	Bidragsmottakers sivilstand er ENSLIG
  Betingelse 3	Antall barn i husstand er mer enn 1
  Resultatkode	REDUSERT_FORSKUDD_50_PROSENT

  REGEL 11
  Betingelse 1	Bidragsmottakers inntekt er lavere enn eller lik 0035
  Betingelse 2	Bidragsmottakers sivilstand er GIFT
  Betingelse 3	Antall barn i husstand er 1
  Resultatkode	ORDINAERT_FORSKUDD_75_PROSENT

  REGEL 12
  Betingelse 1	Bidragsmottakers inntekt er høyere enn 0035
  Betingelse 2	Bidragsmottakers sivilstand er GIFT
  Betingelse 3	Antall barn i husstand er 1
  Resultatkode	REDUSERT_FORSKUDD_50_PROSENT

  REGEL 13
  Betingelse 1	Bidragsmottakers inntekt er lavere enn eller lik 0035 + (0036 x antall barn utover ett)
  Betingelse 2	Bidragsmottakers sivilstand er GIFT
  Betingelse 3	Antall barn i husstand er mer enn 1
  Resultatkode	ORDINAERT_FORSKUDD_75_PROSENT

  REGEL 14
  Betingelse 1	Bidragsmottakers inntekt er høyere enn 0035 + (0036 x antall barn utover ett)
  Betingelse 2	Bidragsmottakers sivilstand er GIFT
  Betingelse 3	Antall barn i husstand er mer enn 1
  Resultatkode	REDUSERT_FORSKUDD_50_PROSENT
  */
    @BeforeEach
    fun init() {
        // Sett opp wiremock mot sjablon-tjenestene
        sjablonApiStub?.settOppSjablonStub()

        // Bygg opp url
        url = "http://localhost:$port/beregn/forskudd"
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 1")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel01() {
        // Forhøyet forskudd ved 11 år: SB alder > 11 år; BM inntekt 290000; BM antall barn egen husstand 1; BM sivilstatus gift
        filnavn = "src/test/resources/testfiler/forskudd_eksempel1.json"
        forventetForskuddBelop = 2080
        forventetForskuddResultatkode = "FORHOYET_FORSKUDD_11_AAR_125_PROSENT"
        forventetForskuddRegel = "REGEL 5"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 2")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel02() {
        // Ordinært forskudd: SB alder > 11 år; BM inntekt 300000; BM antall barn egen husstand 1; BM sivilstatus gift
        filnavn = "src/test/resources/testfiler/forskudd_eksempel2.json"
        forventetForskuddBelop = 1250
        forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT"
        forventetForskuddRegel = "REGEL 11"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 3")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel03() {
        // Redusert forskudd: SB alder > 11 år; BM inntekt 370000; BM antall barn egen husstand 1; BM sivilstatus gift
        filnavn = "src/test/resources/testfiler/forskudd_eksempel3.json"
        forventetForskuddBelop = 830
        forventetForskuddResultatkode = "REDUSERT_FORSKUDD_50_PROSENT"
        forventetForskuddRegel = "REGEL 12"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 4")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel04() {
        // Ordinært forskudd: SB alder > 11 år; BM inntekt 370000; BM antall barn egen husstand 2; BM sivilstatus gift
        filnavn = "src/test/resources/testfiler/forskudd_eksempel4.json"
        forventetForskuddBelop = 1250
        forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT"
        forventetForskuddRegel = "REGEL 13"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 5")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel05() {
        // Redusert forskudd: SB alder > 11 år; BM inntekt 460000; BM antall barn egen husstand 1; BM sivilstatus gift
        filnavn = "src/test/resources/testfiler/forskudd_eksempel5.json"
        forventetForskuddBelop = 830
        forventetForskuddResultatkode = "REDUSERT_FORSKUDD_50_PROSENT"
        forventetForskuddRegel = "REGEL 12"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 6")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel06() {
        // Ordinært forskudd: SB alder > 11 år; BM inntekt 460000; BM antall barn egen husstand 1; BM sivilstatus enslig
        filnavn = "src/test/resources/testfiler/forskudd_eksempel6.json"
        forventetForskuddBelop = 1250
        forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT"
        forventetForskuddRegel = "REGEL 7"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 7")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel07() {
        // Ordinært forskudd: SB alder > 11 år; BM inntekt 460000; BM antall barn egen husstand 3; BM sivilstatus gift
        filnavn = "src/test/resources/testfiler/forskudd_eksempel7.json"
        forventetForskuddBelop = 1250
        forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT"
        forventetForskuddRegel = "REGEL 13"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 8")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel08() {
        // Ordinært forskudd: SB alder > 11 år; BM inntekt 460000; BM antall barn egen husstand 3; BM sivilstatus enslig
        filnavn = "src/test/resources/testfiler/forskudd_eksempel8.json"
        forventetForskuddBelop = 1250
        forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT"
        forventetForskuddRegel = "REGEL 9"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 9")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel09() {
        // Redusert forskudd: SB alder > 11 år; BM inntekt 530000; BM antall barn egen husstand 1; BM sivilstatus gift
        filnavn = "src/test/resources/testfiler/forskudd_eksempel9.json"
        forventetForskuddBelop = 830
        forventetForskuddResultatkode = "REDUSERT_FORSKUDD_50_PROSENT"
        forventetForskuddRegel = "REGEL 12"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 10")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel10() {
        // Avslag: SB alder > 11 år; BM inntekt 540000; BM antall barn egen husstand 1; BM sivilstatus enslig
        filnavn = "src/test/resources/testfiler/forskudd_eksempel10.json"
        forventetForskuddBelop = 0
        forventetForskuddResultatkode = "AVSLAG"
        forventetForskuddRegel = "REGEL 4"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 11")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel11() {
        // Avslag: SB alder > 11 år; BM inntekt 540000; BM antall barn egen husstand 1; BM sivilstatus gift
        filnavn = "src/test/resources/testfiler/forskudd_eksempel11.json"
        forventetForskuddBelop = 0
        forventetForskuddResultatkode = "AVSLAG"
        forventetForskuddRegel = "REGEL 4"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 12")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel12() {
        // Forhøyet forskudd: SB alder < 11 år; BM inntekt 290000; BM antall barn egen husstand 1; BM sivilstatus enslig
        filnavn = "src/test/resources/testfiler/forskudd_eksempel12.json"
        forventetForskuddBelop = 1670
        forventetForskuddResultatkode = "FORHOYET_FORSKUDD_100_PROSENT"
        forventetForskuddRegel = "REGEL 6"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 13")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel13() {
        // Ordinært forskudd: SB alder < 11 år; BM inntekt 290000+13000; BM antall barn egen husstand 1; BM sivilstatus enslig
        filnavn = "src/test/resources/testfiler/forskudd_eksempel13.json"
        forventetForskuddBelop = 1250
        forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT"
        forventetForskuddRegel = "REGEL 7"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 14")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel14() {
        // Redusert forskudd: SB alder < 11 år; BM inntekt 361000; BM antall barn egen husstand 1; BM sivilstatus gift
        filnavn = "src/test/resources/testfiler/forskudd_eksempel14.json"
        forventetForskuddBelop = 830
        forventetForskuddResultatkode = "REDUSERT_FORSKUDD_50_PROSENT"
        forventetForskuddRegel = "REGEL 12"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 15")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel15() {
        // Ordinært forskudd: SB alder < 11 år; BM inntekt 361000; BM antall barn egen husstand 1; BM sivilstatus enslig
        filnavn = "src/test/resources/testfiler/forskudd_eksempel15.json"
        forventetForskuddBelop = 1250
        forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT"
        forventetForskuddRegel = "REGEL 7"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 16")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel16() {
        // Ordinært forskudd: SB alder < 11 år; BM inntekt 468000; BM antall barn egen husstand 1; BM sivilstatus enslig
        filnavn = "src/test/resources/testfiler/forskudd_eksempel16.json"
        forventetForskuddBelop = 1250
        forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT"
        forventetForskuddRegel = "REGEL 7"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 17")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel17() {
        // Redusert forskudd: SB alder < 11 år; BM inntekt 468000; BM antall barn egen husstand 1; BM sivilstatus gift
        filnavn = "src/test/resources/testfiler/forskudd_eksempel17.json"
        forventetForskuddBelop = 830
        forventetForskuddResultatkode = "REDUSERT_FORSKUDD_50_PROSENT"
        forventetForskuddRegel = "REGEL 12"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 18")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel18() {
        // Ordinært forskudd: SB alder < 11 år; BM inntekt 429000; BM antall barn egen husstand 2; BM sivilstatus enslig
        filnavn = "src/test/resources/testfiler/forskudd_eksempel18.json"
        forventetForskuddBelop = 1250
        forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT"
        forventetForskuddRegel = "REGEL 9"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 19")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel19() {
        // Ordinært forskudd: SB alder < 11 år; BM inntekt 429000; BM antall barn egen husstand 2; BM sivilstatus gift
        filnavn = "src/test/resources/testfiler/forskudd_eksempel19.json"
        forventetForskuddBelop = 1250
        forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT"
        forventetForskuddRegel = "REGEL 13"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 20")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel20() {
        // Redusert forskudd: SB alder < 11 år; BM inntekt 430000; BM antall barn egen husstand 2; BM sivilstatus gift
        filnavn = "src/test/resources/testfiler/forskudd_eksempel20.json"
        forventetForskuddBelop = 830
        forventetForskuddResultatkode = "REDUSERT_FORSKUDD_50_PROSENT"
        forventetForskuddRegel = "REGEL 14"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 22")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel22() {
        // Avslag: SB alder < 11 år; BM inntekt 489000+60000; BM antall barn egen husstand 2; BM sivilstatus gift
        filnavn = "src/test/resources/testfiler/forskudd_eksempel22.json"
        forventetForskuddBelop = 0
        forventetForskuddResultatkode = "AVSLAG"
        forventetForskuddRegel = "REGEL 4"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal kalle core og returnere et resultat - eksempel 23")
    fun skalKalleCoreOgReturnereEtResultat_Eksempel23() {
        // Redusert forskudd: SB alder < 11 år; BM inntekt 489000; BM antall barn egen husstand 2; BM sivilstatus gift
        filnavn = "src/test/resources/testfiler/forskudd_eksempel23.json"
        forventetForskuddBelop = 830
        forventetForskuddResultatkode = "REDUSERT_FORSKUDD_50_PROSENT"
        forventetForskuddRegel = "REGEL 14"
        utfoerBeregningerOgEvaluerResultat()
    }

    @Test
    @DisplayName("skal feile med exception ved kall til core - eksempel 1")
    fun skalFeileMedExceptionVedKallTilCore_Eksempel01() {
        filnavn = "src/test/resources/testfiler/forskudd_eksempel1_med_feil_verdi_i_enum.json"
        val request: HttpEntity<String> = lesFilOgByggRequest(filnavn)

        // Kall rest-API for forskudd
        val responseEntity = httpHeaderTestRestTemplate?.exchange(url, HttpMethod.POST, request, BeregnetForskuddResultat::class.java)

        assertAll(
            Executable { assertThat(responseEntity?.statusCode).isEqualTo(HttpStatus.BAD_REQUEST) },
            Executable {
                assertThat(
                    responseEntity?.headers?.get("Error")?.stream()?.anyMatch { s -> s.contains("UgyldigInputException") }).isTrue()
            },
            Executable { assertThat(responseEntity?.headers?.get("Error")?.stream()?.anyMatch { s -> s.contains("BOR_MED_BESTEMOR") }).isTrue() }
        )
    }

    private fun utfoerBeregningerOgEvaluerResultat() {
        val request: HttpEntity<String> = lesFilOgByggRequest(filnavn)

        // Kall rest-API for forskudd
        val responseEntity = httpHeaderTestRestTemplate?.exchange(url, HttpMethod.POST, request, BeregnetForskuddResultat::class.java)
        val forskuddResultat = responseEntity?.body

        assertAll(
            Executable { assertThat(responseEntity?.statusCode).isEqualTo(OK) },
            Executable { assertThat(forskuddResultat).isNotNull() },  // Sjekk resultat av beregningnen
            Executable { assertThat(forskuddResultat?.beregnetForskuddPeriodeListe).isNotNull() },
            Executable { assertThat(forskuddResultat?.beregnetForskuddPeriodeListe).hasSize(1) },
            Executable { assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.resultat).isNotNull() },
            Executable {
                assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.resultat?.belop?.intValueExact())
                    .isEqualTo(forventetForskuddBelop)
            },
            Executable {
                assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.resultat?.kode)
                    .isEqualTo(forventetForskuddResultatkode)
            },
            Executable {
                assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.resultat?.regel)
                    .isEqualTo(forventetForskuddRegel)
            },
            Executable {
                assertThat(forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.grunnlagReferanseListe)?.size()?.isEqualTo(
                    forskuddResultat?.grunnlagListe?.size
                )
            },
            Executable {
                assertThat(
                    forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.grunnlagReferanseListe?.stream()?.filter
                    { grunnlagReferanse -> grunnlagReferanse.startsWith("Mottatt") }?.count() as Long
                )
                    .isEqualTo(request.body?.split("Mottatt".toRegex())?.size?.minus(1)?.toLong())
            },
            {
                assertThat(
                    forskuddResultat?.beregnetForskuddPeriodeListe?.get(0)?.grunnlagReferanseListe?.stream()?.filter
                    { grunnlagReferanse -> grunnlagReferanse.startsWith("Sjablon") }?.count() as Long
                )
                    .isEqualTo(7)
            }
        )
    }

    private fun lesFilOgByggRequest(filnavn: String?): HttpEntity<String> {
        var json = ""

        // Les inn fil med request-data (json)
        try {
            json = Files.readString(Paths.get(filnavn))
        } catch (e: Exception) {
            fail("Klarte ikke å lese fil: $filnavn")
        }

        // Lag request
        return initHttpEntity(json)
    }

    private fun <T> initHttpEntity(body: T): HttpEntity<T> {
        val httpHeaders = HttpHeaders()
        httpHeaders.setContentType(MediaType.APPLICATION_JSON)
        return HttpEntity(body, httpHeaders)
    }
}
