package no.nav.bidrag.beregn.forskudd.rest.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.bidrag.beregn.forskudd.rest.service.BeregnForskuddService
import no.nav.bidrag.transport.behandling.beregning.felles.BeregnGrunnlag
import no.nav.bidrag.transport.behandling.beregning.forskudd.BeregnetForskuddResultat
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/beregn")
@Protected
class BeregnForskuddController(private val beregnForskuddService: BeregnForskuddService) {
    @PostMapping(path = ["/forskudd"])
    @Operation(summary = "Beregner forskudd")
    @SecurityRequirement(name = "bearer-key")
    fun beregnForskudd(
        @RequestBody beregnForskuddGrunnlag: BeregnGrunnlag,
    ): ResponseEntity<BeregnetForskuddResultat> {
        val resultat = beregnForskuddService.beregn(beregnForskuddGrunnlag)
        return ResponseEntity(resultat.responseEntity.body, resultat.responseEntity.statusCode)
    }
}
