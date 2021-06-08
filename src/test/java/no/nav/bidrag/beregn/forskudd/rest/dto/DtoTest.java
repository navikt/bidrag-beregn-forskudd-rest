package no.nav.bidrag.beregn.forskudd.rest.dto;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import no.nav.bidrag.beregn.forskudd.rest.TestUtil;
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DtoTest")
class DtoTest {

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når beregnDatoFra er null")
  void skalKasteIllegalArgumentExceptionNaarBeregnDatoFraErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBeregnDatoFra();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::valider)
        .withMessage("beregnDatoFra kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når beregnDatoTil er null")
  void skalKasteIllegalArgumentExceptionNaarBeregnDatoTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBeregnDatoTil();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::valider)
        .withMessage("beregnDatoTil kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når grunnlagListe er null")
  void skalKasteIllegalArgumentExceptionNaarGrunnlagListeErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenGrunnlagListe();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::valider)
        .withMessage("grunnlagListe kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når referanse er null")
  void skalKasteIllegalArgumentExceptionNaarReferanseErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenReferanse();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::valider)
        .withMessage("referanse kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når type er null")
  void skalKasteIllegalArgumentExceptionNaarTypeErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenType();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::valider)
        .withMessage("type kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når innhold er null")
  void skalKasteIllegalArgumentExceptionNaarInnholdErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenInnhold();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::valider)
        .withMessage("innhold kan ikke være null");
  }

  @Test
  @DisplayName("Skal ikke kaste exception")
  void skalIkkeKasteException() {
    var grunnlag = TestUtil.byggDummyForskuddGrunnlag();
    assertThatCode(grunnlag::valider).doesNotThrowAnyException();
  }
}
