package no.nav.bidrag.beregn.forskudd.service;

import no.nav.bidrag.beregn.forskudd.consumer.SjablonConsumer;
import no.nav.bidrag.beregn.forskudd.dto.BeregnForskuddGrunnlagDto;
import no.nav.bidrag.beregn.forskudd.dto.http.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.periode.ForskuddPeriode;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.ForskuddPeriodeGrunnlag;
import org.springframework.stereotype.Service;

@Service
public class BeregnService {

  private final SjablonConsumer sjablonConsumer;
  private final ForskuddPeriode forskuddPeriode;

  public BeregnService(SjablonConsumer sjablonConsumer, ForskuddPeriode forskuddPeriode) {
    this.sjablonConsumer = sjablonConsumer;
    this.forskuddPeriode = forskuddPeriode;
  }

  public BeregnForskuddResultat beregn(BeregnForskuddGrunnlagDto core) {
    //TODO
//    forskuddPeriode.beregnPerioder(core);
    forskuddPeriode.beregnPerioder(new ForskuddPeriodeGrunnlag());
    return new BeregnForskuddResultat();
  }
}
