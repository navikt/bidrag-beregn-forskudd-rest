package no.nav.bidrag.beregn.forskudd.rest

import no.nav.bidrag.commons.web.DefaultCorsFilter
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class, ManagementWebSecurityAutoConfiguration::class])
@EnableJwtTokenValidation(ignore = ["org.springframework", "org.springdoc"])
@Import(DefaultCorsFilter::class)
class BidragBeregnForskudd
fun main(args: Array<String>) {
    runApplication<BidragBeregnForskudd>(*args)
}
