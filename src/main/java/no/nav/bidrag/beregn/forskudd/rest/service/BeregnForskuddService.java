package no.nav.bidrag.beregn.forskudd.rest.service;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.forskudd.core.ForskuddCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BarnPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnetForskuddResultatCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BostatusPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SivilstandPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SoknadBarnCore;
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer;
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnetForskuddResultat;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.Grunnlag;
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

  private static final String INNTEKT_TYPE = "Inntekt";
  private static final String SIVILSTAND_TYPE = "Sivilstand";
  private static final String BARN_TYPE = "Barn";

  private static final String GENERELL_INFO_TYPE = "GenerellInfo";
  private static final String BOSTATUS_TYPE = "Bostatus";

  private final SjablonConsumer sjablonConsumer;
  private final ForskuddCore forskuddCore;

  public BeregnForskuddService(SjablonConsumer sjablonConsumer, ForskuddCore forskuddCore) {
    this.sjablonConsumer = sjablonConsumer;
    this.forskuddCore = forskuddCore;
  }

  public HttpResponse<BeregnetForskuddResultat> beregn(BeregnForskuddGrunnlag grunnlag) {

    // Kontroll av inputdata
    grunnlag.valider();

    // Lager en map for sjablontall (id og navn)
    var sjablontallMap = new HashMap<String, SjablonTallNavn>();
    for (SjablonTallNavn sjablonTallNavn : SjablonTallNavn.values()) {
      sjablontallMap.put(sjablonTallNavn.getId(), sjablonTallNavn);
    }

    // Henter sjabloner
    var sjablonSjablontallResponse = sjablonConsumer.hentSjablonSjablontall();
    LOGGER.debug("Antall sjabloner hentet av type Sjablontall: {}", sjablonSjablontallResponse.getResponseEntity().getBody().size());

    // Lager input-grunnlag til core-modulen
    var grunnlagTilCore = mapGrunnlagTilCore(grunnlag, sjablontallMap, sjablonSjablontallResponse.getResponseEntity().getBody());

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

    LOGGER.debug("Forskudd - resultat av beregning: {}", resultatFraCore.getBeregnetForskuddPeriodeListe());
    var grunnlagReferanseListe = lagGrunnlagReferanseListe(grunnlag, resultatFraCore);
    return HttpResponse.from(HttpStatus.OK, new BeregnetForskuddResultat(resultatFraCore, grunnlagReferanseListe));
  }

  private BeregnForskuddGrunnlagCore mapGrunnlagTilCore(BeregnForskuddGrunnlag beregnForskuddGrunnlag,
      HashMap<String, SjablonTallNavn> sjablontallMap, List<Sjablontall> sjablontallListe) {

    LocalDate soknadBarnFodselsdato = null;
    String soknadBarnReferanse = null;
    var bostatusPeriodeCoreListe = new ArrayList<BostatusPeriodeCore>();
    var inntektPeriodeCoreListe = new ArrayList<InntektPeriodeCore>();
    var sivilstandPeriodeCoreListe = new ArrayList<SivilstandPeriodeCore>();
    var barnPeriodeCoreListe = new ArrayList<BarnPeriodeCore>();

    for (Grunnlag grunnlag : beregnForskuddGrunnlag.getGrunnlagListe()) {
      switch (grunnlag.getType()) {
        case GENERELL_INFO_TYPE -> {
          soknadBarnFodselsdato = mapFodselsdato(grunnlag);
          soknadBarnReferanse = grunnlag.getReferanse();
        }
        case BOSTATUS_TYPE -> bostatusPeriodeCoreListe.add(mapBostatus(grunnlag));
        case INNTEKT_TYPE -> inntektPeriodeCoreListe.add(mapInntekt(grunnlag));
        case SIVILSTAND_TYPE -> sivilstandPeriodeCoreListe.add(mapSivilstand(grunnlag));
        case BARN_TYPE -> barnPeriodeCoreListe.add(mapBarn(grunnlag));
      }
    }

    var soknadBarnCore = new SoknadBarnCore(soknadBarnReferanse, soknadBarnFodselsdato, bostatusPeriodeCoreListe);

    var sjablonPeriodeCoreListe = mapSjablonVerdier(beregnForskuddGrunnlag.getBeregnDatoFra(), beregnForskuddGrunnlag.getBeregnDatoTil(),
        sjablontallListe, sjablontallMap);

    return new BeregnForskuddGrunnlagCore(beregnForskuddGrunnlag.getBeregnDatoFra(), beregnForskuddGrunnlag.getBeregnDatoTil(), soknadBarnCore,
        inntektPeriodeCoreListe, sivilstandPeriodeCoreListe, barnPeriodeCoreListe, sjablonPeriodeCoreListe);
  }

  private LocalDate mapFodselsdato(Grunnlag grunnlag) {
    var fodselsdato = Optional.of(grunnlag.getInnhold().get("fodselsdato"))
        .orElseThrow(() -> new UgyldigInputException("fodselsdato mangler i objekt av type GenerellInfo")).asText();
    return LocalDate.parse(fodselsdato);
  }

  private BostatusPeriodeCore mapBostatus(Grunnlag grunnlag) {
    var bostatusKode = Optional.of(grunnlag.getInnhold().get("bostatusKode"))
        .orElseThrow(() -> new UgyldigInputException("bostatusKode mangler i objekt av type Bostatus")).asText();
    return new BostatusPeriodeCore(grunnlag.getReferanse(), mapPeriode(grunnlag.getInnhold(), grunnlag.getType()), bostatusKode);
  }

  private InntektPeriodeCore mapInntekt(Grunnlag grunnlag) {
    var inntektType = Optional.of(grunnlag.getInnhold().get("inntektType"))
        .orElseThrow(() -> new UgyldigInputException("inntektType mangler i objekt av type Inntekt")).asText();
    var belop = Optional.of(grunnlag.getInnhold().get("belop"))
        .orElseThrow(() -> new UgyldigInputException("belop mangler i objekt av type Inntekt")).asText();
    return new InntektPeriodeCore(grunnlag.getReferanse(), mapPeriode(grunnlag.getInnhold(), grunnlag.getType()), inntektType,
        new BigDecimal(belop));
  }

  private SivilstandPeriodeCore mapSivilstand(Grunnlag grunnlag) {
    var sivilstandKode = Optional.of(grunnlag.getInnhold().get("sivilstandKode"))
        .orElseThrow(() -> new UgyldigInputException("sivilstandKode mangler i objekt av type Sivilstand")).asText();
    return new SivilstandPeriodeCore(grunnlag.getReferanse(), mapPeriode(grunnlag.getInnhold(), grunnlag.getType()), sivilstandKode);
  }

  private BarnPeriodeCore mapBarn(Grunnlag grunnlag) {
    return new BarnPeriodeCore(grunnlag.getReferanse(), mapPeriode(grunnlag.getInnhold(), grunnlag.getType()));
  }

  private PeriodeCore mapPeriode(JsonNode grunnlagInnhold, String grunnlagType) {
    var datoFom = Optional.of(grunnlagInnhold.get("datoFom"))
        .orElseThrow(() -> new UgyldigInputException("datoFom mangler i objekt av type " + grunnlagType)).asText();
    var datoTil = Optional.of(grunnlagInnhold.get("datoTil")).orElse(null).asText();
    return new PeriodeCore(LocalDate.parse(datoFom), LocalDate.parse(datoTil));
  }

  // Plukker ut aktuelle sjabloner og flytter inn i inputen til core-modulen
  private List<SjablonPeriodeCore> mapSjablonVerdier(LocalDate beregnDatoFra, LocalDate beregnDatoTil, List<Sjablontall> sjablonSjablontallListe,
      HashMap<String, SjablonTallNavn> sjablontallMap) {
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
        .collect(toList());

    // Matcher mottatte grunnlag med grunnlag som er brukt i beregningen
    resultatGrunnlagListe.addAll(forskuddGrunnlag.getGrunnlagListe().stream()
        .filter(grunnlag -> grunnlagReferanseListe.contains(grunnlag.getReferanse()))
        .map(grunnlag -> new ResultatGrunnlag(grunnlag.getReferanse(), grunnlag.getType(), grunnlag.getInnhold()))
        .collect(toList()));

    // Danner grunnlag basert på liste over sjabloner som er brukt i beregningen
    resultatGrunnlagListe.addAll(resultatFraCore.getSjablonListe().stream()
        .map(sjablon -> {
              var map = new LinkedHashMap<String, Object>();
              map.put("datoFom", mapDato(sjablon.getPeriode().getDatoFom()));
              map.put("datoTil", mapDato(sjablon.getPeriode().getDatoTil()));
              map.put("sjablonNavn", sjablon.getNavn());
              map.put("sjablonVerdi", sjablon.getVerdi().intValue());
              return new ResultatGrunnlag(sjablon.getReferanse(), "Sjablon", mapper.valueToTree(map));
            }
        )
        .collect(toList()));

    return resultatGrunnlagListe;
  }

  // Unngå å legge ut datoer høyere enn 9999-12-31
  private String mapDato(LocalDate dato) {
    return dato.isAfter(LocalDate.parse("9999-12-31")) ? "9999-12-31" : dato.toString();
  }
}
