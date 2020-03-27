package no.nav.bidrag.beregn.forskudd.rest.consumer;

import java.util.List;
import no.nav.bidrag.commons.web.HttpStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class SjablonConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SjablonConsumer.class);
  private static final ParameterizedTypeReference<List<Sjablontall>> SJABLONTALL_LISTE = new ParameterizedTypeReference<>() {
  };

  private final RestTemplate restTemplate;
  private final String sjablonUrl;

  public SjablonConsumer(RestTemplate restTemplate, String sjablonBaseUrl) {
    this.restTemplate = restTemplate;
    this.sjablonUrl = sjablonBaseUrl + "/sjablontall/all";
  }

  public HttpStatusResponse<List<Sjablontall>> hentSjablontall() {
    var sjablonResponse = restTemplate.exchange(sjablonUrl, HttpMethod.GET, null, SJABLONTALL_LISTE);

    if (sjablonResponse != null) {
      LOGGER.info("Status ({}) for hent sjablontall: ", sjablonResponse.getStatusCode());
    } else {
      return null;
    }

    return new HttpStatusResponse<>(sjablonResponse.getStatusCode(), sjablonResponse.getBody());
  }
}