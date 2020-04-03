package no.nav.bidrag.beregn.forskudd.rest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import no.nav.bidrag.beregn.forskudd.core.ForskuddCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.rest.TestUtil;
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer;
import no.nav.bidrag.beregn.forskudd.rest.exception.SjablonConsumerException;
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException;
import no.nav.bidrag.commons.web.HttpStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

@DisplayName("BeregnForskuddServiceTest")
class BeregnForskuddServiceTest {

  @InjectMocks
  private BeregnForskuddService beregnForskuddService;

  @Mock
  private SjablonConsumer sjablonConsumerMock;
  @Mock
  private ForskuddCore forskuddCoreMock;

  @BeforeEach
  void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @DisplayName("Skal beregne forskudd")
  void skalBeregneForskudd() {
    var grunnlagTilCoreCaptor = ArgumentCaptor.forClass(BeregnForskuddGrunnlagCore.class);
    when(sjablonConsumerMock.hentSjablontall()).thenReturn(new HttpStatusResponse<>(HttpStatus.OK, TestUtil.dummySjablonListe()));
    when(forskuddCoreMock.beregnForskudd(grunnlagTilCoreCaptor.capture())).thenReturn(TestUtil.dummyForskuddResultatCore());

    var beregnForskuddResultat = beregnForskuddService.beregn(TestUtil.dummyForskuddGrunnlagCore());
    var grunnlagTilCore = grunnlagTilCoreCaptor.getValue();

    assertAll(
        () -> assertThat(beregnForskuddResultat.getHttpStatus()).isEqualTo(HttpStatus.OK),
        () -> assertThat(beregnForskuddResultat.getBody()).isNotNull(),
        () -> assertThat(beregnForskuddResultat.getBody().getResultatPeriodeListe()).isNotNull(),
        () -> assertThat(beregnForskuddResultat.getBody().getResultatPeriodeListe().size()).isEqualTo(1),
        // Den ene sjablonen skal filtreres bort (ikke gyldig for forskudd)
        () -> assertThat(grunnlagTilCore.getSjablonPeriodeListe().size()).isEqualTo(TestUtil.dummySjablonListe().size() - 1)
    );
  }

  @Test
  @DisplayName("Null retur fra SjablonConsumer")
  void nullReturFraSjablonConsumer() {
    when(sjablonConsumerMock.hentSjablontall()).thenReturn(null);

    assertThatExceptionOfType(SjablonConsumerException.class)
        .isThrownBy(() -> beregnForskuddService.beregn(TestUtil.dummyForskuddGrunnlagCore()))
        .withMessage("Feil ved kall av bidrag-sjablon. Ingen respons");
  }

  @Test
  @DisplayName("Feil retur fra SjablonConsumer")
  void feilReturFraSjablonConsumer() {
    Map<String, String> body = new HashMap<>();
    body.put("error code", "204");
    body.put("error msg", "NO_CONTENT");
    body.put("error text", "Ingen sjablonverdier funnet");
    when(sjablonConsumerMock.hentSjablontall()).thenReturn(new HttpStatusResponse(HttpStatus.SERVICE_UNAVAILABLE, body.toString()));

    assertThatExceptionOfType(SjablonConsumerException.class)
        .isThrownBy(() -> beregnForskuddService.beregn(TestUtil.dummyForskuddGrunnlagCore()))
        .withMessageContaining("Feil ved kall av bidrag-sjablon. Status: " + HttpStatus.SERVICE_UNAVAILABLE + " Melding: ");
  }


  @Test
  @DisplayName("Feil i kontroll av input")
  void feilIKontrollAvInput() {
    when(sjablonConsumerMock.hentSjablontall()).thenReturn(new HttpStatusResponse<>(HttpStatus.OK, TestUtil.dummySjablonListe()));
    when(forskuddCoreMock.beregnForskudd(any())).thenReturn(TestUtil.dummyForskuddResultatCoreMedAvvik());

    assertThatExceptionOfType(UgyldigInputException.class)
        .isThrownBy(() -> beregnForskuddService.beregn(TestUtil.dummyForskuddGrunnlagCore()))
        .withMessageContaining("beregnDatoFra kan ikke være null")
        .withMessageContaining("periodeDatoTil må være etter periodeDatoFra");
  }
}
