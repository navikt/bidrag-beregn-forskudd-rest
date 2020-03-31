package no.nav.bidrag.beregn.forskudd.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UgyldigInputException extends IllegalArgumentException {

  private static final String UGYLDIG_INPUT = "Ugyldig input";

  public UgyldigInputException(String melding) {
    super(melding);
  }

  public UgyldigInputException() {
    super(UGYLDIG_INPUT);
  }
}