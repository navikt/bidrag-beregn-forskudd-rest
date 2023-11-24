package no.nav.bidrag.beregn.forskudd.rest.consumer.wiremockstub

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.springframework.cloud.contract.spec.internal.HttpStatus
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component

@Component
class SjablonApiStub {
    fun settOppSjablonStub() {
        settOppSjablonSjablontallStub()
    }

    private fun aClosedJsonResponse(): ResponseDefinitionBuilder {
        return WireMock.aResponse()
            .withHeader(HttpHeaders.CONNECTION, "close")
            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
    }

    private fun settOppSjablonSjablontallStub() {
        val url = "/bidrag-sjablon/sjablontall/all"
        val sjablonliste =
            listOf(
                " [  " +
                    "{\"typeSjablon\": \"0005\"," +
                    "\"datoFom\": \"2020-07-01\"," +
                    "\"datoTom\": \"9999-12-31\"," +
                    "\"verdi\": 1670," +
                    "\"brukerid\": \"A100364 \"," +
                    "\"tidspktEndret\": \"2020-05-17T14:13:16.999\"}," +

                    "{\"typeSjablon\": \"0013\"," +
                    "\"datoFom\": \"2003-01-01\"," +
                    "\"datoTom\": \"9999-12-31\"," +
                    "\"verdi\": 320," +
                    "\"brukerid\": \"A100364 \"," +
                    "\"tidspktEndret\": \"2020-05-17T14:13:16.999\"}," +

                    "{\"typeSjablon\": \"0034\"," +
                    "\"datoFom\": \"2020-07-01\"," +
                    "\"datoTom\": \"9999-12-31\"," +
                    "\"verdi\": 468500," +
                    "\"brukerid\": \"A100364 \"," +
                    "\"tidspktEndret\": \"2020-05-17T14:13:16.999\"}," +

                    "{\"typeSjablon\": \"0033\"," +
                    "\"datoFom\": \"2020-07-01\"," +
                    "\"datoTom\": \"9999-12-31\"," +
                    "\"verdi\": 297500," +
                    "\"brukerid\": \"A100364 \"," +
                    "\"tidspktEndret\": \"2020-05-17T14:13:16.999\"}," +

                    "{\"typeSjablon\": \"0035\"," +
                    "\"datoFom\": \"2020-07-01\"," +
                    "\"datoTom\": \"9999-12-31\"," +
                    "\"verdi\": 360800," +
                    "\"brukerid\": \"A100364 \"," +
                    "\"tidspktEndret\": \"2020-05-17T14:13:16.999\"}," +

                    "{\"typeSjablon\": \"0036\"," +
                    "\"datoFom\": \"2020-07-01\"," +
                    "\"datoTom\": \"9999-12-31\"," +
                    "\"verdi\": 69100," +
                    "\"brukerid\": \"A100364 \"," +
                    "\"tidspktEndret\": \"2020-05-17T14:13:16.999\"}," +

                    "{\"typeSjablon\": \"0038\"," +
                    "\"datoFom\": \"2020-07-01\"," +
                    "\"datoTom\": \"9999-12-31\"," +
                    "\"verdi\": 1250," +
                    "\"brukerid\": \"A100364 \"," +
                    "\"tidspktEndret\": \"2020-05-17T14:15:49.233\"}]",
            )

        stubFor(
            get(urlEqualTo(url))
                .willReturn(
                    aClosedJsonResponse()
                        .withStatus(HttpStatus.OK)
                        .withBody(
                            sjablonliste.joinToString(),
                        ),
                ),
        )
    }
}
