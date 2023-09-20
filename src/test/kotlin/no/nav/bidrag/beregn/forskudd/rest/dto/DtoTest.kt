package no.nav.bidrag.beregn.forskudd.rest.dto

import no.nav.bidrag.beregn.forskudd.rest.TestUtil
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException
import no.nav.bidrag.beregn.forskudd.rest.service.valider
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("DtoTest")
internal class DtoTest {
    @Test
    @DisplayName("Skal kaste IllegalArgumentException når beregnDatoFra er null")
    fun skalKasteIllegalArgumentExceptionNaarBeregnDatoFraErNull() {
        val grunnlag = TestUtil.byggForskuddGrunnlagUtenBeregnDatoFra()
        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java).isThrownBy { grunnlag.valider() }
            .withMessage("beregnDatoFra kan ikke være null")
    }

    @Test
    @DisplayName("Skal kaste IllegalArgumentException når beregnDatoTil er null")
    fun skalKasteIllegalArgumentExceptionNaarBeregnDatoTilErNull() {
        val grunnlag = TestUtil.byggForskuddGrunnlagUtenBeregnDatoTil()
        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java).isThrownBy { grunnlag.valider() }
            .withMessage("beregnDatoTil kan ikke være null")
    }

    @Test
    @DisplayName("Skal kaste IllegalArgumentException når grunnlagListe er null")
    fun skalKasteIllegalArgumentExceptionNaarGrunnlagListeErNull() {
        val grunnlag = TestUtil.byggForskuddGrunnlagUtenGrunnlagListe()
        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java).isThrownBy { grunnlag.valider() }
            .withMessage("grunnlagListe kan ikke være null")
    }

    @Test
    @DisplayName("Skal kaste IllegalArgumentException når referanse er null")
    fun skalKasteIllegalArgumentExceptionNaarReferanseErNull() {
        val grunnlag = TestUtil.byggForskuddGrunnlagUtenReferanse()
        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java).isThrownBy { grunnlag.valider() }
            .withMessage("referanse kan ikke være null")
    }

    @Test
    @DisplayName("Skal kaste IllegalArgumentException når type er null")
    fun skalKasteIllegalArgumentExceptionNaarTypeErNull() {
        val grunnlag = TestUtil.byggForskuddGrunnlagUtenType()
        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java).isThrownBy { grunnlag.valider() }
            .withMessage("type kan ikke være null")
    }

    @Test
    @DisplayName("Skal kaste IllegalArgumentException når innhold er null")
    fun skalKasteIllegalArgumentExceptionNaarInnholdErNull() {
        val grunnlag = TestUtil.byggForskuddGrunnlagUtenInnhold()
        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java).isThrownBy { grunnlag.valider() }
            .withMessage("innhold kan ikke være null")
    }

    @Test
    @DisplayName("Skal ikke kaste exception")
    fun skalIkkeKasteException() {
        val grunnlag = TestUtil.byggDummyForskuddGrunnlag()
        Assertions.assertThatCode { grunnlag.valider() }.doesNotThrowAnyException()
    }
}
