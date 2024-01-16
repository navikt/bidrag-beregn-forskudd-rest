package no.nav.bidrag.beregn.forskudd.rest.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.models.examples.Example
import no.nav.bidrag.beregn.forskudd.BeregnForskuddApi
import no.nav.bidrag.transport.behandling.beregning.felles.BeregnGrunnlag
import no.nav.security.token.support.core.api.Protected
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/beregn")
@Protected
class BeregnForskuddController(private val beregning: BeregnForskuddApi) {
    @Bean
    fun forskuddExample(): Example {
        val example = Example()
        example.value = BeregnForskuddController::class.java.getResource("/eksempler/beregn_request.json")?.readText() ?: ""
        example.description = "Forskudd beregning"
        return example
    }

    @PostMapping(path = ["/forskudd"])
    @Operation(summary = "Beregner forskudd")
    @SecurityRequirement(name = "bearer-key")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content =
        [
            Content(
                examples =
                [
                    ExampleObject(
                        ref = "#/components/examples/Forskudd beregning",
                        name = "Forskudd",
                    ),
                ],
            ),
        ],
    )
    fun beregnForskudd(@RequestBody beregnForskuddGrunnlag: BeregnGrunnlag) = beregning.beregn(beregnForskuddGrunnlag)
}
