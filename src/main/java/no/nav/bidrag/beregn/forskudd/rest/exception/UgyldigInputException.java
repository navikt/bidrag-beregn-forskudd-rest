package no.nav.bidrag.beregn.forskudd.rest.exception;

public class UgyldigInputException extends IllegalArgumentException {

  public UgyldigInputException(String melding) {
    super(melding);
  }
}