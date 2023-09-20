package no.nav.bidrag.beregn.forskudd.rest.consumer

import no.nav.bidrag.beregn.forskudd.rest.exception.SjablonConsumerException
import no.nav.bidrag.commons.web.HttpResponse
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate

private const val SJABLONTALL_URL = "/sjablontall/all"

class SjablonConsumer(private val restTemplate: RestTemplate) {

    fun hentSjablonSjablontall(): HttpResponse<List<Sjablontall>> {
        try {
            val sjablonResponse = restTemplate.exchange(SJABLONTALL_URL, HttpMethod.GET, null, SJABLON_SJABLONTALL_LISTE)
            LOGGER.info("hentSjablonSjablontall fikk http status ${sjablonResponse.statusCode} fra bidrag-sjablon")
            return HttpResponse(sjablonResponse)
        } catch (exception: RestClientResponseException) {
            LOGGER.error(
                "hentSjablonSjablontall fikk følgende feilkode fra bidrag-sjablon: ${exception.statusText}, med melding ${exception.message}"
            )
            throw SjablonConsumerException(exception)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SjablonConsumer::class.java)
        private val SJABLON_SJABLONTALL_LISTE = object : ParameterizedTypeReference<List<Sjablontall>>() {}
    }
}
