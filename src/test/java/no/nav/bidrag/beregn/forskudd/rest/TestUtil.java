package no.nav.bidrag.beregn.forskudd.rest;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnetForskuddResultatCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnetForskuddResultat;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.Grunnlag;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.Periode;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.ResultatBeregning;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.ResultatPeriode;

public class TestUtil {

  private static final String INNTEKT_REFERANSE_1 = "INNTEKT_REFERANSE_1";
  private static final String SIVILSTAND_REFERANSE_ENSLIG = "SIVILSTAND_REFERANSE_ENSLIG";
  private static final String BARN_REFERANSE_1 = "BARN_REFERANSE_1";
  private static final String SOKNADBARN_REFERANSE = "SOKNADBARN_REFERANSE";
  private static final String BOSTATUS_REFERANSE_MED_FORELDRE_1 = "BOSTATUS_REFERANSE_MED_FORELDRE_1";

  public static BeregnForskuddGrunnlag byggDummyForskuddGrunnlag() {
    return byggDummyForskuddGrunnlag("");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenBeregnDatoFra() {
    return byggDummyForskuddGrunnlag("beregnDatoFra");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenBeregnDatoTil() {
    return byggDummyForskuddGrunnlag("beregnDatoTil");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenGrunnlagListe() {
    return byggDummyForskuddGrunnlag("grunnlagListe");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenReferanse() {
    return byggDummyForskuddGrunnlag("referanse");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenType() {
    return byggDummyForskuddGrunnlag("type");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenInnhold() {
    return byggDummyForskuddGrunnlag("innhold");
  }


  // Bygger opp BeregnForskuddGrunnlag
  private static BeregnForskuddGrunnlag byggDummyForskuddGrunnlag(String nullVerdi) {
    var mapper = new ObjectMapper();

    var beregnDatoFra = (nullVerdi.equals("beregnDatoFra") ? null : LocalDate.parse("2017-01-01"));
    var beregnDatoTil = (nullVerdi.equals("beregnDatoTil") ? null : LocalDate.parse("2020-01-01"));
    var referanse = (nullVerdi.equals("referanse") ? null : "Mottatt_BM_Inntekt_AG_20201201");
    var type = (nullVerdi.equals("type") ? null : "Inntekt");
    var innhold = (nullVerdi.equals("innhold") ? null : mapper.valueToTree(Map.of(
        "rolle", "BM",
        "datoFom", "2017-01-01",
        "datoTil", "2020-01-01",
        "inntektType", "INNTEKTTYPE",
        "belop", 290000)));

    List<Grunnlag> grunnlagListe;
    if (nullVerdi.equals("grunnlagListe")) {
      grunnlagListe = null;
    } else {
      grunnlagListe = singletonList(new Grunnlag(referanse, type, innhold));
    }

    return new BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, grunnlagListe);
  }

  // Bygger opp fullt BeregnForskuddGrunnlag
  public static BeregnForskuddGrunnlag byggForskuddGrunnlag() {
    var mapper = new ObjectMapper();

    var barnIHusstandInnhold = mapper.valueToTree(Map.of(
        "datoFom", "2017-01-01",
        "datoTil", "2020-01-01",
        "antall", "1"));
    var soknadsbarnInnhold = mapper.valueToTree(Map.of(
        "soknadsbarnId", "1",
        "fodselsdato", "2006-12-01"));
    var bostatusInnhold = mapper.valueToTree(Map.of(
        "datoFom", "2017-01-01",
        "datoTil", "2020-01-01",
        "rolle", "SOKNADSBARN",
        "bostatusKode", "MED_FORELDRE"));
    var inntektInnhold = mapper.valueToTree(Map.of(
        "datoFom", "2017-01-01",
        "datoTil", "2020-01-01",
        "rolle", "BIDRAGSMOTTAKER",
        "inntektType", "INNTEKTSOPPLYSNINGER_ARBEIDSGIVER",
        "belop", 290000));
    var sivilstandInnhold = mapper.valueToTree(Map.of(
        "rolle", "BM",
        "datoFom", "2017-01-01",
        "datoTil", "2020-01-01",
        "sivilstandKode", "GIFT"));

    var beregnDatoFra = LocalDate.parse("2017-01-01");
    var beregnDatoTil = LocalDate.parse("2020-01-01");

    List<Grunnlag> grunnlagListe = new ArrayList<>();
    grunnlagListe.add(new Grunnlag("Mottatt_BarnIHusstand", "BARN_I_HUSSTAND", barnIHusstandInnhold));
    grunnlagListe.add(new Grunnlag("Mottatt_BarnIHusstand", "SOKNADSBARN_INFO", soknadsbarnInnhold));
    grunnlagListe.add(new Grunnlag("Mottatt_Bostatus_20170101", "BOSTATUS", bostatusInnhold));
    grunnlagListe.add(new Grunnlag("Mottatt_Inntekt_AG_20170101", "INNTEKT", inntektInnhold));
    grunnlagListe.add(new Grunnlag("Mottatt_Sivilstand_20201201", "SIVILSTAND", sivilstandInnhold));

    return new BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, grunnlagListe);
  }

  // Bygger opp BeregnForskuddResultatCore
  public static BeregnetForskuddResultatCore dummyForskuddResultatCore() {
    var beregnetForskuddPeriodeListe = new ArrayList<ResultatPeriodeCore>();
    beregnetForskuddPeriodeListe.add(new ResultatPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregningCore(BigDecimal.valueOf(100), "INNVILGET_100_PROSENT", "REGEL 1"),
        List.of(INNTEKT_REFERANSE_1, SIVILSTAND_REFERANSE_ENSLIG, BARN_REFERANSE_1, SOKNADBARN_REFERANSE, BOSTATUS_REFERANSE_MED_FORELDRE_1)));
    return new BeregnetForskuddResultatCore(beregnetForskuddPeriodeListe, emptyList(), emptyList());
  }

  // Bygger opp BeregnForskuddResultatCore med avvik
  public static BeregnetForskuddResultatCore dummyForskuddResultatCoreMedAvvik() {
    var avvikListe = new ArrayList<AvvikCore>();
    avvikListe.add(new AvvikCore("beregnDatoFra kan ikke være null", "NULL_VERDI_I_DATO"));
    avvikListe.add(new AvvikCore(
        "periodeDatoTil må være etter periodeDatoFra i bidragMottakInntektPeriodeListe: periodeDatoFra=2018-04-01, periodeDatoTil=2018-03-01",
        "DATO_FRA_ETTER_DATO_TIL"));
    return new BeregnetForskuddResultatCore(emptyList(), emptyList(), avvikListe);
  }

  // Bygger opp BeregnForskuddResultat
  public static BeregnetForskuddResultat dummyForskuddResultat() {
    var beregnetForskuddPeriodeListe = new ArrayList<ResultatPeriode>();
    beregnetForskuddPeriodeListe.add(new ResultatPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(100), "INNVILGET_100_PROSENT", "REGEL 1"),
        List.of(INNTEKT_REFERANSE_1, SIVILSTAND_REFERANSE_ENSLIG, BARN_REFERANSE_1, SOKNADBARN_REFERANSE, BOSTATUS_REFERANSE_MED_FORELDRE_1)));
    return new BeregnetForskuddResultat(beregnetForskuddPeriodeListe, emptyList());
  }

  // Bygger opp liste av sjablonverdier
  public static List<Sjablontall> dummySjablonSjablontallListe() {
    var sjablonSjablontallListe = new ArrayList<Sjablontall>();

    sjablonSjablontallListe.add(new Sjablontall("0005", LocalDate.parse("2015-07-01"), LocalDate.parse("2016-06-30"), BigDecimal.valueOf(1490)));
    sjablonSjablontallListe.add(new Sjablontall("0005", LocalDate.parse("2016-07-01"), LocalDate.parse("2017-06-30"), BigDecimal.valueOf(1530)));
    sjablonSjablontallListe.add(new Sjablontall("0005", LocalDate.parse("2017-07-01"), LocalDate.parse("2018-06-30"), BigDecimal.valueOf(1570)));
    sjablonSjablontallListe.add(new Sjablontall("0005", LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30"), BigDecimal.valueOf(1600)));
    sjablonSjablontallListe.add(new Sjablontall("0005", LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30"), BigDecimal.valueOf(1640)));
    sjablonSjablontallListe.add(new Sjablontall("0005", LocalDate.parse("2020-07-01"), LocalDate.parse("9999-12-31"), BigDecimal.valueOf(1670)));

    sjablonSjablontallListe.add(new Sjablontall("0013", LocalDate.parse("2003-01-01"), LocalDate.parse("9999-12-31"), BigDecimal.valueOf(320)));

    sjablonSjablontallListe.add(new Sjablontall("0033", LocalDate.parse("2015-07-01"), LocalDate.parse("2016-06-30"), BigDecimal.valueOf(241600)));
    sjablonSjablontallListe.add(new Sjablontall("0033", LocalDate.parse("2016-07-01"), LocalDate.parse("2017-06-30"), BigDecimal.valueOf(264200)));
    sjablonSjablontallListe.add(new Sjablontall("0033", LocalDate.parse("2017-07-01"), LocalDate.parse("2018-06-30"), BigDecimal.valueOf(271000)));
    sjablonSjablontallListe.add(new Sjablontall("0033", LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30"), BigDecimal.valueOf(270200)));
    sjablonSjablontallListe.add(new Sjablontall("0033", LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30"), BigDecimal.valueOf(277600)));
    sjablonSjablontallListe.add(new Sjablontall("0033", LocalDate.parse("2020-07-01"), LocalDate.parse("9999-12-31"), BigDecimal.valueOf(297500)));

    sjablonSjablontallListe.add(new Sjablontall("0034", LocalDate.parse("2015-07-01"), LocalDate.parse("2016-06-30"), BigDecimal.valueOf(370200)));
    sjablonSjablontallListe.add(new Sjablontall("0034", LocalDate.parse("2016-07-01"), LocalDate.parse("2017-06-30"), BigDecimal.valueOf(399100)));
    sjablonSjablontallListe.add(new Sjablontall("0034", LocalDate.parse("2017-07-01"), LocalDate.parse("2018-06-30"), BigDecimal.valueOf(408200)));
    sjablonSjablontallListe.add(new Sjablontall("0034", LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30"), BigDecimal.valueOf(419700)));
    sjablonSjablontallListe.add(new Sjablontall("0034", LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30"), BigDecimal.valueOf(430000)));
    sjablonSjablontallListe.add(new Sjablontall("0034", LocalDate.parse("2020-07-01"), LocalDate.parse("9999-12-31"), BigDecimal.valueOf(468500)));

    sjablonSjablontallListe.add(new Sjablontall("0035", LocalDate.parse("2015-07-01"), LocalDate.parse("2016-06-30"), BigDecimal.valueOf(314800)));
    sjablonSjablontallListe.add(new Sjablontall("0035", LocalDate.parse("2016-07-01"), LocalDate.parse("2017-06-30"), BigDecimal.valueOf(328700)));
    sjablonSjablontallListe.add(new Sjablontall("0035", LocalDate.parse("2017-07-01"), LocalDate.parse("2018-06-30"), BigDecimal.valueOf(335900)));
    sjablonSjablontallListe.add(new Sjablontall("0035", LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30"), BigDecimal.valueOf(336500)));
    sjablonSjablontallListe.add(new Sjablontall("0035", LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30"), BigDecimal.valueOf(344900)));
    sjablonSjablontallListe.add(new Sjablontall("0035", LocalDate.parse("2020-07-01"), LocalDate.parse("9999-12-31"), BigDecimal.valueOf(360800)));

    sjablonSjablontallListe.add(new Sjablontall("0036", LocalDate.parse("2015-07-01"), LocalDate.parse("2016-06-30"), BigDecimal.valueOf(58400)));
    sjablonSjablontallListe.add(new Sjablontall("0036", LocalDate.parse("2016-07-01"), LocalDate.parse("2017-06-30"), BigDecimal.valueOf(60200)));
    sjablonSjablontallListe.add(new Sjablontall("0036", LocalDate.parse("2017-07-01"), LocalDate.parse("2018-06-30"), BigDecimal.valueOf(61100)));
    sjablonSjablontallListe.add(new Sjablontall("0036", LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30"), BigDecimal.valueOf(61700)));
    sjablonSjablontallListe.add(new Sjablontall("0036", LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30"), BigDecimal.valueOf(62700)));
    sjablonSjablontallListe.add(new Sjablontall("0036", LocalDate.parse("2020-07-01"), LocalDate.parse("9999-12-31"), BigDecimal.valueOf(69100)));

    // Ikke i bruk for forskudd
    sjablonSjablontallListe.add(new Sjablontall("0028", LocalDate.parse("2015-07-01"), LocalDate.parse("2016-06-30"), BigDecimal.valueOf(74250)));
    sjablonSjablontallListe.add(new Sjablontall("0028", LocalDate.parse("2016-07-01"), LocalDate.parse("2017-06-30"), BigDecimal.valueOf(76250)));
    sjablonSjablontallListe.add(new Sjablontall("0028", LocalDate.parse("2017-07-01"), LocalDate.parse("2018-06-30"), BigDecimal.valueOf(78300)));
    sjablonSjablontallListe.add(new Sjablontall("0028", LocalDate.parse("2018-07-01"), LocalDate.parse("2019-06-30"), BigDecimal.valueOf(54750)));
    sjablonSjablontallListe.add(new Sjablontall("0028", LocalDate.parse("2019-07-01"), LocalDate.parse("2020-06-30"), BigDecimal.valueOf(56550)));
    sjablonSjablontallListe.add(new Sjablontall("0028", LocalDate.parse("2020-07-01"), LocalDate.parse("9999-12-31"), BigDecimal.valueOf(51300)));

    return sjablonSjablontallListe;
  }
}
