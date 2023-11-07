package no.nav.bidrag.beregn.forskudd.rest.service

import no.nav.bidrag.beregn.forskudd.core.ForskuddCore
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore
import no.nav.bidrag.beregn.forskudd.rest.BidragBeregnForskuddTest
import no.nav.bidrag.beregn.forskudd.rest.TestUtil
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException
import no.nav.bidrag.commons.web.HttpResponse.Companion.from
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.capture
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockitoExtension::class)
@ActiveProfiles(BidragBeregnForskuddTest.TEST_PROFILE)
internal class BeregnForskuddServiceTest {
    @InjectMocks
    private lateinit var beregnForskuddService: BeregnForskuddService

    @Mock
    private lateinit var sjablonConsumerMock: SjablonConsumer

    @Mock
    private lateinit var forskuddCoreMock: ForskuddCore

    @Captor
    private lateinit var grunnlagTilCoreCaptor: ArgumentCaptor<BeregnForskuddGrunnlagCore>

    @Test
    @DisplayName("Skal beregne forskudd")
    fun skalBeregneForskudd() {
        `when`(sjablonConsumerMock.hentSjablonSjablontall()).thenReturn(from(HttpStatus.OK, TestUtil.dummySjablonSjablontallListe()))
        `when`(forskuddCoreMock.beregnForskudd(capture(grunnlagTilCoreCaptor))).thenReturn(TestUtil.dummyForskuddResultatCore())

        val beregnForskuddResultat = beregnForskuddService.beregn(TestUtil.byggForskuddGrunnlag())
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
    @DisplayName("Skal kaste UgyldigInputException ved feil retur fra Core")
    fun skalKasteUgyldigInputExceptionVedFeilReturFraCore() {
        `when`(sjablonConsumerMock.hentSjablonSjablontall()).thenReturn(from(HttpStatus.OK, TestUtil.dummySjablonSjablontallListe()))
        `when`(forskuddCoreMock.beregnForskudd(any())).thenReturn(TestUtil.dummyForskuddResultatCoreMedAvvik())
        assertThatExceptionOfType(UgyldigInputException::class.java)
            .isThrownBy { beregnForskuddService.beregn(TestUtil.byggForskuddGrunnlag()) }
            .withMessageContaining("beregnDatoFra kan ikke være null")
            .withMessageContaining("periodeDatoTil må være etter periodeDatoFra")
    }

    companion object MockitoHelper {
        fun <T> any(): T = Mockito.any()
    }
}
