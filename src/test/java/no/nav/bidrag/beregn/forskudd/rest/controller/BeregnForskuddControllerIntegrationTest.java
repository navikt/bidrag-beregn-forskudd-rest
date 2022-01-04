package no.nav.bidrag.beregn.forskudd.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.http.HttpStatus.OK;

import java.nio.file.Files;
import java.nio.file.Paths;
import no.nav.bidrag.beregn.forskudd.rest.BidragBeregnForskuddTest;
import no.nav.bidrag.beregn.forskudd.rest.BidragBeregnForskuddOverridesConfig;
import no.nav.bidrag.beregn.forskudd.rest.consumer.wiremock_stub.SjablonApiStub;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnetForskuddResultat;
import no.nav.bidrag.commons.web.test.HttpHeaderTestRestTemplate;
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = BidragBeregnForskuddTest.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(BidragBeregnForskuddOverridesConfig.class)
@AutoConfigureWireMock(port = 8096)
@EnableMockOAuth2Server
public class BeregnForskuddControllerIntegrationTest {

  @Autowired
  private HttpHeaderTestRestTemplate httpHeaderTestRestTemplate;

  @Autowired
  private SjablonApiStub sjablonApiStub;

  @LocalServerPort
  private int port;

  private String url;
  private String filnavn;

  private Integer forventetForskuddBelop;
  private String forventetForskuddResultatkode;
  private String forventetForskuddRegel;

  /*
  Beskrivelse av regler

  REGEL 1
  Betingelse 1	Søknadsbarn alder er høyere enn eller lik 18 år
  Resultatkode	AVSLAG

  REGEL 2
  Betingelse 1	Søknadsbarn alder er høyere enn eller lik 11 år
  Betingelse 2	Søknadsbarn bostedsstatus er ENSLIG_ASYLANT
  Resultatkode	FORSKUDD_ENSLIG_ASYLANT_11_AAR_250_PROSENT

  REGEL 3
  Betingelse 1	Søknadsbarn alder er lavere enn 11 år
  Betingelse 2	Søknadsbarn bostedsstatus er ENSLIG_ASYLANT
  Resultatkode	FORSKUDD_ENSLIG_ASYLANT_200_PROSENT

  REGEL 4
  Betingelse 1	Søknadsbarn alder er høyere enn eller lik 11 år
  Betingelse 2	Søknadsbarn bostedsstatus er ALENE eller MED_ANDRE_ENN_FORELDRE
  Resultatkode	FORHOYET_FORSKUDD_11_AAR_125_PROSENT

  REGEL 5
  Betingelse 1	Søknadsbarn alder er lavere enn 11 år
  Betingelse 2	Søknadsbarn bostedsstatus er ALENE eller MED_ANDRE_ENN_FORELDRE
  Resultatkode	FORHOYET_FORSKUDD_100_PROSENT

  REGEL 6
  Betingelse 1	Bidragsmottakers inntekt er høyere enn 0005 x 0013
  Resultatkode	AVSLAG

  REGEL 7
  Betingelse 1	Bidragsmottakers inntekt er lavere enn eller lik 0033
  Betingelse 2	Søknadsbarn alder er høyere enn eller lik 11 år
  Resultatkode	FORHOYET_FORSKUDD_11_AAR_125_PROSENT

  REGEL 8
  Betingelse 1	Bidragsmottakers inntekt er lavere enn eller lik 0033
  Betingelse 2	Søknadsbarn alder er lavere enn 11 år
  Resultatkode	FORHOYET_FORSKUDD_100_PROSENT

  REGEL 9
  Betingelse 1	Bidragsmottakers inntekt er lavere enn eller lik 0034
  Betingelse 2	Bidragsmottakers sivilstand er ENSLIG
  Betingelse 3	Antall barn i husstand er 1
  Resultatkode	ORDINAERT_FORSKUDD_75_PROSENT

  REGEL 10
  Betingelse 1	Bidragsmottakers inntekt er høyere enn 0034
  Betingelse 2	Bidragsmottakers sivilstand er ENSLIG
  Betingelse 3	Antall barn i husstand er 1
  Resultatkode	REDUSERT_FORSKUDD_50_PROSENT

  REGEL 11
  Betingelse 1	Bidragsmottakers inntekt er lavere enn eller lik 0034 + (0036 x antall barn utover ett)
  Betingelse 2	Bidragsmottakers sivilstand er ENSLIG
  Betingelse 3	Antall barn i husstand er mer enn 1
  Resultatkode	ORDINAERT_FORSKUDD_75_PROSENT

  REGEL 12
  Betingelse 1	Bidragsmottakers inntekt er høyere enn 0034 + (0036 x antall barn utover ett)
  Betingelse 2	Bidragsmottakers sivilstand er ENSLIG
  Betingelse 3	Antall barn i husstand er mer enn 1
  Resultatkode	REDUSERT_FORSKUDD_50_PROSENT

  REGEL 13
  Betingelse 1	Bidragsmottakers inntekt er lavere enn eller lik 0035
  Betingelse 2	Bidragsmottakers sivilstand er GIFT
  Betingelse 3	Antall barn i husstand er 1
  Resultatkode	ORDINAERT_FORSKUDD_75_PROSENT

  REGEL 14
  Betingelse 1	Bidragsmottakers inntekt er høyere enn 0035
  Betingelse 2	Bidragsmottakers sivilstand er GIFT
  Betingelse 3	Antall barn i husstand er 1
  Resultatkode	REDUSERT_FORSKUDD_50_PROSENT

  REGEL 15
  Betingelse 1	Bidragsmottakers inntekt er lavere enn eller lik 0035 + (0036 x antall barn utover ett)
  Betingelse 2	Bidragsmottakers sivilstand er GIFT
  Betingelse 3	Antall barn i husstand er mer enn 1
  Resultatkode	ORDINAERT_FORSKUDD_75_PROSENT

  REGEL 16
  Betingelse 1	Bidragsmottakers inntekt er høyere enn 0035 + (0036 x antall barn utover ett)
  Betingelse 2	Bidragsmottakers sivilstand er GIFT
  Betingelse 3	Antall barn i husstand er mer enn 1
  Resultatkode	REDUSERT_FORSKUDD_50_PROSENT
  */

