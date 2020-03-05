package no.nav.bidrag.beregn.forskudd.rest;

import no.nav.bidrag.beregn.forskudd.periode.ForskuddPeriode;
import no.nav.bidrag.beregn.forskudd.periode.ForskuddPeriodeImpl;
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BidragBeregnForskudd {

  @Bean
  public ForskuddPeriode forskuddPeriode() {
    return new ForskuddPeriodeImpl();
  }

  @Bean
  public SjablonConsumer sjablonConsumer(@Value("${SJABLON_URL}") String sjablonBaseUrl, RestTemplate restTemplate) {
    restTemplate.setUriTemplateHandler(new RootUriTemplateHandler(sjablonBaseUrl));
    return new SjablonConsumer(restTemplate);
  }

  public static void main(String[] args) {
    SpringApplication.run(BidragBeregnForskudd.class, args);
  }
}
