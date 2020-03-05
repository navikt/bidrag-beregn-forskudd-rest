package no.nav.bidrag.beregn.forskudd.rest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.forskudd.rest.dto.BeregnForskuddGrunnlagDto;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BidragPeriodeResultat;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.Sjablontall;

public class TestUtil {

  // Bygger opp liste av sjablonverdier
  public static List<Sjablontall> dummySjablonListe() {
    var sjablontallListe = new ArrayList<Sjablontall>();
    sjablontallListe.add(new Sjablontall("0005", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(1600)));
    sjablontallListe.add(new Sjablontall("0013", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(320)));
    sjablontallListe.add(new Sjablontall("9999", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(9999)));
    return sjablontallListe;
  }

  // Bygger opp BeregnForskuddGrunnlagDto
  public static BeregnForskuddGrunnlagDto dummyForskuddGrunnlagDto() {
    var forskuddGrunnlagDto = new BeregnForskuddGrunnlagDto();
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
    var forskuddResultat = new BeregnForskuddResultat();
    var bidragPeriodeResultatListe = new ArrayList<BidragPeriodeResultat>();
    forskuddResultat.setBidragPeriodeResultatListe(bidragPeriodeResultatListe);
    return forskuddResultat;
  }
}
