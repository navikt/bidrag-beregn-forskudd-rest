package no.nav.bidrag.beregn.forskudd.rest.consumer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class Sjablontall(
    var typeSjablon: String,
    var datoFom: LocalDate,
    var datoTom: LocalDate,
    var verdi: BigDecimal
) {
  fun erGyldigSjablon(): Boolean {
    return when (typeSjablon) {
      "0005", "0013", "0033", "0034", "0035", "0036" -> true
      else -> false
    }
  }
}
