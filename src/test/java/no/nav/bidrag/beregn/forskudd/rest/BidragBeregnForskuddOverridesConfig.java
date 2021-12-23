package no.nav.bidrag.beregn.forskudd.rest;

import java.util.Collections;
import no.nav.bidrag.beregn.forskudd.rest.consumer.BidragGcpProxyConsumer;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
@Profile({"test", "local"})
public class BidragBeregnForskuddOverridesConfig {

  @Autowired
  public MockOAuth2Server auth2Server;

  @Bean
  public BidragGcpProxyConsumer bidragGcpProxyConsumer(
      @Value("${BIDRAGGCPPROXY_URL}") String url,
      RestTemplate restTemplate
  ) {
    restTemplate.setUriTemplateHandler(new RootUriTemplateHandler(url));
    restTemplate.getInterceptors().add(generateBearerToken("bidraggcpproxy"));
    return new BidragGcpProxyConsumer(restTemplate);
  }

  public ClientHttpRequestInterceptor generateBearerToken(String clientRegistrationId) {
    return (request, body, execution) -> {
      var accessToken = token("", "", "");
      request.getHeaders().setBearerAuth(accessToken);
      return execution.execute(request, body);
    };
  }

  private String token(String issuerId, String subject, String scope) {
    return "Bearer "  + auth2Server.issueToken(
        issuerId,
        subject,
        new DefaultOAuth2TokenCallback(
            issuerId,
            subject,
            Collections.emptyList(),
            Collections.singletonMap("scope", scope),
            3600
        )
    ).serialize();
  }
}
