package no.nav.bidrag.beregn.forskudd.rest;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.dto.AvvikCore;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddResultatCore;
import no.nav.bidrag.beregn.forskudd.core.dto.InntektCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatBeregningCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ResultatPeriodeCore;
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.Inntekt;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.Periode;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.ResultatBeregning;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.ResultatGrunnlag;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.ResultatPeriode;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.SivilstandPeriode;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.Sjablon;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.SoknadBarn;

public class TestUtil {

  public static BeregnForskuddGrunnlag byggForskuddGrunnlag() {
    return byggForskuddGrunnlag("");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenBostatusDatoFra() {
    return byggForskuddGrunnlag("bostatusDatoFra");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenBostatusDatoTil() {
    return byggForskuddGrunnlag("bostatusDatoTil");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenBostatusKode() {
    return byggForskuddGrunnlag("bostatusKode");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenSoknadBarnFodselsdato() {
    return byggForskuddGrunnlag("soknadBarnFodselsdato");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenInntektDatoFra() {
    return byggForskuddGrunnlag("inntektDatoFra");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenInntektDatoTil() {
    return byggForskuddGrunnlag("inntektDatoTil");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenInntektType() {
    return byggForskuddGrunnlag("inntektType");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenInntektBelop() {
    return byggForskuddGrunnlag("inntektBelop");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenSivilstandDatoFra() {
    return byggForskuddGrunnlag("sivilstandDatoFra");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenSivilstandDatoTil() {
    return byggForskuddGrunnlag("sivilstandDatoTil");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenSivilstandKode() {
    return byggForskuddGrunnlag("sivilstandKode");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenBarnDatoFra() {
    return byggForskuddGrunnlag("barnDatoFra");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenBarnDatoTil() {
    return byggForskuddGrunnlag("barnDatoTil");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenBeregnDatoFra() {
    return byggForskuddGrunnlag("beregnDatoFra");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenBeregnDatoTil() {
    return byggForskuddGrunnlag("beregnDatoTil");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenSoknadBarnBostatusPeriodeListe() {
    return byggForskuddGrunnlag("soknadBarnBostatusPeriodeListe");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenBidragMottakerInntektPeriodeListe() {
    return byggForskuddGrunnlag("bidragMottakerInntektPeriodeListe");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenBidragMottakerSivilstandPeriodeListe() {
    return byggForskuddGrunnlag("bidragMottakerSivilstandPeriodeListe");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenSoknadBarn() {
    return byggForskuddGrunnlag("soknadBarn");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenBostatusDatoFraTil() {
    return byggForskuddGrunnlag("bostatusDatoFraTil");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenInntektDatoFraTil() {
    return byggForskuddGrunnlag("inntektDatoFraTil");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenSivilstandDatoFraTil() {
    return byggForskuddGrunnlag("sivilstandDatoFraTil");
  }


  // Bygger opp BeregnForskuddGrunnlag
  private static BeregnForskuddGrunnlag byggForskuddGrunnlag(String nullVerdi) {
    var bostatusDatoFra = (nullVerdi.equals("bostatusDatoFra") ? null : LocalDate.parse("2017-01-01"));
    var bostatusDatoTil = (nullVerdi.equals("bostatusDatoTil") ? null : LocalDate.parse("2020-01-01"));
    var bostatusKode = (nullVerdi.equals("bostatusKode") ? null : "MED_FORELDRE");
    var soknadBarnFodselsDato = (nullVerdi.equals("soknadBarnFodselsdato") ? null : LocalDate.parse("2006-05-12"));
    var inntektDatoFra = (nullVerdi.equals("inntektDatoFra") ? null : LocalDate.parse("2017-01-01"));
    var inntektDatoTil = (nullVerdi.equals("inntektDatoTil") ? null : LocalDate.parse("2020-01-01"));
    var inntektType = (nullVerdi.equals("inntektType") ? null : "OVERGANGSSTONAD");
    var inntektBelop = (nullVerdi.equals("inntektBelop") ? null : BigDecimal.valueOf(100000));
    var sivilstandDatoFra = (nullVerdi.equals("sivilstandDatoFra") ? null : LocalDate.parse("2017-01-01"));
    var sivilstandDatoTil = (nullVerdi.equals("sivilstandDatoTil") ? null : LocalDate.parse("2020-01-01"));
    var sivilstandKode = (nullVerdi.equals("sivilstandKode") ? null : "GIFT");
    var barnDatoFra = (nullVerdi.equals("barnDatoFra") ? null : LocalDate.parse("2017-01-01"));
    var barnDatoTil = (nullVerdi.equals("barnDatoTil") ? null : LocalDate.parse("2020-01-01"));
    var beregnDatoFra = (nullVerdi.equals("beregnDatoFra") ? null : LocalDate.parse("2017-01-01"));
    var beregnDatoTil = (nullVerdi.equals("beregnDatoTil") ? null : LocalDate.parse("2020-01-01"));

    List<BostatusPeriode> soknadBarnBostatusPeriodeListe;
    if (nullVerdi.equals("soknadBarnBostatusPeriodeListe")) {
      soknadBarnBostatusPeriodeListe = null;
    } else {
      BostatusPeriode bostatusPeriode;
      if (nullVerdi.equals("bostatusDatoFraTil")) {
        bostatusPeriode = new BostatusPeriode(null, bostatusKode);
      } else {
        bostatusPeriode = new BostatusPeriode(new Periode(bostatusDatoFra, bostatusDatoTil), bostatusKode);
      }
      soknadBarnBostatusPeriodeListe = new ArrayList<>();
      soknadBarnBostatusPeriodeListe.add(bostatusPeriode);
    }
    SoknadBarn soknadBarn;
    if (nullVerdi.equals("soknadBarn")) {
      soknadBarn = null;
    } else {
      soknadBarn = new SoknadBarn(soknadBarnFodselsDato, soknadBarnBostatusPeriodeListe);
    }

    List<InntektPeriode> bidragMottakerInntektPeriodeListe;
    if (nullVerdi.equals("bidragMottakerInntektPeriodeListe")) {
      bidragMottakerInntektPeriodeListe = null;
    } else {
      InntektPeriode bidragMottakerInntektPeriode;
      if (nullVerdi.equals("inntektDatoFraTil")) {
        bidragMottakerInntektPeriode = new InntektPeriode(null, inntektType, inntektBelop);
      } else {
        bidragMottakerInntektPeriode = new InntektPeriode(new Periode(inntektDatoFra, inntektDatoTil), inntektType, inntektBelop);
      }
      bidragMottakerInntektPeriodeListe = new ArrayList<>();
      bidragMottakerInntektPeriodeListe.add(bidragMottakerInntektPeriode);
    }

    List<SivilstandPeriode> bidragMottakerSivilstandPeriodeListe;
    if (nullVerdi.equals("bidragMottakerSivilstandPeriodeListe")) {
      bidragMottakerSivilstandPeriodeListe = null;
    } else {
      SivilstandPeriode bidragMottakerSivilstandPeriode;
      if (nullVerdi.equals("sivilstandDatoFraTil")) {
        bidragMottakerSivilstandPeriode = new SivilstandPeriode(null, sivilstandKode);
      } else {
        bidragMottakerSivilstandPeriode = new SivilstandPeriode(new Periode(sivilstandDatoFra, sivilstandDatoTil), sivilstandKode);
      }
      bidragMottakerSivilstandPeriodeListe = new ArrayList<>();
      bidragMottakerSivilstandPeriodeListe.add(bidragMottakerSivilstandPeriode);
    }

    var bidragMottakerBarnPeriodeListe = new ArrayList<Periode>();
    bidragMottakerBarnPeriodeListe.add(new Periode(barnDatoFra, barnDatoTil));

    return new BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadBarn,
        bidragMottakerInntektPeriodeListe, bidragMottakerSivilstandPeriodeListe, bidragMottakerBarnPeriodeListe);
  }

  // Bygger opp BeregnForskuddResultatCore
  public static BeregnForskuddResultatCore dummyForskuddResultatCore() {
    var bidragPeriodeResultatListe = new ArrayList<ResultatPeriodeCore>();
    bidragPeriodeResultatListe.add(new ResultatPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregningCore(BigDecimal.valueOf(100), "INNVILGET_100_PROSENT", "REGEL 1"),
        new ResultatGrunnlagCore(singletonList(new InntektCore("LØNNSINNTEKT", BigDecimal.valueOf(500000))), "ENSLIG", 2, 10,
            "MED_FORELDRE", emptyList())));
    return new BeregnForskuddResultatCore(bidragPeriodeResultatListe, emptyList());
  }

  // Bygger opp BeregnForskuddResultatCore med avvik
  public static BeregnForskuddResultatCore dummyForskuddResultatCoreMedAvvik() {
    var avvikListe = new ArrayList<AvvikCore>();
    avvikListe.add(new AvvikCore("beregnDatoFra kan ikke være null", "NULL_VERDI_I_DATO"));
    avvikListe.add(new AvvikCore(
        "periodeDatoTil må være etter periodeDatoFra i bidragMottakInntektPeriodeListe: periodeDatoFra=2018-04-01, periodeDatoTil=2018-03-01",
        "DATO_FRA_ETTER_DATO_TIL"));
    return new BeregnForskuddResultatCore(emptyList(), avvikListe);
  }

  // Bygger opp BeregnForskuddResultat
  public static BeregnForskuddResultat dummyForskuddResultat() {
    var bidragPeriodeResultatListe = new ArrayList<ResultatPeriode>();
    bidragPeriodeResultatListe.add(new ResultatPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(100), "INNVILGET_100_PROSENT", "REGEL 1"),
        new ResultatGrunnlag(singletonList(new Inntekt("LØNNSINNTEKT", BigDecimal.valueOf(500000))), "ENSLIG", 2, 10,
            "MED_FORELDRE", byggSjablonNavnVerdiListe())));
    return new BeregnForskuddResultat(bidragPeriodeResultatListe);
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

  public static List<Sjablon> byggSjablonNavnVerdiListe() {
    var sjablonListe = new ArrayList<Sjablon>();

    // Sjablontall
    sjablonListe.add(new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), BigDecimal.valueOf(1600)));
    sjablonListe.add(new Sjablon(SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR.getNavn(), BigDecimal.valueOf(320)));
    sjablonListe.add(new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP.getNavn(), BigDecimal.valueOf(270200)));
    sjablonListe.add(new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP.getNavn(), BigDecimal.valueOf(419700)));
    sjablonListe.add(new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP.getNavn(), BigDecimal.valueOf(336500)));
    sjablonListe.add(new Sjablon(SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP.getNavn(), BigDecimal.valueOf(61700)));

    return sjablonListe;
  }

}
