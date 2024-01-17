package no.nav.bidrag.beregn.forskudd.rest.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.models.examples.Example
import no.nav.bidrag.beregn.forskudd.BeregnForskuddApi
import no.nav.bidrag.commons.util.OpenApiExample
import no.nav.bidrag.transport.behandling.beregning.felles.BeregnGrunnlag
import no.nav.security.token.support.core.api.Protected
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/beregn")
@Protected
class BeregnForskuddController(private val beregning: BeregnForskuddApi) {

    val eksempelfiler = (1..23).map { "forskudd_eksempel$it" }
    private fun createExample(filename: String): OpenApiExample {
        val example = Example()
        example.value = BeregnForskuddController::class.java.getResource("/testfiler/$filename.json")?.readText() ?: ""
        example.description = filename
        return OpenApiExample(
            example = example,
            path = "/beregn/forskudd",
            method = HttpMethod.POST,
        )
    }

    @Bean
    fun eksempler(): List<OpenApiExample> = eksempelfiler.map { createExample(it) }

    @PostMapping(path = ["/forskudd"])
    @Operation(summary = "Beregner forskudd")
    @SecurityRequirement(name = "bearer-key")
    fun beregnForskudd(@RequestBody beregnForskuddGrunnlag: BeregnGrunnlag) = beregning.beregn(beregnForskuddGrunnlag)
}
