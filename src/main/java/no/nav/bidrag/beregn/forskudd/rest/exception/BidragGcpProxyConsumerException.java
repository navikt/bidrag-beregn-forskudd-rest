package no.nav.bidrag.beregn.forskudd.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientResponseException;

public class BidragGcpProxyConsumerException extends RuntimeException {

  private final HttpStatus statusCode;

  public HttpStatus getStatusCode() {
    return statusCode;
  }

  public BidragGcpProxyConsumerException(RestClientResponseException exception) {
    super(exception);
    this.statusCode = HttpStatus.valueOf(exception.getRawStatusCode());
  }
}
