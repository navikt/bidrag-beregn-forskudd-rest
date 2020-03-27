package no.nav.bidrag.beregn.forskudd.rest.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import no.nav.bidrag.beregn.forskudd.rest.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
@DisplayName("SjablonConsumerTest")
class SjablonConsumerTest {

  @InjectMocks
  private SjablonConsumer sjablonConsumer;

  @Mock
  private RestTemplate restTemplateMock;

  @Test
  @DisplayName("Skal hente liste av sjablonverdier")
  void skalHenteListeAvSjablonverdier() {
    when(restTemplateMock.exchange(anyString(), eq(HttpMethod.GET), eq(null), (ParameterizedTypeReference<List<Sjablontall>>) any()))
        .thenReturn(new ResponseEntity<>(TestUtil.dummySjablonListe(), HttpStatus.OK));
    var sjablonResponse = sjablonConsumer.hentSjablontall();

    assertAll(
        () -> assertThat(sjablonResponse).isNotNull(),
        () -> assertThat(sjablonResponse.getBody().size()).isEqualTo(TestUtil.dummySjablonListe().size()),
        () -> assertThat(sjablonResponse.getBody().get(0).getTypeSjablon()).isEqualTo("0005")
    );
  }

  @Test
  @DisplayName("Skal returnere null")
  void skalReturnereNull() {
    when(restTemplateMock.exchange(anyString(), eq(HttpMethod.GET), eq(null), (ParameterizedTypeReference<List<Sjablontall>>) any()))
        .thenReturn(null);
    var sjablonResponse = sjablonConsumer.hentSjablontall();

    assertThat(sjablonResponse).isNull();
  }
}
