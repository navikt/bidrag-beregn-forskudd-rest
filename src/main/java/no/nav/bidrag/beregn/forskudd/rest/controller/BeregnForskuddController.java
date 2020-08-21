package no.nav.bidrag.beregn.forskudd.rest.controller;

import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.rest.service.BeregnForskuddService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/beregn")
public class BeregnForskuddController {

  private final BeregnForskuddService beregnForskuddService;

  public BeregnForskuddController(BeregnForskuddService beregnForskuddService) {
    this.beregnForskuddService = beregnForskuddService;
  }

  @PostMapping(path = "/forskudd")
  public ResponseEntity<BeregnForskuddResultat> beregnForskudd(@RequestBody BeregnForskuddGrunnlag beregnForskuddGrunnlag) {
    var resultat = beregnForskuddService.beregn(beregnForskuddGrunnlag.tilCore());
    return new ResponseEntity<>(resultat.getResponseEntity().getBody(), resultat.getResponseEntity().getStatusCode());
  }
}
