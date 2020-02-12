package no.nav.bidrag.beregning.forskudd.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SjablonConsumer {

  public SjablonConsumer(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  private final RestTemplate restTemplate;

}
