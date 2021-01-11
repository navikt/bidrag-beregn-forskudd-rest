package no.nav.bidrag.beregn.forskudd.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import java.time.LocalDate;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.rest.BidragBeregnForskuddLocal;
import no.nav.bidrag.beregn.forskudd.rest.TestUtil;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.rest.service.BeregnForskuddService;
import no.nav.bidrag.commons.web.HttpResponse;
import no.nav.bidrag.commons.web.test.HttpHeaderTestRestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@ExtendWith(MockitoExtension.class)
@DisplayName("BeregnForskuddControllerTest")
@SpringBootTest(classes = BidragBeregnForskuddLocal.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class BeregnForskuddControllerMockTest {

  @Autowired
  private HttpHeaderTestRestTemplate httpHeaderTestRestTemplate;
  @LocalServerPort
  private int port;
  @MockBean
  private BeregnForskuddService beregnForskuddServiceMock;

  @Test
  @DisplayName("Skal returnere forskudd resultat")
  void skalReturnereForskuddResultat() {

    when(beregnForskuddServiceMock.beregn(any(BeregnForskuddGrunnlagCore.class)))
        .thenReturn(HttpResponse.from(OK, TestUtil.dummyForskuddResultat()));

    var url = "http://localhost:" + port + "/bidrag-beregn-forskudd-rest/beregn/forskudd";
    var request = initHttpEntity(TestUtil.byggForskuddGrunnlag());
    var responseEntity = httpHeaderTestRestTemplate.exchange(url, HttpMethod.POST, request, BeregnForskuddResultat.class);
    var forskuddResultat = responseEntity.getBody();

    assertAll(
        () -> assertThat(responseEntity.getStatusCode()).isEqualTo(OK),
        () -> assertThat(forskuddResultat).isNotNull(),
        () -> assertThat(forskuddResultat.getResultatPeriodeListe()).isNotNull(),
        () -> assertThat(forskuddResultat.getResultatPeriodeListe().size()).isEqualTo(1),
        () -> assertThat(forskuddResultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(forskuddResultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(forskuddResultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBelop())
            .isEqualTo(100),
        () -> assertThat(forskuddResultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatKode())
            .isEqualTo("INNVILGET_100_PROSENT"),
        () -> assertThat(forskuddResultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 1")
    );
  }

  @Test
  @DisplayName("Skal returnere 400 Bad Request når input data mangler")
  void skalReturnere400BadRequestNaarInputDataMangler() {

    var url = "http://localhost:" + port + "/bidrag-beregn-forskudd-rest/beregn/forskudd";
    var request = initHttpEntity(TestUtil.byggForskuddGrunnlagUtenBostatusKode());
    var responseEntity = httpHeaderTestRestTemplate.exchange(url, HttpMethod.POST, request, BeregnForskuddResultat.class);

    assertAll(
        () -> assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST)
    );
  }

  @Test
  @DisplayName("Skal returnere 500 Internal Server Error når kall til servicen feiler")
  void skalReturnere500InternalServerErrorNaarKallTilServicenFeiler() {

    when(beregnForskuddServiceMock.beregn(any(BeregnForskuddGrunnlagCore.class))).thenReturn(HttpResponse.from(INTERNAL_SERVER_ERROR, null));

    var url = "http://localhost:" + port + "/bidrag-beregn-forskudd-rest/beregn/forskudd";
    var request = initHttpEntity(TestUtil.byggForskuddGrunnlag());
    var responseEntity = httpHeaderTestRestTemplate.exchange(url, HttpMethod.POST, request, BeregnForskuddResultat.class);
    var forskuddResultat = responseEntity.getBody();

    assertAll(
        () -> assertThat(responseEntity.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR),
        () -> assertThat(forskuddResultat).isNull()
    );
  }

  private <T> HttpEntity<T> initHttpEntity(T body) {
    var httpHeaders = new HttpHeaders();
    return new HttpEntity<>(body, httpHeaders);
  }
}
