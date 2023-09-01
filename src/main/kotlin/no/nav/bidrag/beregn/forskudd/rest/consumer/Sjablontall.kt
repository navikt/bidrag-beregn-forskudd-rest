package no.nav.bidrag.beregn.forskudd.rest.consumer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class Sjablontall(
    var typeSjablon: String? = null,
    var datoFom: LocalDate? = null,
    var datoTom: LocalDate? = null,
    var verdi: BigDecimal? = null
)
