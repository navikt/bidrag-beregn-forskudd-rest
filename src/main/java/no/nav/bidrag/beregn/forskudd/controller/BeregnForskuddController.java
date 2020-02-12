package no.nav.bidrag.beregn.forskudd.controller;

import no.nav.bidrag.beregn.forskudd.service.BeregnService;
import no.nav.bidrag.beregn.forskudd.dto.http.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.dto.http.BeregnForskuddResultat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping(path = "/forskudd")
  public ResponseEntity<BeregnForskuddResultat> get(@RequestBody BeregnForskuddGrunnlag beregnForskuddGrunnlag) {
    beregnService.beregn(beregnForskuddGrunnlag.hentCore());
    return new ResponseEntity<>(new BeregnForskuddResultat("Hello world"), HttpStatus.OK);
  }
}
