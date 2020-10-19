package no.nav.bidrag.beregn.forskudd.rest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import no.nav.bidrag.beregn.forskudd.core.ForskuddCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.rest.TestUtil;
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer;
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException;
import no.nav.bidrag.commons.web.HttpResponse;
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
    when(sjablonConsumerMock.hentSjablonSjablontall()).thenReturn(HttpResponse.from(HttpStatus.OK, TestUtil.dummySjablonSjablontallListe()));
    when(forskuddCoreMock.beregnForskudd(grunnlagTilCoreCaptor.capture())).thenReturn(TestUtil.dummyForskuddResultatCore());

    var beregnForskuddResultat = beregnForskuddService.beregn(TestUtil.byggForskuddGrunnlag().tilCore());
    var grunnlagTilCore = grunnlagTilCoreCaptor.getValue();

    assertAll(
        () -> assertThat(beregnForskuddResultat.getResponseEntity().getStatusCode()).isEqualTo(HttpStatus.OK),
        () -> assertThat(beregnForskuddResultat.getResponseEntity().getBody()).isNotNull(),
        () -> assertThat(beregnForskuddResultat.getResponseEntity().getBody().getResultatPeriodeListe()).isNotNull(),
        () -> assertThat(beregnForskuddResultat.getResponseEntity().getBody().getResultatPeriodeListe().size()).isEqualTo(1),
        // Sjablontyper som ikke er gyldige for forskudd og sjabloner som ikke er innenfor beregn-fra-til-dato filtreres bort
        () -> assertThat(grunnlagTilCore.getSjablonPeriodeListe().size()).isEqualTo(21)
    );
  }

  @Test
  @DisplayName("Skal kaste UgyldigInputException ved feil retur fra Core")
  void skalKasteUgyldigInputExceptionVedFeilReturFraCore() {
    when(sjablonConsumerMock.hentSjablonSjablontall()).thenReturn(HttpResponse.from(HttpStatus.OK, TestUtil.dummySjablonSjablontallListe()));
    when(forskuddCoreMock.beregnForskudd(any())).thenReturn(TestUtil.dummyForskuddResultatCoreMedAvvik());

    assertThatExceptionOfType(UgyldigInputException.class)
        .isThrownBy(() -> beregnForskuddService.beregn(TestUtil.byggForskuddGrunnlag().tilCore()))
        .withMessageContaining("beregnDatoFra kan ikke være null")
        .withMessageContaining("periodeDatoTil må være etter periodeDatoFra");
  }
}
