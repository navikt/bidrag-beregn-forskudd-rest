package no.nav.bidrag.beregn.forskudd.service;

import no.nav.bidrag.beregn.forskudd.consumer.SjablonConsumer;
import no.nav.bidrag.beregn.forskudd.dto.BeregnForskuddDto;
import org.springframework.stereotype.Service;

@Service
public class BeregnService {

  public BeregnService(SjablonConsumer sjablonConsumer) {
    this.sjablonConsumer = sjablonConsumer;
  }

  private final SjablonConsumer sjablonConsumer;

  public void beregn(BeregnForskuddDto core) {
    // todo
  }
}
