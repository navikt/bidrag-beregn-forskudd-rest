package no.nav.bidrag.beregning.forskudd.controller;

import no.nav.bidrag.beregning.forskudd.dto.http.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregning.forskudd.dto.http.BeregnForskuddResultat;
import no.nav.bidrag.beregning.forskudd.service.BeregnService;
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
