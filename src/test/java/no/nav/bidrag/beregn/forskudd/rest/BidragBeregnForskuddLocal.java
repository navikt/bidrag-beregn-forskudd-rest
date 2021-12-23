package no.nav.bidrag.beregn.forskudd.rest;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

@SpringBootApplication
@EnableMockOAuth2Server
@EnableJwtTokenValidation(ignore = {"org.springdoc", "org.springframework"})
@ComponentScan(excludeFilters = {@Filter(type = ASSIGNABLE_TYPE, value = BidragBeregnForskudd.class)})
public class BidragBeregnForskuddLocal {

  public static final String LOCAL = "local"; // Enable endpoint testing with Swagger locally, see application.yaml

  public static void main(String... args) {
    SpringApplication app = new SpringApplication(BidragBeregnForskuddLocal.class);
    app.setAdditionalProfiles(LOCAL);
    app.run(args);
  }
}
