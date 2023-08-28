package no.nav.bidrag.beregn.forskudd.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import no.nav.bidrag.beregn.forskudd.rest.consumer.wiremock_stub.SjablonApiStub
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class, ManagementWebSecurityAutoConfiguration::class])
@EnableJwtTokenValidation(ignore = ["org.springdoc", "org.springframework"])
@ComponentScan(excludeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = [BidragBeregnForskudd::class])])
open class BidragBeregnForskuddLocal {
    companion object {
        const val LOCAL_PROFILE = "local"
    }
    fun main(args: Array<String>) {

        val wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort().dynamicHttpsPort()) // No-args constructor will start on port 8080, no HTTPS
        wireMockServer.start()

        val app = SpringApplication(BidragBeregnForskuddLocal::class.java)
        app.setAdditionalProfiles(LOCAL_PROFILE)
        val context = app.run(*args)
        context.getBean(SjablonApiStub::class.java).settOppSjablonStub()

        wireMockServer.resetAll()
        wireMockServer.stop()

    }
}
