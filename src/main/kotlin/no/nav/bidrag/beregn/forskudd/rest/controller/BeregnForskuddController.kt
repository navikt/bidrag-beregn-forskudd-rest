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

    companion object {
        val eksempelfiler = (1..23).map { "forskudd_eksempel$it" }
        private fun createExample(filename: String): Example {
            val example = Example()
            example.value = BeregnForskuddController::class.java.getResource("/testfiler/$filename.json")?.readText() ?: ""
            example.description = filename
            return example
        }
    }

    @Bean
    fun eksempel1(): List<Example> = eksempelfiler.map { createExample(it) }

    @PostMapping(path = ["/forskudd"])
    @Operation(summary = "Beregner forskudd")
    @SecurityRequirement(name = "bearer-key")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content =
        [
            Content(
                examples = [
                    ExampleObject(
                        ref = "#/components/examples/forskudd_eksempel1",
                        name = "forskudd_eksempel1",
                    ),
                    ExampleObject(
                        ref = "#/components/examples/forskudd_eksempel2",
                        name = "forskudd_eksempel2",
                    ),
                    ExampleObject(
                        ref = "#/components/examples/forskudd_eksempel3",
                        name = "forskudd_eksempel3",
                    ),
                    ExampleObject(
                        ref = "#/components/examples/forskudd_eksempel4",
                        name = "forskudd_eksempel4",
                    ),
                    ExampleObject(
                        ref = "#/components/examples/forskudd_eksempel5",
                        name = "forskudd_eksempel5",
                    ),
                    ExampleObject(
                        ref = "#/components/examples/forskudd_eksempel6",
                        name = "forskudd_eksempel6",
                    ),
                    ExampleObject(
                        ref = "#/components/examples/forskudd_eksempel7",
                        name = "forskudd_eksempel7",
                    ),
                ],
            ),
        ],
    )
    fun beregnForskudd(@RequestBody beregnForskuddGrunnlag: BeregnGrunnlag) = beregning.beregn(beregnForskuddGrunnlag)
}
