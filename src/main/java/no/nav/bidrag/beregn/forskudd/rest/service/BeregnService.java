package no.nav.bidrag.beregn.forskudd.rest.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import no.nav.bidrag.beregn.forskudd.periode.ForskuddPeriode;
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer;
import no.nav.bidrag.beregn.forskudd.rest.dto.BeregnForskuddGrunnlagDto;
import no.nav.bidrag.beregn.forskudd.rest.dto.SjablontallCore;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.Sjablontall;
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
    var sjablonResponse = sjablonConsumer.hentSjablontall();
    core.setSjablonVerdiListe(mapSjablonVerdier(sjablonResponse.getBody()));
//    var resultatFraCore = forskuddPeriode.beregnPerioder(core);
//    return new BeregnForskuddResultat(resultatFraCore);
    return new BeregnForskuddResultat();
  }

  private List<SjablontallCore> mapSjablonVerdier(List<Sjablontall> sjablontallListe) {
    return sjablontallListe.stream().filter(Sjablontall::erGyldigSjablon)
        .map(sTL -> new SjablontallCore(sTL.getTypeSjablon(), sTL.getDatoFom(), sTL.getDatoTom(), sTL.getVerdi()))
        .collect(toList());
  }
}
