package no.nav.bidrag.beregn.forskudd.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import java.time.LocalDate;
import no.nav.bidrag.beregn.forskudd.rest.BidragBeregnForskuddLocal;
import no.nav.bidrag.beregn.forskudd.rest.TestUtil;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnetForskuddResultat;
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

    when(beregnForskuddServiceMock.beregn(any(BeregnForskuddGrunnlag.class)))
        .thenReturn(HttpResponse.from(OK, TestUtil.dummyForskuddResultat()));

    var url = "http://localhost:" + port + "/bidrag-beregn-forskudd-rest/beregn/forskudd";
    var request = initHttpEntity(TestUtil.byggDummyForskuddGrunnlag());
    var responseEntity = httpHeaderTestRestTemplate.exchange(url, HttpMethod.POST, request, BeregnetForskuddResultat.class);
    var forskuddResultat = responseEntity.getBody();

    assertAll(
        () -> assertThat(responseEntity.getStatusCode()).isEqualTo(OK),
        () -> assertThat(forskuddResultat).isNotNull(),
        () -> assertThat(forskuddResultat.getBeregnetForskuddPeriodeListe()).isNotNull(),
        () -> assertThat(forskuddResultat.getBeregnetForskuddPeriodeListe().size()).isEqualTo(1),
        () -> assertThat(forskuddResultat.getBeregnetForskuddPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(forskuddResultat.getBeregnetForskuddPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(forskuddResultat.getBeregnetForskuddPeriodeListe().get(0).getResultat().getBelop().intValue())
            .isEqualTo(100),
        () -> assertThat(forskuddResultat.getBeregnetForskuddPeriodeListe().get(0).getResultat().getKode())
            .isEqualTo("INNVILGET_100_PROSENT"),
        () -> assertThat(forskuddResultat.getBeregnetForskuddPeriodeListe().get(0).getResultat().getRegel()).isEqualTo("REGEL 1")
    );
  }

  @Test
  @DisplayName("Skal returnere 500 Internal Server Error når kall til servicen feiler")
  void skalReturnere500InternalServerErrorNaarKallTilServicenFeiler() {

    when(beregnForskuddServiceMock.beregn(any(BeregnForskuddGrunnlag.class))).thenReturn(HttpResponse.from(INTERNAL_SERVER_ERROR, null));

    var url = "http://localhost:" + port + "/bidrag-beregn-forskudd-rest/beregn/forskudd";
    var request = initHttpEntity(TestUtil.byggDummyForskuddGrunnlag());
    var responseEntity = httpHeaderTestRestTemplate.exchange(url, HttpMethod.POST, request, BeregnetForskuddResultat.class);
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
