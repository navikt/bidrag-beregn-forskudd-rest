package no.nav.bidrag.beregn.forskudd.rest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import no.nav.bidrag.beregn.forskudd.core.ForskuddCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.rest.TestUtil;
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer;
import no.nav.bidrag.commons.web.HttpStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

@DisplayName("BeregnServiceTest")
class BeregnServiceTest {

  @InjectMocks
  private BeregnService beregnService;

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

    var beregnForskuddResultat = beregnService.beregn(TestUtil.dummyForskuddGrunnlagCore());
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

    var beregnForskuddResultat = beregnService.beregn(TestUtil.dummyForskuddGrunnlagCore());

    assertAll(
        () -> assertThat(beregnForskuddResultat.getHttpStatus()).isEqualTo(HttpStatus.NO_CONTENT),
        () -> assertThat(beregnForskuddResultat.getBody()).isNull()
    );
  }

  @Test
  @DisplayName("Feil retur fra SjablonConsumer")
  void feilReturFraSjablonConsumer() {
    when(sjablonConsumerMock.hentSjablontall()).thenReturn(new HttpStatusResponse<>(HttpStatus.SERVICE_UNAVAILABLE, null));

    var beregnForskuddResultat = beregnService.beregn(TestUtil.dummyForskuddGrunnlagCore());

    assertAll(
        () -> assertThat(beregnForskuddResultat.getHttpStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE),
        () -> assertThat(beregnForskuddResultat.getBody()).isNull()
    );
  }
}
