package no.nav.bidrag.beregn.forskudd.rest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.forskudd.core.dto.ForskuddPeriodeGrunnlagDto;
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BidragPeriodeResultat;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.ForskuddBeregningResultat;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.ResultatPeriode;

public class TestUtil {

  // Bygger opp liste av sjablonverdier
  public static List<Sjablontall> dummySjablonListe() {
    var sjablontallListe = new ArrayList<Sjablontall>();
    sjablontallListe.add(new Sjablontall("0005", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(1600)));
    sjablontallListe.add(new Sjablontall("0013", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(320)));
    sjablontallListe.add(new Sjablontall("0033", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(270200)));
    sjablontallListe.add(new Sjablontall("0034", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(419700)));
    sjablontallListe.add(new Sjablontall("0035", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(336500)));
    sjablontallListe.add(new Sjablontall("0036", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(61700)));
    return sjablontallListe;
  }

  // Bygger opp BeregnForskuddGrunnlagDto
  public static ForskuddPeriodeGrunnlagDto dummyForskuddGrunnlagDto() {
    var forskuddGrunnlagDto = new ForskuddPeriodeGrunnlagDto();
    forskuddGrunnlagDto.setBeregnDatoFra(LocalDate.MIN);
    forskuddGrunnlagDto.setBeregnDatoTil(LocalDate.MAX);
    return forskuddGrunnlagDto;
  }

  // Bygger opp BeregnForskuddGrunnlag
  public static BeregnForskuddGrunnlag dummyForskuddGrunnlag() {
    var forskuddGrunnlag = new BeregnForskuddGrunnlag();
    forskuddGrunnlag.setBeregnDatoFra(LocalDate.MIN);
    forskuddGrunnlag.setBeregnDatoTil(LocalDate.MAX);
    return forskuddGrunnlag;
  }

  // Bygger opp BeregnForskuddResultat
  public static BeregnForskuddResultat dummyForskuddResultat() {
    var beregnForskuddResultat = new BeregnForskuddResultat();
    var bidragPeriodeResultatListe = new ArrayList<BidragPeriodeResultat>();
    bidragPeriodeResultatListe.add(new BidragPeriodeResultat(new ResultatPeriode(LocalDate.parse("2017-01-01"), LocalDate.parse("2019-01-01")),
        new ForskuddBeregningResultat(BigDecimal.valueOf(100), "INNVILGET_100_PROSENT", "REGEL 1")));
    beregnForskuddResultat.setPeriodeResultatListe(bidragPeriodeResultatListe);
    return beregnForskuddResultat;
  }
}
