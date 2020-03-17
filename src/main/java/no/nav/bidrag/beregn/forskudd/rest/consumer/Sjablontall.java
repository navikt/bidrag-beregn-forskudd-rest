package no.nav.bidrag.beregn.forskudd.rest.consumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sjablontall {

  private String typeSjablon;
  private LocalDate datoFom;
  private LocalDate datoTom;
  private BigDecimal verdi;

  protected Sjablontall() {
  }

  public Sjablontall(String typeSjablon, LocalDate datoFom, LocalDate datoTom, BigDecimal verdi) {
    this.typeSjablon = typeSjablon;
    this.datoFom = datoFom;
    this.datoTom = datoTom;
    this.verdi = verdi;
  }

  public String getTypeSjablon() {
    return typeSjablon;
  }

  public LocalDate getDatoFom() {
    return datoFom;
  }

  public LocalDate getDatoTom() {
    return datoTom;
  }

  public BigDecimal getVerdi() {
    return verdi;
  }

  public boolean erGyldigSjablon() {
    return (typeSjablon.equals("0005") || typeSjablon.equals("0013") || typeSjablon.equals("0033") || typeSjablon.equals("0034") ||
        typeSjablon.equals("0035") || typeSjablon.equals("0036"));
  }
}
