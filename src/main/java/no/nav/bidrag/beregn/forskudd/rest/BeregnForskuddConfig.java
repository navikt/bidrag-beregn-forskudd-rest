package no.nav.bidrag.beregn.forskudd.rest;

import no.nav.bidrag.beregn.forskudd.core.ForskuddCore;
import no.nav.bidrag.beregn.forskudd.rest.consumer.BidragGcpProxyConsumer;
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer;
import no.nav.bidrag.beregn.forskudd.rest.service.SecurityTokenService;
import no.nav.bidrag.beregn.forskudd.rest.service.SjablonService;
import no.nav.bidrag.commons.ExceptionLogger;
import no.nav.bidrag.commons.web.CorrelationIdFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeregnForskuddConfig {

  @Bean
  public ForskuddCore barnebidragCore() {
    return ForskuddCore.getInstance();
  }

//  @Bean
//  public SjablonConsumer sjablonConsumer(@Value("${SJABLON_URL}") String sjablonBaseUrl, RestTemplate restTemplate) {
//    return new SjablonConsumer(restTemplate, sjablonBaseUrl);
//  }

  @Bean
  public BidragGcpProxyConsumer bidragGcpProxyConsumer(
      @Value("${BIDRAGGCPPROXY_URL}") String url,
      SecurityTokenService securityTokenService,
      RestTemplate restTemplate
  ) {
    restTemplate.setUriTemplateHandler(new RootUriTemplateHandler(url));
    restTemplate.getInterceptors().add(securityTokenService.generateBearerToken("bidraggcpproxy"));
    return new BidragGcpProxyConsumer(restTemplate);
  }

  @Bean
  public SjablonService sjablonService(BidragGcpProxyConsumer bidragGcpProxyConsumer) {
    return new SjablonService(bidragGcpProxyConsumer);
  }

  @Bean
  public ExceptionLogger exceptionLogger() {
    return new ExceptionLogger(BidragBeregnForskudd.class.getSimpleName());
  }

  @Bean
  public CorrelationIdFilter correlationIdFilter() {
    return new CorrelationIdFilter();
  }

}
