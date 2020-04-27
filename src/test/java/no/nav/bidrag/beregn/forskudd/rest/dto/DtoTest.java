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
  @DisplayName("Skal kaste IllegalArgumentException når bostatusDatoFra er null")
  void skalKasteIllegalArgumentExceptionNaarBostatusDatoFraErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBostatusDatoFra();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("periodeDatoFra kan ikke være null");
  }

  @Test
  @DisplayName("Skal ikke kaste exception når bostatusDatoTil er null")
  void skalIkkeKasteExceptionNaarBostatusDatoTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBostatusDatoTil();
    assertThatCode(grunnlag::tilCore).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når bostatusKode er null")
  void skalKasteIllegalArgumentExceptionNaarBostatusKodeErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBostatusKode();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("bostatusKode kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når soknadBarnFodselsdato er null")
  void skalKasteIllegalArgumentExceptionNaarSoknadBarnFodselsdatoErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenSoknadBarnFodselsdato();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("soknadBarnFodselsdato kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når inntektDatoFra er null")
  void skalKasteIllegalArgumentExceptionNaarInntektDatoFraErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenInntektDatoFra();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("periodeDatoFra kan ikke være null");
  }

  @Test
  @DisplayName("Skal ikke kaste exception når inntektDatoTil er null")
  void skalIkkeKasteExceptionNaarInntektDatoTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenInntektDatoTil();
    assertThatCode(grunnlag::tilCore).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når inntektBelop er null")
  void skalKasteIllegalArgumentExceptionNaarInntektTypeErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenInntektType();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("inntektType kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når inntektBelop er null")
  void skalKasteIllegalArgumentExceptionNaarInntektBelopErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenInntektBelop();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("inntektBelop kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når sivilstandDatoFra er null")
  void skalKasteIllegalArgumentExceptionNaarSivilstandDatoFraErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenSivilstandDatoFra();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("periodeDatoFra kan ikke være null");
  }

  @Test
  @DisplayName("Skal ikke kaste exception når sivilstandDatoTil er null")
  void skalIkkeKasteExceptionNaarSivilstandDatoTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenSivilstandDatoTil();
    assertThatCode(grunnlag::tilCore).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når sivilstandKode er null")
  void skalKasteIllegalArgumentExceptionNaarSivilstandKodeErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenSivilstandKode();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("sivilstandKode kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når barnDatoFra er null")
  void skalKasteIllegalArgumentExceptionNaarBarnDatoFraErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBarnDatoFra();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("periodeDatoFra kan ikke være null");
  }

  @Test
  @DisplayName("Skal ikke kaste exception når barnDatoTil er null")
  void skalIkkeKasteExceptionNaarBarnDatoTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBarnDatoTil();
    assertThatCode(grunnlag::tilCore).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når beregnDatoFra er null")
  void skalKasteIllegalArgumentExceptionNaarBeregnDatoFraErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBeregnDatoFra();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("beregnDatoFra kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når beregnDatoTil er null")
  void skalKasteIllegalArgumentExceptionNaarBeregnDatoTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBeregnDatoTil();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("beregnDatoTil kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når soknadBarnBostatusPeriodeListe er null")
  void skalKasteIllegalArgumentExceptionNaarSoknadBarnBostatusPeriodeListeErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenSoknadBarnBostatusPeriodeListe();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("soknadBarnBostatusPeriodeListe kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når bidragMottakerInntektPeriodeListe er null")
  void skalKasteIllegalArgumentExceptionNaarBidragMottakerInntektPeriodeListeErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBidragMottakerInntektPeriodeListe();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("bidragMottakerInntektPeriodeListe kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når bidragMottakerSivilstandPeriodeListe er null")
  void skalKasteIllegalArgumentExceptionNaarBidragMottakerSivilstandPeriodeListeErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBidragMottakerSivilstandPeriodeListe();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("bidragMottakerSivilstandPeriodeListe kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når soknadBarn er null")
  void skalKasteIllegalArgumentExceptionNaarSoknadBarnErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenSoknadBarn();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("soknadBarn kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når bostatusDatoFraTil er null")
  void skalKasteIllegalArgumentExceptionNaarBostatusDatoFraTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBostatusDatoFraTil();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("bostatusDatoFraTil kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når inntektDatoFraTil er null")
  void skalKasteIllegalArgumentExceptionNaarInntektDatoFraTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenInntektDatoFraTil();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("inntektDatoFraTil kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når sivilstandDatoFraTil er null")
  void skalKasteIllegalArgumentExceptionNaarSivilstandDatoFraTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenSivilstandDatoFraTil();
    assertThatExceptionOfType(UgyldigInputException.class).isThrownBy(grunnlag::tilCore)
        .withMessage("sivilstandDatoFraTil kan ikke være null");
  }

  @Test
  @DisplayName("Skal ikke kaste exception")
  void skalIkkeKasteException() {
    var grunnlag = TestUtil.byggForskuddGrunnlag();
    assertThatCode(grunnlag::tilCore).doesNotThrowAnyException();
  }
}
