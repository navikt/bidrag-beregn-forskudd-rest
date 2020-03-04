package no.nav.bidrag.beregn.forskudd.rest.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import no.nav.bidrag.beregn.forskudd.rest.TestUtil;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.Sjablontall;
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
@DisplayName("SjablonConsumerTest")
@SuppressWarnings("unchecked")
class SjablonConsumerTest {

  @InjectMocks
  private SjablonConsumer sjablonConsumer;

  @Mock
  private RestTemplate restTemplateMock;

  @Test
  @DisplayName("skal hente liste av sjablonverdier")
  void skalHenteListeAvSjablonverdier() {

    when(restTemplateMock.exchange(anyString(), any(), any(), (ParameterizedTypeReference<List<Sjablontall>>) any()))
        .thenReturn(new ResponseEntity<>(TestUtil.dummySjablonListe(), HttpStatus.OK));

    var sjablonResponse = sjablonConsumer.hentSjablontall();

    List<Sjablontall> sjablontallListe = sjablonResponse.getBody();

    assertThat(sjablontallListe.size() == 2);
    assertThat(sjablontallListe.get(0).getTypeSjablon().equals("0005"));

    verify(restTemplateMock)
        .exchange(
            eq("/sjablontall/all"),
            eq(HttpMethod.GET),
            any(),
            (ParameterizedTypeReference<List<Sjablontall>>) any()
        );
  }
}
