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
import no.nav.bidrag.beregn.forskudd.rest.exception.SjablonConsumerException;
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException;
import no.nav.bidrag.commons.web.HttpStatusResponse;
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

  public HttpStatusResponse<BeregnForskuddResultat> beregn(BeregnForskuddGrunnlagCore grunnlagTilCore) {

    var sjablonResponse = sjablonConsumer.hentSjablontall();

    if (sjablonResponse == null) {
      LOGGER.error("Feil ved kall av bidrag-sjablon. Ingen respons");
      throw new SjablonConsumerException("Feil ved kall av bidrag-sjablon. Ingen respons");
    }

    if (!(sjablonResponse.getHttpStatus().is2xxSuccessful())) {
      LOGGER.error("Feil ved kall av bidrag-sjablon. Status: {}", sjablonResponse.getHttpStatus().toString());
      throw new SjablonConsumerException("Feil ved kall av bidrag-sjablon. Status: " + sjablonResponse.getHttpStatus().toString() + " Melding: " +
          sjablonResponse.getBody());
    }

    grunnlagTilCore.setSjablonPeriodeListe(mapSjablonVerdier(sjablonResponse.getBody()));

    LOGGER.debug("Forskudd - grunnlag for beregning: {}", grunnlagTilCore);
    var resultatFraCore = forskuddCore.beregnForskudd(grunnlagTilCore);

    if (!resultatFraCore.getAvvikListe().isEmpty()) {
      LOGGER.error("Ugyldig input ved beregning av forskudd");
      LOGGER.error("Forskudd - grunnlag for beregning: {}", grunnlagTilCore);
      LOGGER.error("Forskudd - avvik: {}", resultatFraCore.getAvvikListe().stream().map(AvvikCore::getAvvikTekst).collect(Collectors.joining("; ")));
      throw new UgyldigInputException(resultatFraCore.getAvvikListe().stream().map(AvvikCore::getAvvikTekst).collect(Collectors.joining("; ")));
    }

    LOGGER.debug("Forskudd - resultat av beregning: {}", resultatFraCore.getResultatPeriodeListe());
    return new HttpStatusResponse(HttpStatus.OK, new BeregnForskuddResultat(resultatFraCore));
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
