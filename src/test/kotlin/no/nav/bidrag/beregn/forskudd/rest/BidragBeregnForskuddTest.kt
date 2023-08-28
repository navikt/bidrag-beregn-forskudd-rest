package no.nav.bidrag.beregn.forskudd.rest

import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class, ManagementWebSecurityAutoConfiguration::class])
@ComponentScan(
    excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        value = [BidragBeregnForskudd::class, BidragBeregnForskuddLocal::class]
    )]
)
open class BidragBeregnForskuddTest {
//    @JvmStatic
    fun main(args: Array<String>) {
        val app = SpringApplication(BidragBeregnForskuddTest::class.java)
        app.run(*args)
    }
}
