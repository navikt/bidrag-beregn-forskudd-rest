package no.nav.bidrag.beregn.forskudd.rest.exception

import org.springframework.http.HttpStatusCode
import org.springframework.web.client.RestClientResponseException

class SjablonConsumerException(exception: RestClientResponseException) : RuntimeException(exception) {
    val statusCode: HttpStatusCode

    init {
        statusCode = exception.statusCode
    }
}
