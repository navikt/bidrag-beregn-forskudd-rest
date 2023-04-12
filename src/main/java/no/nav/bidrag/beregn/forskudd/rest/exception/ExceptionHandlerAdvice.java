package no.nav.bidrag.beregn.forskudd.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {

  @ExceptionHandler
  public ResponseEntity<?> handleUgyldigInputException(UgyldigInputException exception) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .header("Error", errorMsg(exception))
        .build();
  }

  @ExceptionHandler
  public ResponseEntity<?> handleSjablonConsumerException(SjablonConsumerException exception) {
    return ResponseEntity
        .status(exception.getStatusCode())
        .header("Error", errorMsg(exception))
        .build();
  }

  private String errorMsg(RuntimeException runtimeException) {
    return String.format("%s: %s", runtimeException.getClass().getSimpleName(), runtimeException.getMessage());
  }
}
