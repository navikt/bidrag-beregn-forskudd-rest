package no.nav.bidrag.beregn.forskudd.rest.service;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.forskudd.core.dto.BarnIHusstandenPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BostatusPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SivilstandPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SoknadBarnCore;
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.Grunnlag;
import no.nav.bidrag.beregn.forskudd.rest.exception.UgyldigInputException;

public class CoreMapper {

  private static final String SOKNADSBARN_TYPE = "SOKNADSBARN_INFO";
  private static final String BOSTATUS_TYPE = "BOSTATUS";
  private static final String INNTEKT_TYPE = "INNTEKT";
  private static final String SIVILSTAND_TYPE = "SIVILSTAND";
  private static final String BARN_I_HUSSTAND_TYPE = "BARN_I_HUSSTAND";

  public static BeregnForskuddGrunnlagCore mapGrunnlagTilCore(BeregnForskuddGrunnlag beregnForskuddGrunnlag, List<Sjablontall> sjablontallListe) {

    // Lager en map for sjablontall (id og navn)
    var sjablontallMap = new HashMap<String, SjablonTallNavn>();
    for (SjablonTallNavn sjablonTallNavn : SjablonTallNavn.values()) {
      sjablontallMap.put(sjablonTallNavn.getId(), sjablonTallNavn);
    }

    SoknadBarnCore soknadbarnCore = null;
    var bostatusPeriodeCoreListe = new ArrayList<BostatusPeriodeCore>();
    var inntektPeriodeCoreListe = new ArrayList<InntektPeriodeCore>();
    var sivilstandPeriodeCoreListe = new ArrayList<SivilstandPeriodeCore>();
    var barnIHusstandenPeriodeCoreListe = new ArrayList<BarnIHusstandenPeriodeCore>();

    for (Grunnlag grunnlag : beregnForskuddGrunnlag.getGrunnlagListe()) {
      switch (grunnlag.getType()) {
        case SOKNADSBARN_TYPE -> soknadbarnCore = mapSoknadsbarn(grunnlag);
        case BOSTATUS_TYPE -> bostatusPeriodeCoreListe.add(mapBostatus(grunnlag));
        case INNTEKT_TYPE -> inntektPeriodeCoreListe.add(mapInntekt(grunnlag));
        case SIVILSTAND_TYPE -> sivilstandPeriodeCoreListe.add(mapSivilstand(grunnlag));
        case BARN_I_HUSSTAND_TYPE -> barnIHusstandenPeriodeCoreListe.add(mapBarnIHusstanden(grunnlag));
      }
    }

//    var soknadBarnCore = new SoknadBarnCore(soknadBarnReferanse, soknadBarnFodselsdato);

    var sjablonPeriodeCoreListe = mapSjablonVerdier(beregnForskuddGrunnlag.getBeregnDatoFra(), beregnForskuddGrunnlag.getBeregnDatoTil(),
        sjablontallListe, sjablontallMap);

    return new BeregnForskuddGrunnlagCore(beregnForskuddGrunnlag.getBeregnDatoFra(), beregnForskuddGrunnlag.getBeregnDatoTil(), soknadbarnCore,
        bostatusPeriodeCoreListe, inntektPeriodeCoreListe, sivilstandPeriodeCoreListe, barnIHusstandenPeriodeCoreListe, sjablonPeriodeCoreListe);
  }

  private static SoknadBarnCore mapSoknadsbarn(Grunnlag grunnlag) {
    var fodselsdato = Optional.of(grunnlag.getInnhold().get("fodselsdato"))
        .orElseThrow(() -> new UgyldigInputException("fødselsdato mangler i objekt av type SOKNADSBARN_INFO")).asText();
    return new SoknadBarnCore(grunnlag.getReferanse(), LocalDate.parse(fodselsdato));
  }

  private static BostatusPeriodeCore mapBostatus(Grunnlag grunnlag) {
    var bostatusKode = Optional.of(grunnlag.getInnhold().get("bostatusKode"))
        .orElseThrow(() -> new UgyldigInputException("bostatusKode mangler i objekt av type Bostatus")).asText();
    return new BostatusPeriodeCore(grunnlag.getReferanse(), mapPeriode(grunnlag.getInnhold(), grunnlag.getType()), bostatusKode);
  }

  private static InntektPeriodeCore mapInntekt(Grunnlag grunnlag) {
    var inntektType = Optional.of(grunnlag.getInnhold().get("inntektType"))
        .orElseThrow(() -> new UgyldigInputException("inntektType mangler i objekt av type Inntekt")).asText();
    var belop = Optional.of(grunnlag.getInnhold().get("belop"))
        .orElseThrow(() -> new UgyldigInputException("belop mangler i objekt av type Inntekt")).asText();
    return new InntektPeriodeCore(grunnlag.getReferanse(), mapPeriode(grunnlag.getInnhold(), grunnlag.getType()), inntektType, new BigDecimal(belop));
  }

  private static SivilstandPeriodeCore mapSivilstand(Grunnlag grunnlag) {
    var sivilstandKode = Optional.of(grunnlag.getInnhold().get("sivilstandKode"))
        .orElseThrow(() -> new UgyldigInputException("sivilstandKode mangler i objekt av type Sivilstand")).asText();
    return new SivilstandPeriodeCore(grunnlag.getReferanse(), mapPeriode(grunnlag.getInnhold(), grunnlag.getType()), sivilstandKode);
  }

  private static BarnIHusstandenPeriodeCore mapBarnIHusstanden(Grunnlag grunnlag) {
    var antall = Optional.of(grunnlag.getInnhold().get("antall"))
        .orElseThrow(() -> new UgyldigInputException("antall mangler i objekt av type BarnIHusstanden")).asText();
    return new BarnIHusstandenPeriodeCore(grunnlag.getReferanse(), mapPeriode(grunnlag.getInnhold(), grunnlag.getType()), Double.parseDouble(antall));
  }

  private static PeriodeCore mapPeriode(JsonNode grunnlagInnhold, String grunnlagType) {
    var datoFom = Optional.of(grunnlagInnhold.get("datoFom"))
        .orElseThrow(() -> new UgyldigInputException("datoFom mangler i objekt av type " + grunnlagType)).asText();
    var datoTil = Optional.of(grunnlagInnhold.get("datoTil")).orElse(null).asText();
    return new PeriodeCore(LocalDate.parse(datoFom), LocalDate.parse(datoTil));
  }

  // Plukker ut aktuelle sjabloner og flytter inn i inputen til core-modulen
  private static List<SjablonPeriodeCore> mapSjablonVerdier(LocalDate beregnDatoFra, LocalDate beregnDatoTil, List<Sjablontall> sjablonSjablontallListe,
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
  private static boolean filtrerSjablonTall(SjablonTallNavn sjablonTallNavn) {
    return sjablonTallNavn.getForskudd();
  }
}
