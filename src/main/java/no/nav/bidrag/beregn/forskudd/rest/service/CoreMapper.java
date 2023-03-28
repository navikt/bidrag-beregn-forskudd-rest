package no.nav.bidrag.beregn.forskudd.rest.service;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    // Mapper grunnlagstyper til input for core
    for (Grunnlag grunnlag : beregnForskuddGrunnlag.getGrunnlagListe()) {
      switch (grunnlag.getType()) {
        case SOKNADSBARN_TYPE -> soknadbarnCore = mapSoknadsbarn(grunnlag);
        case BOSTATUS_TYPE -> bostatusPeriodeCoreListe.add(mapBostatus(grunnlag));
        case INNTEKT_TYPE -> inntektPeriodeCoreListe.add(mapInntekt(grunnlag));
        case SIVILSTAND_TYPE -> sivilstandPeriodeCoreListe.add(mapSivilstand(grunnlag));
        case BARN_I_HUSSTAND_TYPE -> barnIHusstandenPeriodeCoreListe.add(mapBarnIHusstanden(grunnlag));
      }
    }

    // Validerer at alle nødvendige grunnlag er med
    validerGrunnlag(soknadbarnCore != null, !bostatusPeriodeCoreListe.isEmpty(), !inntektPeriodeCoreListe.isEmpty(),
        !sivilstandPeriodeCoreListe.isEmpty());

    var sjablonPeriodeCoreListe = mapSjablonVerdier(beregnForskuddGrunnlag.getBeregnDatoFra(), beregnForskuddGrunnlag.getBeregnDatoTil(),
        sjablontallListe, sjablontallMap);

    return new BeregnForskuddGrunnlagCore(beregnForskuddGrunnlag.getBeregnDatoFra(), beregnForskuddGrunnlag.getBeregnDatoTil(), soknadbarnCore,
        bostatusPeriodeCoreListe, inntektPeriodeCoreListe, sivilstandPeriodeCoreListe, barnIHusstandenPeriodeCoreListe, sjablonPeriodeCoreListe);
  }

  private static void validerGrunnlag(boolean soknadbarnGrunnlag, boolean bostatusGrunnlag, boolean inntektGrunnlag, boolean sivilstandGrunnlag) {
    if (!soknadbarnGrunnlag) {
      throw new UgyldigInputException("Grunnlagstype SOKNADSBARN_INFO mangler i input");
    } else if (!bostatusGrunnlag) {
      throw new UgyldigInputException("Grunnlagstype BOSTATUS mangler i input");
    } else if (!inntektGrunnlag) {
      throw new UgyldigInputException("Grunnlagstype INNTEKT mangler i input");
    } else if (!sivilstandGrunnlag) {
      throw new UgyldigInputException("Grunnlagstype SIVILSTAND mangler i input");
    }
  }

  private static SoknadBarnCore mapSoknadsbarn(Grunnlag grunnlag) {
    var fodselsdato = grunnlag.getInnhold().get("fodselsdato") != null ? grunnlag.getInnhold().get("fodselsdato").asText() : null;
    if (fodselsdato == null) {
      throw new UgyldigInputException("fødselsdato mangler i objekt av type " + SOKNADSBARN_TYPE);
    }
    return new SoknadBarnCore(grunnlag.getReferanse(), formaterDato(fodselsdato, "fodselsdato", SOKNADSBARN_TYPE));
  }

  private static BostatusPeriodeCore mapBostatus(Grunnlag grunnlag) {
    var bostatusKode = grunnlag.getInnhold().get("bostatusKode") != null ? grunnlag.getInnhold().get("bostatusKode").asText() : null;
    if (bostatusKode == null) {
      throw new UgyldigInputException("bostatusKode mangler i objekt av type " + BOSTATUS_TYPE);
    }
    return new BostatusPeriodeCore(grunnlag.getReferanse(), mapPeriode(grunnlag.getInnhold(), grunnlag.getType()), bostatusKode);
  }

  private static InntektPeriodeCore mapInntekt(Grunnlag grunnlag) {
    var inntektType = grunnlag.getInnhold().get("inntektType") != null ? grunnlag.getInnhold().get("inntektType").asText() : null;
    if (inntektType == null) {
      throw new UgyldigInputException("inntektType mangler i objekt av type " + INNTEKT_TYPE);
    }
    var belop = grunnlag.getInnhold().get("belop") != null ? grunnlag.getInnhold().get("belop").asText() : null;
    if (belop == null) {
      throw new UgyldigInputException("belop mangler i objekt av type " + INNTEKT_TYPE);
    }
    return new InntektPeriodeCore(grunnlag.getReferanse(), mapPeriode(grunnlag.getInnhold(), grunnlag.getType()), inntektType,
        formaterBelop(belop, INNTEKT_TYPE));
  }

  private static SivilstandPeriodeCore mapSivilstand(Grunnlag grunnlag) {
    var sivilstandKode = grunnlag.getInnhold().get("sivilstandKode") != null ? grunnlag.getInnhold().get("sivilstandKode").asText() : null;
    if (sivilstandKode == null) {
      throw new UgyldigInputException("sivilstandKode mangler i objekt av type " + SIVILSTAND_TYPE);
    }
    return new SivilstandPeriodeCore(grunnlag.getReferanse(), mapPeriode(grunnlag.getInnhold(), grunnlag.getType()), sivilstandKode);
  }

  private static BarnIHusstandenPeriodeCore mapBarnIHusstanden(Grunnlag grunnlag) {
    var antall = grunnlag.getInnhold().get("antall") != null ? grunnlag.getInnhold().get("antall").asText() : null;
    if (antall == null) {
      throw new UgyldigInputException("antall mangler i objekt av type " + BARN_I_HUSSTAND_TYPE);
    }
    return new BarnIHusstandenPeriodeCore(grunnlag.getReferanse(), mapPeriode(grunnlag.getInnhold(), grunnlag.getType()),
        formaterAntall(antall, BARN_I_HUSSTAND_TYPE));
  }

  private static PeriodeCore mapPeriode(JsonNode grunnlagInnhold, String grunnlagType) {
    var datoFom = grunnlagInnhold.get("datoFom") != null ? grunnlagInnhold.get("datoFom").asText() : null;
    if (datoFom == null) {
      throw new UgyldigInputException("datoFom mangler i objekt av type " + grunnlagType);
    }
    var datoTil = grunnlagInnhold.get("datoTil") != null ? grunnlagInnhold.get("datoTil").asText() : null;
    return new PeriodeCore(formaterDato(datoFom, "datoFom", grunnlagType), formaterDato(datoTil, "datoTil", grunnlagType));
  }

  // Plukker ut aktuelle sjabloner og flytter inn i inputen til core-modulen
  private static List<SjablonPeriodeCore> mapSjablonVerdier(LocalDate beregnDatoFra, LocalDate beregnDatoTil,
      List<Sjablontall> sjablonSjablontallListe,
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
        .toList();
  }

  // Sjekker om en type SjablonTall er i bruk for forskudd
  private static boolean filtrerSjablonTall(SjablonTallNavn sjablonTallNavn) {
    return sjablonTallNavn.getForskudd();
  }

  private static LocalDate formaterDato(String dato, String datoType, String grunnlagType) {
    if ((dato == null) || (dato.equals("null"))) {
      return null;
    }
    try {
      return LocalDate.parse(dato);
    } catch (DateTimeParseException e) {
      throw new UgyldigInputException("Dato " + dato + " av type " + datoType + " i objekt av type " + grunnlagType + " har feil format");
    }
  }

  private static BigDecimal formaterBelop(String belop, String grunnlagType) {
    if ((belop == null) || (belop.equals("null"))) {
      return null;
    }
    try {
      return new BigDecimal(belop);
    } catch (NumberFormatException e) {
      throw new UgyldigInputException("belop " + belop + " i objekt av type " + grunnlagType + " har feil format");
    }
  }

  private static Double formaterAntall(String antall, String grunnlagType) {
    if ((antall == null) || (antall.equals("null"))) {
      return null;
    }
    try {
      return Double.parseDouble(antall);
    } catch (NumberFormatException e) {
      throw new UgyldigInputException("antall " + antall + " i objekt av type " + grunnlagType + " har feil format");
    }
  }
}
