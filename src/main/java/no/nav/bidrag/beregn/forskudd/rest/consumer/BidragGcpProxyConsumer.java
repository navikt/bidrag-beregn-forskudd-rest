package no.nav.bidrag.beregn.forskudd.rest.consumer;

import java.util.List;
import no.nav.bidrag.beregn.forskudd.rest.exception.BidragGcpProxyConsumerException;
import no.nav.bidrag.commons.web.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class BidragGcpProxyConsumer {

  private final RestTemplate restTemplate;

  private static final Logger LOGGER = LoggerFactory.getLogger(BidragGcpProxyConsumer.class);

  public BidragGcpProxyConsumer(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public <T> HttpResponse<List<T>> hentSjablonListe(String path, ParameterizedTypeReference<List<T>> responseType) {
    try {
      var sjablonListe = restTemplate.exchange(path, HttpMethod.GET, null, responseType);
      LOGGER.info("Hent {} fikk http status {} fra bidrag-sjablon", path, sjablonListe.getStatusCode());
      return new HttpResponse<>(sjablonListe);
    } catch (RestClientResponseException exception) {
      throw new BidragGcpProxyConsumerException(exception);
    }
  }
}