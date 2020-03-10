package no.nav.bidrag.beregn.forskudd.rest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import no.nav.bidrag.beregn.forskudd.core.ForskuddCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ForskuddPeriodeResultatDto;
import no.nav.bidrag.beregn.forskudd.rest.TestUtil;
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer;
import no.nav.bidrag.commons.web.HttpStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
  void initMocksAndService() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @DisplayName("skal beregne forskudd")
  void testSkalBeregneForskudd() {
    when(sjablonConsumerMock.hentSjablontall()).thenReturn(new HttpStatusResponse<>(HttpStatus.OK, TestUtil.dummySjablonListe()));
    when(forskuddCoreMock.beregnForskudd(any())).thenReturn(new ForskuddPeriodeResultatDto());
    var beregnForskuddResultat = beregnService.beregn(TestUtil.dummyForskuddGrunnlagDto());
    assertThat(beregnForskuddResultat).isNotNull();
  }
}
