package no.nav.bidrag.beregn.forskudd.rest.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import no.nav.bidrag.beregn.forskudd.core.ForskuddCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.dto.PeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer;
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddResultat;
import no.nav.bidrag.commons.web.HttpStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BeregnService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BeregnService.class);

  private final SjablonConsumer sjablonConsumer;
  private final ForskuddCore forskuddCore;

  public BeregnService(SjablonConsumer sjablonConsumer, ForskuddCore forskuddCore) {
    this.sjablonConsumer = sjablonConsumer;
    this.forskuddCore = forskuddCore;
  }

  public HttpStatusResponse<BeregnForskuddResultat> beregn(BeregnForskuddGrunnlagCore grunnlagTilCore) {

    var sjablonResponse = sjablonConsumer.hentSjablontall();

    if (sjablonResponse == null) {
      LOGGER.error("Feil ved kall av bidrag-sjablon. Ingen respons");
      return new HttpStatusResponse(HttpStatus.NO_CONTENT, null);
    }

    if (!(sjablonResponse.getHttpStatus().is2xxSuccessful())) {
      LOGGER.error("Feil ved kall av bidrag-sjablon. Status: {}", sjablonResponse.getHttpStatus().toString());
      return new HttpStatusResponse(sjablonResponse.getHttpStatus(), null);
    }

    grunnlagTilCore.setSjablonPeriodeListe(mapSjablonVerdier(sjablonResponse.getBody()));
    var resultatFraCore = forskuddCore.beregnForskudd(grunnlagTilCore);
    LOGGER.debug("Forskudd - grunnlag for beregning: {}", grunnlagTilCore);
    LOGGER.debug("Forskudd - resultat av beregning: {}", resultatFraCore);
    return new HttpStatusResponse(HttpStatus.OK, new BeregnForskuddResultat(resultatFraCore));
  }

  private List<SjablonPeriodeCore> mapSjablonVerdier(List<Sjablontall> sjablontallListe) {
    return sjablontallListe
        .stream()
        .filter(Sjablontall::erGyldigSjablon)
        .map(sTL -> new SjablonPeriodeCore(new PeriodeCore(sTL.getDatoFom(), sTL.getDatoTom()), sTL.getTypeSjablon(), sTL.getVerdi()))
        .collect(toList());
  }
}