  @BeforeEach
  void init() {
    // Sett opp wiremock mot sjablon-tjenestene
    sjablonApiStub.settOppSjablonStub();

    // Bygg opp url
    url = "http://localhost:" + port + "/beregn/forskudd";
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 1")
  void skalKalleCoreOgReturnereEtResultat_Eksempel01() {
    // Forhøyet forskudd ved 11 år: SB alder > 11 år; BM inntekt 290000; BM antall barn egen husstand 1; BM sivilstatus gift
    filnavn = "src/test/resources/testfiler/forskudd_eksempel1.json";

    forventetForskuddBelop = 2090;
    forventetForskuddResultatkode = "FORHOYET_FORSKUDD_11_AAR_125_PROSENT";
    forventetForskuddRegel = "REGEL 7";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 2")
  void skalKalleCoreOgReturnereEtResultat_Eksempel02() {
    // Ordinært forskudd: SB alder > 11 år; BM inntekt 300000; BM antall barn egen husstand 1; BM sivilstatus gift
    filnavn = "src/test/resources/testfiler/forskudd_eksempel2.json";

    forventetForskuddBelop = 1250;
    forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT";
    forventetForskuddRegel = "REGEL 13";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 3")
  void skalKalleCoreOgReturnereEtResultat_Eksempel03() {
    // Ordinært forskudd: SB alder > 11 år; BM inntekt 370000; BM antall barn egen husstand 1; BM sivilstatus gift
    filnavn = "src/test/resources/testfiler/forskudd_eksempel3.json";

    forventetForskuddBelop = 830;
    forventetForskuddResultatkode = "REDUSERT_FORSKUDD_50_PROSENT";
    forventetForskuddRegel = "REGEL 14";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 4")
  void skalKalleCoreOgReturnereEtResultat_Eksempel04() {
    // Ordinært forskudd: SB alder > 11 år; BM inntekt 370000; BM antall barn egen husstand 2; BM sivilstatus gift
    filnavn = "src/test/resources/testfiler/forskudd_eksempel4.json";

    forventetForskuddBelop = 1250;
    forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT";
    forventetForskuddRegel = "REGEL 15";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 5")
  void skalKalleCoreOgReturnereEtResultat_Eksempel05() {
    // Ordinært forskudd: SB alder > 11 år; BM inntekt 460000; BM antall barn egen husstand 1; BM sivilstatus gift
    filnavn = "src/test/resources/testfiler/forskudd_eksempel5.json";

    forventetForskuddBelop = 830;
    forventetForskuddResultatkode = "REDUSERT_FORSKUDD_50_PROSENT";
    forventetForskuddRegel = "REGEL 14";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 6")
  void skalKalleCoreOgReturnereEtResultat_Eksempel06() {
    // Ordinært forskudd: SB alder > 11 år; BM inntekt 460000; BM antall barn egen husstand 1; BM sivilstatus enslig
    filnavn = "src/test/resources/testfiler/forskudd_eksempel6.json";

    forventetForskuddBelop = 1250;
    forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT";
    forventetForskuddRegel = "REGEL 9";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 7")
  void skalKalleCoreOgReturnereEtResultat_Eksempel07() {
    // Ordinært forskudd: SB alder > 11 år; BM inntekt 460000; BM antall barn egen husstand 3; BM sivilstatus gift
    filnavn = "src/test/resources/testfiler/forskudd_eksempel7.json";

    forventetForskuddBelop = 1250;
    forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT";
    forventetForskuddRegel = "REGEL 15";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 8")
  void skalKalleCoreOgReturnereEtResultat_Eksempel08() {
    // Ordinært forskudd: SB alder > 11 år; BM inntekt 460000; BM antall barn egen husstand 3; BM sivilstatus enslig
    filnavn = "src/test/resources/testfiler/forskudd_eksempel8.json";

    forventetForskuddBelop = 1250;
    forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT";
    forventetForskuddRegel = "REGEL 11";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 9")
  void skalKalleCoreOgReturnereEtResultat_Eksempel09() {
    // Ordinært forskudd: SB alder > 11 år; BM inntekt 530000; BM antall barn egen husstand 1; BM sivilstatus gift
    filnavn = "src/test/resources/testfiler/forskudd_eksempel9.json";

    forventetForskuddBelop = 830;
    forventetForskuddResultatkode = "REDUSERT_FORSKUDD_50_PROSENT";
    forventetForskuddRegel = "REGEL 14";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 10")
  void skalKalleCoreOgReturnereEtResultat_Eksempel10() {
    // Ordinært forskudd: SB alder > 11 år; BM inntekt 540000; BM antall barn egen husstand 1; BM sivilstatus enslig
    filnavn = "src/test/resources/testfiler/forskudd_eksempel10.json";

    forventetForskuddBelop = 0;
    forventetForskuddResultatkode = "AVSLAG";
    forventetForskuddRegel = "REGEL 6";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 11")
  void skalKalleCoreOgReturnereEtResultat_Eksempel11() {
    // Ordinært forskudd: SB alder > 11 år; BM inntekt 540000; BM antall barn egen husstand 1; BM sivilstatus gift
    filnavn = "src/test/resources/testfiler/forskudd_eksempel11.json";

    forventetForskuddBelop = 0;
    forventetForskuddResultatkode = "AVSLAG";
    forventetForskuddRegel = "REGEL 6";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 12")
  void skalKalleCoreOgReturnereEtResultat_Eksempel12() {
    // Ordinært forskudd: SB alder < 11 år; BM inntekt 290000; BM antall barn egen husstand 1; BM sivilstatus enslig
    filnavn = "src/test/resources/testfiler/forskudd_eksempel12.json";

    forventetForskuddBelop = 1670;
    forventetForskuddResultatkode = "FORHOYET_FORSKUDD_100_PROSENT";
    forventetForskuddRegel = "REGEL 8";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 13")
  void skalKalleCoreOgReturnereEtResultat_Eksempel13() {
    // Ordinært forskudd: SB alder < 11 år; BM inntekt 290000+13000; BM antall barn egen husstand 1; BM sivilstatus enslig
    filnavn = "src/test/resources/testfiler/forskudd_eksempel13.json";

    forventetForskuddBelop = 1250;
    forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT";
    forventetForskuddRegel = "REGEL 9";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 14")
  void skalKalleCoreOgReturnereEtResultat_Eksempel14() {
    // Ordinært forskudd: SB alder < 11 år; BM inntekt 361000; BM antall barn egen husstand 1; BM sivilstatus gift
    filnavn = "src/test/resources/testfiler/forskudd_eksempel14.json";

    forventetForskuddBelop = 830;
    forventetForskuddResultatkode = "REDUSERT_FORSKUDD_50_PROSENT";
    forventetForskuddRegel = "REGEL 14";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 15")
  void skalKalleCoreOgReturnereEtResultat_Eksempel15() {
    // Ordinært forskudd: SB alder < 11 år; BM inntekt 361000; BM antall barn egen husstand 1; BM sivilstatus enslig
    filnavn = "src/test/resources/testfiler/forskudd_eksempel15.json";

    forventetForskuddBelop = 1250;
    forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT";
    forventetForskuddRegel = "REGEL 9";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 16")
  void skalKalleCoreOgReturnereEtResultat_Eksempel16() {
    // Ordinært forskudd: SB alder < 11 år; BM inntekt 468000; BM antall barn egen husstand 1; BM sivilstatus enslig
    filnavn = "src/test/resources/testfiler/forskudd_eksempel16.json";

    forventetForskuddBelop = 1250;
    forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT";
    forventetForskuddRegel = "REGEL 9";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 17")
  void skalKalleCoreOgReturnereEtResultat_Eksempel17() {
    // Ordinært forskudd: SB alder < 11 år; BM inntekt 468000; BM antall barn egen husstand 1; BM sivilstatus gift
    filnavn = "src/test/resources/testfiler/forskudd_eksempel17.json";

    forventetForskuddBelop = 830;
    forventetForskuddResultatkode = "REDUSERT_FORSKUDD_50_PROSENT";
    forventetForskuddRegel = "REGEL 14";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 18")
  void skalKalleCoreOgReturnereEtResultat_Eksempel18() {
    // Ordinært forskudd: SB alder < 11 år; BM inntekt 429000; BM antall barn egen husstand 2; BM sivilstatus enslig
    filnavn = "src/test/resources/testfiler/forskudd_eksempel18.json";

    forventetForskuddBelop = 1250;
    forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT";
    forventetForskuddRegel = "REGEL 11";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 19")
  void skalKalleCoreOgReturnereEtResultat_Eksempel19() {
    // Ordinært forskudd: SB alder < 11 år; BM inntekt 429000; BM antall barn egen husstand 2; BM sivilstatus gift
    filnavn = "src/test/resources/testfiler/forskudd_eksempel19.json";

    forventetForskuddBelop = 1250;
    forventetForskuddResultatkode = "ORDINAERT_FORSKUDD_75_PROSENT";
    forventetForskuddRegel = "REGEL 15";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 20")
  void skalKalleCoreOgReturnereEtResultat_Eksempel20() {
    // Ordinært forskudd: SB alder < 11 år; BM inntekt 430000; BM antall barn egen husstand 2; BM sivilstatus gift
    filnavn = "src/test/resources/testfiler/forskudd_eksempel20.json";

    forventetForskuddBelop = 830;
    forventetForskuddResultatkode = "REDUSERT_FORSKUDD_50_PROSENT";
    forventetForskuddRegel = "REGEL 16";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 22")
  void skalKalleCoreOgReturnereEtResultat_Eksempel22() {
    // Ordinært forskudd: SB alder < 11 år; BM inntekt 489000+60000; BM antall barn egen husstand 2; BM sivilstatus gift
    filnavn = "src/test/resources/testfiler/forskudd_eksempel22.json";

    forventetForskuddBelop = 0;
    forventetForskuddResultatkode = "AVSLAG";
    forventetForskuddRegel = "REGEL 6";

    utfoerBeregningerOgEvaluerResultat();
  }

  @Test
  @DisplayName("skal kalle core og returnere et resultat - eksempel 23")
  void skalKalleCoreOgReturnereEtResultat_Eksempel23() {
    // Ordinært forskudd: SB alder < 11 år; BM inntekt 489000; BM antall barn egen husstand 2; BM sivilstatus gift
    filnavn = "src/test/resources/testfiler/forskudd_eksempel23.json";

    forventetForskuddBelop = 830;
    forventetForskuddResultatkode = "REDUSERT_FORSKUDD_50_PROSENT";
    forventetForskuddRegel = "REGEL 16";

    utfoerBeregningerOgEvaluerResultat();
  }

  private void utfoerBeregningerOgEvaluerResultat() {
    var request = lesFilOgByggRequest(filnavn);

    // Kall rest-API for barnebidrag
    var responseEntity = httpHeaderTestRestTemplate.exchange(url, HttpMethod.POST, request, BeregnetForskuddResultat.class);
    var forskuddResultat = responseEntity.getBody();

    assertAll(
        () -> assertThat(responseEntity.getStatusCode()).isEqualTo(OK),
        () -> assertThat(forskuddResultat).isNotNull(),

        // Sjekk resultat av beregningnen
        () -> assertThat(forskuddResultat.getBeregnetForskuddPeriodeListe()).isNotNull(),
        () -> assertThat(forskuddResultat.getBeregnetForskuddPeriodeListe().size()).isEqualTo(1),
        () -> assertThat(forskuddResultat.getBeregnetForskuddPeriodeListe().get(0).getResultat()).isNotNull(),
        () -> assertThat(forskuddResultat.getBeregnetForskuddPeriodeListe().get(0).getResultat().getBelop().intValue())
            .isEqualTo(forventetForskuddBelop),
        () -> assertThat(forskuddResultat.getBeregnetForskuddPeriodeListe().get(0).getResultat().getKode())
            .isEqualTo(forventetForskuddResultatkode),
        () -> assertThat(forskuddResultat.getBeregnetForskuddPeriodeListe().get(0).getResultat().getRegel())
            .isEqualTo(forventetForskuddRegel),

        () -> assertThat(forskuddResultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlagReferanseListe().size())
            .isEqualTo(forskuddResultat.getGrunnlagListe().size()),
        () -> assertThat((int) forskuddResultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlagReferanseListe().stream()
            .filter(grunnlagReferanse -> grunnlagReferanse.startsWith("Mottatt")).count())
            .isEqualTo(request.getBody().split("Mottatt", -1).length  - 1),
        () -> assertThat((int) forskuddResultat.getBeregnetForskuddPeriodeListe().get(0).getGrunnlagReferanseListe().stream()
            .filter(grunnlagReferanse -> grunnlagReferanse.startsWith("Sjablon")).count())
            .isEqualTo(7)
    );
  }

  private HttpEntity<String> lesFilOgByggRequest(String filnavn) {
    var json = "";

    // Les inn fil med request-data (json)
    try {
      json = Files.readString(Paths.get(filnavn));
    } catch (Exception e) {
      fail("Klarte ikke å lese fil: " + filnavn);
    }

    // Lag request
    return initHttpEntity(json);
  }

  private <T> HttpEntity<T> initHttpEntity(T body) {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(body, httpHeaders);
  }

}
