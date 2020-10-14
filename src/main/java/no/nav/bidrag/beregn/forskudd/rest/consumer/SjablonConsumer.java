package no.nav.bidrag.beregn.forskudd.rest.consumer;

import java.util.List;
import no.nav.bidrag.beregn.forskudd.rest.exception.SjablonConsumerException;
import no.nav.bidrag.commons.web.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class SjablonConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SjablonConsumer.class);
  private static final ParameterizedTypeReference<List<Sjablontall>> SJABLON_SJABLONTALL_LISTE = new ParameterizedTypeReference<>() {
  };

  private final RestTemplate restTemplate;
  private final String sjablonSjablontallUrl;

  public SjablonConsumer(RestTemplate restTemplate, String sjablonBaseUrl) {
    this.restTemplate = restTemplate;
    this.sjablonSjablontallUrl = sjablonBaseUrl + "/sjablontall/all";
  }

  public HttpResponse<List<Sjablontall>> hentSjablonSjablontall() {

    try {
      var sjablonResponse = restTemplate.exchange(sjablonSjablontallUrl, HttpMethod.GET, null, SJABLON_SJABLONTALL_LISTE);
      LOGGER.info("hentSjablonSjablontall fikk http status {} fra bidrag-sjablon", sjablonResponse.getStatusCode());
      return new HttpResponse<>(sjablonResponse);
    } catch (RestClientResponseException exception) {
      LOGGER.error("hentSjablonSjablontall fikk følgende feilkode fra bidrag-sjablon: {}, med melding {}", exception.getStatusText(),
          exception.getMessage());
      throw new SjablonConsumerException(exception);
    }
  }
}