package no.nav.bidrag.beregn.forskudd.rest.service;

import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.forskudd.core.ForskuddCore;
import no.nav.bidrag.beregn.forskudd.core.dto.ForskuddPeriodeGrunnlagDto;
import no.nav.bidrag.beregn.forskudd.core.dto.PeriodeDto;
import no.nav.bidrag.beregn.forskudd.core.dto.SjablontallDto;
import no.nav.bidrag.beregn.forskudd.rest.consumer.SjablonConsumer;
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall;
import no.nav.bidrag.beregn.forskudd.rest.dto.http.BeregnForskuddResultat;
import org.springframework.stereotype.Service;

@Service
public class BeregnService {

  private final SjablonConsumer sjablonConsumer;
  private final ForskuddCore forskuddCore;

  public BeregnService(SjablonConsumer sjablonConsumer, ForskuddCore forskuddCore) {
    this.sjablonConsumer = sjablonConsumer;
    this.forskuddCore = forskuddCore;
  }

  public BeregnForskuddResultat beregn(ForskuddPeriodeGrunnlagDto coreDto) {
//    var sjablonResponse = sjablonConsumer.hentSjablontall();
//    coreDto.setSjablonVerdiListe(mapSjablonVerdier(sjablonResponse.getBody()));
    coreDto.setSjablontallListe(mapSjablonVerdier(dummySjablonListe()));
    var resultatFraCore = forskuddCore.beregnForskudd(coreDto);
    return new BeregnForskuddResultat(resultatFraCore);
  }

  private List<SjablontallDto> mapSjablonVerdier(List<Sjablontall> sjablontallListe) {
    return sjablontallListe.stream().filter(Sjablontall::erGyldigSjablon)
        .map(sTL -> new SjablontallDto(sTL.getTypeSjablon(), new PeriodeDto(sTL.getDatoFom(), sTL.getDatoTom()), sTL.getVerdi()))
        .collect(toList());
  }

  private static List<Sjablontall> dummySjablonListe() {
    var sjablontallListe = new ArrayList<Sjablontall>();
    sjablontallListe.add(new Sjablontall("0005", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(1600)));
    sjablontallListe.add(new Sjablontall("0013", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(320)));
    sjablontallListe.add(new Sjablontall("0033", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(270200)));
    sjablontallListe.add(new Sjablontall("0034", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(419700)));
    sjablontallListe.add(new Sjablontall("0035", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(336500)));
    sjablontallListe.add(new Sjablontall("0036", LocalDate.MIN, LocalDate.MAX, BigDecimal.valueOf(61700)));
    return sjablontallListe;
  }
}
