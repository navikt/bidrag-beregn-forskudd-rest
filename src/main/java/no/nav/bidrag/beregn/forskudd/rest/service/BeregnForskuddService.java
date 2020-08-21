package no.nav.bidrag.beregn.forskudd.rest.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Collectors;
import no.nav.bidrag.beregn.forskudd.core.ForskuddCore;
import no.nav.bidrag.beregn.forskudd.core.dto.AvvikCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.dto.PeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer;
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException;
import no.nav.bidrag.commons.web.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BeregnForskuddService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BeregnForskuddService.class);

  private final SjablonConsumer sjablonConsumer;
  private final ForskuddCore forskuddCore;

  public BeregnForskuddService(SjablonConsumer sjablonConsumer, ForskuddCore forskuddCore) {
    this.sjablonConsumer = sjablonConsumer;
    this.forskuddCore = forskuddCore;
  }

  public HttpResponse<BeregnForskuddResultat> beregn(BeregnForskuddGrunnlagCore grunnlagTilCore) {

    var sjablonResponse = sjablonConsumer.hentSjablontall();
    LOGGER.debug("Antall sjabloner hentet av type Sjablontall: {}", sjablonResponse.getResponseEntity().getBody().size());

    grunnlagTilCore.setSjablonPeriodeListe(mapSjablonVerdier(sjablonResponse.getResponseEntity().getBody()));

    LOGGER.debug("Forskudd - grunnlag for beregning: {}", grunnlagTilCore);
    var resultatFraCore = forskuddCore.beregnForskudd(grunnlagTilCore);

    if (!resultatFraCore.getAvvikListe().isEmpty()) {
      LOGGER.error("Ugyldig input ved beregning av forskudd" + System.lineSeparator()
          + "Forskudd - grunnlag for beregning: " + grunnlagTilCore + System.lineSeparator()
          + "Forskudd - avvik: " + resultatFraCore.getAvvikListe().stream().map(AvvikCore::getAvvikTekst).collect(Collectors.joining("; ")));
      throw new UgyldigInputException(resultatFraCore.getAvvikListe().stream().map(AvvikCore::getAvvikTekst).collect(Collectors.joining("; ")));
    }

    LOGGER.debug("Forskudd - resultat av beregning: {}", resultatFraCore.getResultatPeriodeListe());
    return HttpResponse.from(HttpStatus.OK, new BeregnForskuddResultat(resultatFraCore));
  }

  //Plukker ut aktuelle sjabloner og flytter inn i inputen til core-modulen
  private List<SjablonPeriodeCore> mapSjablonVerdier(List<Sjablontall> sjablontallListe) {
    return sjablontallListe
        .stream()
        .filter(Sjablontall::erGyldigSjablon)
        .map(sTL -> new SjablonPeriodeCore(new PeriodeCore(sTL.getDatoFom(), sTL.getDatoTom()), sTL.getTypeSjablon(), sTL.getVerdi()))
        .collect(toList());
  }
}
