package no.nav.bidrag.beregn.forskudd.rest.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpStatusCodeException

@ControllerAdvice
class ExceptionHandlerAdvice {
    @ExceptionHandler
    fun handleUgyldigInputException(exception: IllegalArgumentException): ResponseEntity<*> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .header(HttpHeaders.WARNING, errorMsg(exception))
            .build<Any>()
    }

    @ExceptionHandler
    fun handleSjablonConsumerException(exception: HttpStatusCodeException): ResponseEntity<*> {
        return ResponseEntity
            .status(exception.statusCode)
            .header(HttpHeaders.WARNING, errorMsg(exception))
            .build<Any>()
    }

    private fun errorMsg(runtimeException: RuntimeException): String {
        return String.format("%s: %s", runtimeException.javaClass.simpleName, runtimeException.message)
    }
}
