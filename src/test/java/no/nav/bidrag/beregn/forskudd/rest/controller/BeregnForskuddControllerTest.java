package no.nav.bidrag.beregn.forskudd.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

import java.util.Optional;
import no.nav.bidrag.beregn.forskudd.core.dto.ForskuddPeriodeGrunnlagDto;
import no.nav.bidrag.beregn.forskudd.rest.BidragBeregnForskuddLocal;
import no.nav.bidrag.beregn.forskudd.rest.TestUtil;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.rest.service.BeregnService;
import no.nav.bidrag.commons.web.test.HttpHeaderTestRestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@DisplayName("BeregnForskuddControllerTest")
@SpringBootTest(classes = BidragBeregnForskuddLocal.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class BeregnForskuddControllerTest {

  @Autowired
  private HttpHeaderTestRestTemplate httpHeaderTestRestTemplate;
  @LocalServerPort
  private int port;
  @MockBean
  private BeregnService beregnServiceMock;

  @Test
  @DisplayName("skal hente mock data")
  void skalHenteMockData() {

    when(beregnServiceMock.beregn(any(ForskuddPeriodeGrunnlagDto.class))).thenReturn(TestUtil.dummyForskuddResultat());

    var url = "http://localhost:" + port + "/bidrag-beregn-forskudd-rest/beregn/forskudd";
    var request = initHttpEntity(TestUtil.dummyForskuddGrunnlag());

    var responseEntity = httpHeaderTestRestTemplate.exchange(url, HttpMethod.POST, request, BeregnForskuddResultat.class);

    var forskuddResultat = Optional.ofNullable(responseEntity.getBody());
    assertThat(responseEntity.getStatusCode()).as("status").isEqualTo(OK);
    assertThat(forskuddResultat).isNotNull();
//    assertThat(forskuddResultat).hasValueSatisfying(resultat -> assertAll(
//        () -> assertThat(resultat).extracting(BeregnForskuddResultat::getBidragPeriodeResultatListe).as("test").isEqualTo("Hello world")
//    ));
  }

  private <T> HttpEntity<T> initHttpEntity(T body) {
    var httpHeaders = new HttpHeaders();
    return new HttpEntity<>(body, httpHeaders);
  }

}
