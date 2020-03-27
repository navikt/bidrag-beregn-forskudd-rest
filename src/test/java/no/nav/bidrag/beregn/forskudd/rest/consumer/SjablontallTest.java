package no.nav.bidrag.beregn.forskudd.rest.consumer;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SjablontallTest")
class SjablontallTest {

  @Test
  @DisplayName("Er gyldig sjablon")
  void erGyldigSjablon() {
    var sjablontall = new Sjablontall("0033", LocalDate.parse("2020-01-01"), LocalDate.parse("2020-12-31"), BigDecimal.valueOf(100));
    assertThat(sjablontall.erGyldigSjablon()).isTrue();
  }

  @Test
  @DisplayName("Er ikke gyldig sjablon")
  void erIkkeGyldigSjablon() {
    var sjablontall = new Sjablontall("XXXX", LocalDate.parse("2020-01-01"), LocalDate.parse("2020-12-31"), BigDecimal.valueOf(100));
    assertThat(sjablontall.erGyldigSjablon()).isFalse();
  }
}
