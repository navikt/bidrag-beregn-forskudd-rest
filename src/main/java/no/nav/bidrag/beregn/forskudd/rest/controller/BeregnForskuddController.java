package no.nav.bidrag.beregn.forskudd.rest.controller;

import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.rest.service.BeregnService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/beregn")
public class BeregnForskuddController {

  private final BeregnService beregnService;

  public BeregnForskuddController(BeregnService beregnService) {
    this.beregnService = beregnService;
  }

  @RequestMapping(path = "/forskudd")
  public ResponseEntity<BeregnForskuddResultat> beregnForskudd(@RequestBody BeregnForskuddGrunnlag beregnForskuddGrunnlag) {
    BeregnForskuddResultat resultat = beregnService.beregn(beregnForskuddGrunnlag.hentCore());
    return new ResponseEntity<>(resultat, HttpStatus.OK);
  }
}
