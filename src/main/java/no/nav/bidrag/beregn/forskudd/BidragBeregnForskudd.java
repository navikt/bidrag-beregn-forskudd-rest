package no.nav.bidrag.beregn.forskudd;

import no.nav.bidrag.beregn.forskudd.periode.ForskuddPeriode;
import no.nav.bidrag.beregn.forskudd.periode.ForskuddPeriodeImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BidragBeregnForskudd {

  @Bean
  public ForskuddPeriode forskuddPeriode() {
    return new ForskuddPeriodeImpl();
  }

  public static void main(String[] args) {
    SpringApplication.run(BidragBeregnForskudd.class, args);
  }

}
