package no.nav.bidrag.beregn.forskudd.rest.service;

import static no.nav.bidrag.beregn.forskudd.rest.BidragBeregnForskudd.SECURE_LOGGER;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.forskudd.core.ForskuddCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnetForskuddResultatCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnetForskuddResultat;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.ResultatGrunnlag;
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

  public HttpResponse<BeregnetForskuddResultat> beregn(BeregnForskuddGrunnlag grunnlag) {

    // Kontroll av inputdata
    grunnlag.valider();

    // Henter sjabloner
    var sjablonSjablontallResponse = sjablonConsumer.hentSjablonSjablontall();
    if (LOGGER.isDebugEnabled() && sjablonSjablontallResponse.getResponseEntity().getBody() != null) {
      LOGGER.debug("Antall sjabloner hentet av type Sjablontall: {}", sjablonSjablontallResponse.getResponseEntity().getBody().size());
    }

    // Lager input-grunnlag til core-modulen
    var grunnlagTilCore = CoreMapper.mapGrunnlagTilCore(grunnlag, sjablonSjablontallResponse.getResponseEntity().getBody());

    BeregnetForskuddResultatCore resultatFraCore;

    // Kaller core-modulen for beregning av forskudd
    if (SECURE_LOGGER.isDebugEnabled()) {
      SECURE_LOGGER.debug("Forskudd - grunnlag for beregning: {}", grunnlagTilCore);
    }

    try {
      resultatFraCore = forskuddCore.beregnForskudd(grunnlagTilCore);
    } catch (Exception e) {
      throw new UgyldigInputException("Ugyldig input ved beregning av forskudd: " + e.getMessage());
    }

    if (!resultatFraCore.getAvvikListe().isEmpty()) {
      LOGGER.error("Ugyldig input ved beregning av forskudd. Følgende avvik ble funnet: " + System.lineSeparator()
          + resultatFraCore.getAvvikListe().stream().map(AvvikCore::getAvvikTekst).collect(Collectors.joining(System.lineSeparator())));
      SECURE_LOGGER.error("Ugyldig input ved beregning av forskudd. Følgende avvik ble funnet: " + System.lineSeparator()
          + resultatFraCore.getAvvikListe().stream().map(AvvikCore::getAvvikTekst).collect(Collectors.joining(System.lineSeparator())));
      SECURE_LOGGER.info("Forskudd - grunnlag for beregning: " + System.lineSeparator()
          + "beregnDatoFra= " + grunnlagTilCore.getBeregnDatoFra() + System.lineSeparator()
          + "beregnDatoTil= " + grunnlagTilCore.getBeregnDatoTil() + System.lineSeparator()
          + "soknadBarn= " + grunnlagTilCore.getSoknadBarn() + System.lineSeparator()
          + "barnIHusstandenPeriodeListe= " + grunnlagTilCore.getBarnIHusstandenPeriodeListe() + System.lineSeparator()
          + "inntektPeriodeListe= " + grunnlagTilCore.getInntektPeriodeListe() + System.lineSeparator()
          + "sivilstandPeriodeListe= " + grunnlagTilCore.getSivilstandPeriodeListe() + System.lineSeparator());
      throw new UgyldigInputException("Ugyldig input ved beregning av forskudd. Følgende avvik ble funnet: "
          + resultatFraCore.getAvvikListe().stream().map(AvvikCore::getAvvikTekst).collect(Collectors.joining("; ")));
    }

    if (SECURE_LOGGER.isDebugEnabled()) {
      SECURE_LOGGER.debug("Forskudd - resultat av beregning: {}", resultatFraCore.getBeregnetForskuddPeriodeListe());
    }

    var grunnlagReferanseListe = lagGrunnlagReferanseListe(grunnlag, resultatFraCore);
    return HttpResponse.Companion.from(HttpStatus.OK, new BeregnetForskuddResultat(resultatFraCore, grunnlagReferanseListe));
  }

  // Lager en liste over resultatgrunnlag som inneholder:
  //   - mottatte grunnlag som er brukt i beregningen
  //   - sjabloner som er brukt i beregningen
  private List<ResultatGrunnlag> lagGrunnlagReferanseListe(BeregnForskuddGrunnlag forskuddGrunnlag, BeregnetForskuddResultatCore resultatFraCore) {
    var mapper = new ObjectMapper();
    var resultatGrunnlagListe = new ArrayList<ResultatGrunnlag>();

    var grunnlagReferanseListe = resultatFraCore.getBeregnetForskuddPeriodeListe().stream()
        .map(ResultatPeriodeCore::getGrunnlagReferanseListe)
        .flatMap(Collection::stream)
        .distinct()
        .toList();

    // Matcher mottatte grunnlag med grunnlag som er brukt i beregningen
    resultatGrunnlagListe.addAll(forskuddGrunnlag.getGrunnlagListe().stream()
        .filter(grunnlag -> grunnlagReferanseListe.contains(grunnlag.getReferanse()))
        .map(grunnlag -> new ResultatGrunnlag(grunnlag.getReferanse(), grunnlag.getType(), grunnlag.getInnhold()))
        .toList());

    // Danner grunnlag basert på liste over sjabloner som er brukt i beregningen
    resultatGrunnlagListe.addAll(resultatFraCore.getSjablonListe().stream()
        .map(sjablon -> {
              var map = new LinkedHashMap<String, Object>();
              map.put("datoFom", mapDato(sjablon.getPeriode().getDatoFom()));
              map.put("datoTil", mapDato(sjablon.getPeriode().getDatoTil()));
              map.put("sjablonNavn", sjablon.getNavn());
              map.put("sjablonVerdi", sjablon.getVerdi().intValue());
              return new ResultatGrunnlag(sjablon.getReferanse(), "SJABLON", mapper.valueToTree(map));
            }
        )
        .toList());

    return resultatGrunnlagListe;
  }

  // Unngå å legge ut datoer høyere enn 9999-12-31
  private static String mapDato(LocalDate dato) {
    return dato.isAfter(LocalDate.parse("9999-12-31")) ? "9999-12-31" : dato.toString();
  }
}
