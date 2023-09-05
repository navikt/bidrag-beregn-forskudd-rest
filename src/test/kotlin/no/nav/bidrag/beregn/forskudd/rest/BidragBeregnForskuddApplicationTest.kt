package no.nav.bidrag.beregn.forskudd.rest

import no.nav.bidrag.beregn.forskudd.rest.BidragBeregnForskuddTest.Companion.TEST_PROFILE
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [BidragBeregnForskuddTest::class], webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(TEST_PROFILE)
@DisplayName("BidragBeregnForskudd")
@AutoConfigureWireMock(port = 0)
@EnableMockOAuth2Server
class BidragBeregnForskuddApplicationTest {
    @Test
    fun contextLoads() {
    }
}
