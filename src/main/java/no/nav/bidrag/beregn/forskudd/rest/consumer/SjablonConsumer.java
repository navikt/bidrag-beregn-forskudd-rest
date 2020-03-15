package no.nav.bidrag.beregn.forskudd.rest.consumer;

import java.util.List;
import no.nav.bidrag.commons.web.HttpStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

public class SjablonConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SjablonConsumer.class);
  private static final ParameterizedTypeReference<List<Sjablontall>> SJABLONTALL_LISTE = new ParameterizedTypeReference<>() {
  };

  private final RestTemplate restTemplate;

  public SjablonConsumer(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public HttpStatusResponse<List<Sjablontall>> hentSjablontall() {
    var sjablonResponse = restTemplate.exchange("/sjablontall/all", HttpMethod.GET, null, SJABLONTALL_LISTE);

    //TODO Endre denne testen, den slår alltid til
    if (sjablonResponse != null) {
      LOGGER.info("Status ({}) for hent sjablontall: {}", sjablonResponse.getStatusCode(), sjablonResponse.getBody());
    } else {
      LOGGER.warn("Hent sjablontall feilet");
      return new HttpStatusResponse<>(HttpStatus.I_AM_A_TEAPOT);
    }

    return new HttpStatusResponse<>(sjablonResponse.getStatusCode(), sjablonResponse.getBody());
  }
}
