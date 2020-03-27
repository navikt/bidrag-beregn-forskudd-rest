package no.nav.bidrag.beregn.forskudd.rest.dto;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import no.nav.bidrag.beregn.forskudd.rest.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DtoTest")
class DtoTest {

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når bostatusDatoFra er null")
  void bostatusDatoFraErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBostatusDatoFra();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("periodeDatoFra kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når bostatusDatoTil er null")
  void bostatusDatoTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBostatusDatoTil();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("periodeDatoTil kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når bostatusKode er null")
  void bostatusKodeErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBostatusKode();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("bostatusKode kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når soknadBarnFodselsdato er null")
  void soknadBarnFodselsdatoErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenSoknadBarnFodselsdato();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("soknadBarnFodselsdato kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når inntektDatoFra er null")
  void inntektDatoFraErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenInntektDatoFra();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("periodeDatoFra kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når inntektDatoTil er null")
  void inntektDatoTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenInntektDatoTil();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("periodeDatoTil kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når inntektBelop er null")
  void inntektBelopErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenInntektBelop();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("inntektBelop kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når sivilstandDatoFra er null")
  void sivilstandDatoFraErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenSivilstandDatoFra();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("periodeDatoFra kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når sivilstandDatoTil er null")
  void sivilstandDatoTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenSivilstandDatoTil();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("periodeDatoTil kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når sivilstandKode er null")
  void sivilstandKodeErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenSivilstandKode();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("sivilstandKode kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når barnDatoFra er null")
  void barnDatoFraErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBarnDatoFra();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("periodeDatoFra kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når barnDatoTil er null")
  void barnDatoTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBarnDatoTil();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("periodeDatoTil kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når beregnDatoFra er null")
  void beregnDatoFraErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBeregnDatoFra();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("beregnDatoFra kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når beregnDatoTil er null")
  void beregnDatoTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBeregnDatoTil();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("beregnDatoTil kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når soknadBarnBostatusPeriodeListe er null")
  void soknadBarnBostatusPeriodeListeErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenSoknadBarnBostatusPeriodeListe();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("soknadBarnBostatusPeriodeListe kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når bidragMottakerInntektPeriodeListe er null")
  void bidragMottakerInntektPeriodeListeErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBidragMottakerInntektPeriodeListe();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("bidragMottakerInntektPeriodeListe kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når bidragMottakerSivilstandPeriodeListe er null")
  void bidragMottakerSivilstandPeriodeListeErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBidragMottakerSivilstandPeriodeListe();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("bidragMottakerSivilstandPeriodeListe kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når soknadBarn er null")
  void soknadBarnErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenSoknadBarn();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("soknadBarn kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når bostatusDatoFraTil er null")
  void bostatusDatoFraTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenBostatusDatoFraTil();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("bostatusDatoFraTil kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når inntektDatoFraTil er null")
  void inntektDatoFraTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenInntektDatoFraTil();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("inntektDatoFraTil kan ikke være null");
  }

  @Test
  @DisplayName("Skal kaste IllegalArgumentException når sivilstandDatoFraTil er null")
  void sivilstandDatoFraTilErNull() {
    var grunnlag = TestUtil.byggForskuddGrunnlagUtenSivilstandDatoFraTil();
    assertThatIllegalArgumentException().isThrownBy(grunnlag::tilCore)
        .withMessage("sivilstandDatoFraTil kan ikke være null");
  }

  @Test
  @DisplayName("Skal ikke kaste exception")
  void skalIkkeKasteException() {
    var grunnlag = TestUtil.byggForskuddGrunnlag();
    assertThatCode(grunnlag::tilCore).doesNotThrowAnyException();
  }
}
