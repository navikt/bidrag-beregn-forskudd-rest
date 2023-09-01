package no.nav.bidrag.beregn.forskudd.rest.service

import no.nav.bidrag.beregn.forskudd.core.ForskuddCore
import no.nav.bidrag.beregn.forskudd.rest.BidragBeregnForskuddTest
import no.nav.bidrag.beregn.forskudd.rest.TestUtil
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException
import no.nav.bidrag.commons.web.HttpResponse.Companion.from
import no.nav.bidrag.transport.beregning.forskudd.core.request.BeregnForskuddGrunnlagCore
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@DisplayName("BeregnForskuddServiceTest")
@ActiveProfiles(BidragBeregnForskuddTest.TEST_PROFILE)
internal class BeregnForskuddServiceTest {
    @InjectMocks
    private val beregnForskuddService: BeregnForskuddService? = null

    @Mock
    private val sjablonConsumerMock: SjablonConsumer? = null

    @Mock
    private val forskuddCoreMock: ForskuddCore? = null

    @Test
    @DisplayName("Skal beregne forskudd")
    @Disabled
    fun skalBeregneForskudd() {
        val grunnlagTilCoreCaptor = ArgumentCaptor.forClass(BeregnForskuddGrunnlagCore::class.java)

        Mockito.`when`(sjablonConsumerMock!!.hentSjablonSjablontall()).thenReturn(from(HttpStatus.OK, TestUtil.dummySjablonSjablontallListe()))
        Mockito.`when`(forskuddCoreMock!!.beregnForskudd(grunnlagTilCoreCaptor.capture())).thenReturn(TestUtil.dummyForskuddResultatCore())

        val beregnForskuddResultat = beregnForskuddService!!.beregn(TestUtil.byggForskuddGrunnlag())
        val grunnlagTilCore = grunnlagTilCoreCaptor.value

        assertAll(
            Executable { assertThat(beregnForskuddResultat.responseEntity.statusCode).isEqualTo(HttpStatus.OK) },
            Executable { assertThat(beregnForskuddResultat.responseEntity.body).isNotNull() },
            Executable { assertThat(beregnForskuddResultat.responseEntity.body?.beregnetForskuddPeriodeListe).isNotNull() },
            Executable { assertThat(beregnForskuddResultat.responseEntity.body?.beregnetForskuddPeriodeListe).hasSize(1) },
            // Sjablontyper som ikke er gyldige for forskudd og sjabloner som ikke er innenfor beregn-fra-til-dato filtreres bort
            Executable { assertThat(grunnlagTilCore.sjablonPeriodeListe).hasSize(21) }
        )
    }

    @Test
    @Disabled
    @DisplayName("Skal kaste UgyldigInputException ved feil retur fra Core")
    fun skalKasteUgyldigInputExceptionVedFeilReturFraCore() {
        Mockito.`when`(sjablonConsumerMock!!.hentSjablonSjablontall()).thenReturn(from(HttpStatus.OK, TestUtil.dummySjablonSjablontallListe()))
        Mockito.`when`(forskuddCoreMock!!.beregnForskudd(ArgumentMatchers.any())).thenReturn(TestUtil.dummyForskuddResultatCoreMedAvvik())
        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java)
            .isThrownBy { beregnForskuddService!!.beregn(TestUtil.byggForskuddGrunnlag()) }
            .withMessageContaining("beregnDatoFra kan ikke være null")
            .withMessageContaining("periodeDatoTil må være etter periodeDatoFra")
    }

    @Test
    @Disabled
    @DisplayName("Skal kaste UgyldigInputException ved ugyldig datoFom format")
    fun skalKasteUgyldigInputExceptionVedUgyldigDatoFomFormat() {
        Mockito.`when`(sjablonConsumerMock!!.hentSjablonSjablontall()).thenReturn(from(HttpStatus.OK, TestUtil.dummySjablonSjablontallListe()))
        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java)
            .isThrownBy { beregnForskuddService!!.beregn(TestUtil.byggForskuddGrunnlag("2017-xx-01", "2020-01-01", "2006-12-01", "1.0", "290000")) }
            .withMessage("Dato 2017-xx-01 av type datoFom i objekt av type BARN_I_HUSSTAND har feil format")
    }

    @Test
    @DisplayName("Skal kaste UgyldigInputException ved ugyldig datoTom format")
    fun skalKasteUgyldigInputExceptionVedUgyldigDatoTomFormat() {
        Mockito.`when`(sjablonConsumerMock!!.hentSjablonSjablontall()).thenReturn(from(HttpStatus.OK, TestUtil.dummySjablonSjablontallListe()))
        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java)
            .isThrownBy { beregnForskuddService!!.beregn(TestUtil.byggForskuddGrunnlag("2017-01-01", "2020-xx-01", "2006-12-01", "1.0", "290000")) }
            .withMessage("Dato 2020-xx-01 av type datoTil i objekt av type BARN_I_HUSSTAND har feil format")
    }

    @Test
    @DisplayName("Skal kaste UgyldigInputException ved ugyldig fodselsdato format")
    fun skalKasteUgyldigInputExceptionVedUgyldigFodselsdatoFormat() {
        Mockito.`when`(sjablonConsumerMock!!.hentSjablonSjablontall()).thenReturn(from(HttpStatus.OK, TestUtil.dummySjablonSjablontallListe()))
        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java)
            .isThrownBy { beregnForskuddService!!.beregn(TestUtil.byggForskuddGrunnlag("2017-01-01", "2020-01-01", "2006-xx-01", "1.0", "290000")) }
            .withMessage("Dato 2006-xx-01 av type fodselsdato i objekt av type SOKNADSBARN_INFO har feil format")
    }

    @Test
    @DisplayName("Skal kaste UgyldigInputException ved ugyldig antall barn i husstand format")
    fun skalKasteUgyldigInputExceptionVedUgyldigAntallBarnIHusstandFormat() {
        Mockito.`when`(sjablonConsumerMock!!.hentSjablonSjablontall()).thenReturn(from(HttpStatus.OK, TestUtil.dummySjablonSjablontallListe()))
        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java)
            .isThrownBy { beregnForskuddService!!.beregn(TestUtil.byggForskuddGrunnlag("2017-01-01", "2020-01-01", "2006-12-01", "1.x", "290000")) }
            .withMessage("antall 1.x i objekt av type BARN_I_HUSSTAND har feil format")
    }

    @Test
    @DisplayName("Skal kaste UgyldigInputException ved beløp inntekt format")
    fun skalKasteUgyldigInputExceptionVedUgyldigBelopInntektFormat() {
        Mockito.`when`(sjablonConsumerMock!!.hentSjablonSjablontall()).thenReturn(from(HttpStatus.OK, TestUtil.dummySjablonSjablontallListe()))
        Assertions.assertThatExceptionOfType(UgyldigInputException::class.java)
            .isThrownBy { beregnForskuddService!!.beregn(TestUtil.byggForskuddGrunnlag("2017-01-01", "2020-01-01", "2006-12-01", "1.0", "29x000")) }
            .withMessage("belop 29x000 i objekt av type INNTEKT har feil format")
    }
}
