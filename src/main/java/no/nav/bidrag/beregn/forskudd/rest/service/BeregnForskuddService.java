package no.nav.bidrag.beregn.forskudd.rest.service;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.forskudd.core.ForskuddCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
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

  private LocalDate beregnDatoFra;
  private LocalDate beregnDatoTil;
  private Map<String, SjablonTallNavn> sjablontallMap = new HashMap<>();

  public BeregnForskuddService(SjablonConsumer sjablonConsumer, ForskuddCore forskuddCore) {
    this.sjablonConsumer = sjablonConsumer;
    this.forskuddCore = forskuddCore;
  }

  public HttpResponse<BeregnForskuddResultat> beregn(BeregnForskuddGrunnlagCore grunnlagTilCore) {

    // Initier beregnDatoFra og beregnDatoTil
    beregnDatoFra = grunnlagTilCore.getBeregnDatoFra();
    beregnDatoTil = grunnlagTilCore.getBeregnDatoTil();

    // Lager en map for sjablontall (id og navn)
    for (SjablonTallNavn sjablonTallNavn : SjablonTallNavn.values()) {
      sjablontallMap.put(sjablonTallNavn.getId(), sjablonTallNavn);
    }

    //Henter sjabloner
    var sjablonSjablontallResponse = sjablonConsumer.hentSjablonSjablontall();
    LOGGER.debug("Antall sjabloner hentet av type Sjablontall: {}", sjablonSjablontallResponse.getResponseEntity().getBody().size());

    grunnlagTilCore.setSjablonPeriodeListe(mapSjablonVerdier(sjablonSjablontallResponse.getResponseEntity().getBody()));

    // Kaller core-modulen for beregning av forskudd
    LOGGER.debug("Forskudd - grunnlag for beregning: {}", grunnlagTilCore);
    var resultatFraCore = forskuddCore.beregnForskudd(grunnlagTilCore);

    if (!resultatFraCore.getAvvikListe().isEmpty()) {
      LOGGER.error("Ugyldig input ved beregning av forskudd. Følgende avvik ble funnet: " + System.lineSeparator()
          + resultatFraCore.getAvvikListe().stream().map(AvvikCore::getAvvikTekst).collect(Collectors.joining(System.lineSeparator())));
      LOGGER.info("Forskudd - grunnlag for beregning: " + System.lineSeparator()
          + "beregnDatoFra= " + grunnlagTilCore.getBeregnDatoFra() + System.lineSeparator()
          + "beregnDatoTil= " + grunnlagTilCore.getBeregnDatoTil() + System.lineSeparator()
          + "soknadBarn= " + grunnlagTilCore.getSoknadBarn() + System.lineSeparator()
          + "bidragMottakerBarnPeriodeListe= " + grunnlagTilCore.getBidragMottakerBarnPeriodeListe() + System.lineSeparator()
          + "bidragMottakerInntektPeriodeListe= " + grunnlagTilCore.getBidragMottakerInntektPeriodeListe() + System.lineSeparator()
          + "bidragMottakerSivilstandPeriodeListe= " + grunnlagTilCore.getBidragMottakerSivilstandPeriodeListe() + System.lineSeparator());
      throw new UgyldigInputException("Ugyldig input ved beregning av forskudd. Følgende avvik ble funnet: "
          + resultatFraCore.getAvvikListe().stream().map(AvvikCore::getAvvikTekst).collect(Collectors.joining("; ")));

    }

    LOGGER.debug("Forskudd - resultat av beregning: {}", resultatFraCore.getResultatPeriodeListe());
    return HttpResponse.from(HttpStatus.OK, new BeregnForskuddResultat(resultatFraCore));
  }

  //Plukker ut aktuelle sjabloner og flytter inn i inputen til core-modulen
  private List<SjablonPeriodeCore> mapSjablonVerdier(List<Sjablontall> sjablonSjablontallListe) {
    return sjablonSjablontallListe
        .stream()
        .filter(sjablon -> (!(sjablon.getDatoFom().isAfter(beregnDatoTil)) && (!(sjablon.getDatoTom().isBefore(beregnDatoFra)))))
        .filter(sjablon -> filtrerSjablonTall(sjablontallMap.getOrDefault(sjablon.getTypeSjablon(), SjablonTallNavn.DUMMY)))
        .map(sjablon -> new SjablonPeriodeCore(
            new PeriodeCore(sjablon.getDatoFom(), sjablon.getDatoTom()),
            sjablontallMap.getOrDefault(sjablon.getTypeSjablon(), SjablonTallNavn.DUMMY).getNavn(),
            emptyList(),
            singletonList(new SjablonInnholdCore(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), sjablon.getVerdi()))))
        .collect(toList());
  }

  // Sjekker om en type SjablonTall er i bruk for forskudd
  private boolean filtrerSjablonTall(SjablonTallNavn sjablonTallNavn) {
    return sjablonTallNavn.getForskudd();
  }
}
