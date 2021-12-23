package no.nav.bidrag.beregn.forskudd.rest.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;

@Service
public class SecurityTokenService {

  private Authentication ANONYMOUS_AUTHENTICATION =  new AnonymousAuthenticationToken(
      "anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
  );

  private OAuth2AuthorizedClientManager authorizedClientManager;

  public SecurityTokenService(@Lazy OAuth2AuthorizedClientManager authorizedClientManager) {
    this.authorizedClientManager = authorizedClientManager;
  }

  public ClientHttpRequestInterceptor generateBearerToken(String clientRegistrationId) {
    return (request, body, execution) -> {
      var accessToken = authorizedClientManager
          .authorize(
              OAuth2AuthorizeRequest
                  .withClientRegistrationId(clientRegistrationId)
                  .principal(ANONYMOUS_AUTHENTICATION)
                  .build()
          ).getAccessToken();
      request.getHeaders().setBearerAuth(accessToken.getTokenValue());
      return execution.execute(request, body);
    };
  }
}
