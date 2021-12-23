package no.nav.bidrag.beregn.forskudd.rest.consumer.wiremock_stub;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.springframework.cloud.contract.spec.internal.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class SjablonApiStub {

  public void settOppSjablonStub() {
    settOppSjablonSjablontallStub();
  }

  private void settOppSjablonSjablontallStub() {
    var url = "/sjablon/sjablontall?all=true";

    stubFor(
        get(urlEqualTo(url))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withStatus(HttpStatus.OK)
                    .withBody(
                        String.join(
                            "\n",
                            " [  "
                                + "{\"typeSjablon\": \"0005\","
                                + "\"datoFom\": \"2020-07-01\","
                                + "\"datoTom\": \"9999-12-31\","
                                + "\"verdi\": 1670,"
                                + "\"brukerid\": \"A100364 \","
                                + "\"tidspktEndret\": \"2020-05-17T14:13:16.999\"},"

                                + "{\"typeSjablon\": \"0013\","
                                + "\"datoFom\": \"2003-01-01\","
                                + "\"datoTom\": \"9999-12-31\","
                                + "\"verdi\": 320,"
                                + "\"brukerid\": \"A100364 \","
                                + "\"tidspktEndret\": \"2020-05-17T14:13:16.999\"},"

                                + "{\"typeSjablon\": \"0034\","
                                + "\"datoFom\": \"2020-07-01\","
                                + "\"datoTom\": \"9999-12-31\","
                                + "\"verdi\": 468500,"
                                + "\"brukerid\": \"A100364 \","
                                + "\"tidspktEndret\": \"2020-05-17T14:13:16.999\"},"

                                + "{\"typeSjablon\": \"0033\","
                                + "\"datoFom\": \"2020-07-01\","
                                + "\"datoTom\": \"9999-12-31\","
                                + "\"verdi\": 297500,"
                                + "\"brukerid\": \"A100364 \","
                                + "\"tidspktEndret\": \"2020-05-17T14:13:16.999\"},"

                                + "{\"typeSjablon\": \"0035\","
                                + "\"datoFom\": \"2020-07-01\","
                                + "\"datoTom\": \"9999-12-31\","
                                + "\"verdi\": 360800,"
                                + "\"brukerid\": \"A100364 \","
                                + "\"tidspktEndret\": \"2020-05-17T14:13:16.999\"},"

                                + "{\"typeSjablon\": \"0036\","
                                + "\"datoFom\": \"2020-07-01\","
                                + "\"datoTom\": \"9999-12-31\","
                                + "\"verdi\": 69100,"
                                + "\"brukerid\": \"A100364 \","
                                + "\"tidspktEndret\": \"2020-05-17T14:13:16.999\"},"

                                + "{\"typeSjablon\": \"0038\","
                                + "\"datoFom\": \"2020-07-01\","
                                + "\"datoTom\": \"9999-12-31\","
                                + "\"verdi\": 1250,"
                                + "\"brukerid\": \"A100364 \","
                                + "\"tidspktEndret\": \"2020-05-17T14:15:49.233\"}]"))));
  }
}
