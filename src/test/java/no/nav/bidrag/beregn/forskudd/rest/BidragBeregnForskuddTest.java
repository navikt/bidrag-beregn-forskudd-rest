package no.nav.bidrag.beregn.forskudd.rest;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@ComponentScan(excludeFilters = {@Filter(type = ASSIGNABLE_TYPE, value = {BidragBeregnForskudd.class, BidragBeregnForskuddLocal.class})})
public class BidragBeregnForskuddTest {

  public static void main(String... args) {
    SpringApplication app = new SpringApplication(BidragBeregnForskuddTest.class);
    app.run(args);
  }
}
