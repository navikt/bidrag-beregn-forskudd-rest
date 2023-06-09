package no.nav.bidrag.beregn.forskudd.rest;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import no.nav.bidrag.commons.web.DefaultCorsFilter;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@EnableJwtTokenValidation(ignore = {"org.springframework", "org.springdoc"})
@SecurityScheme(
    bearerFormat = "JWT",
    name = "bearer-key",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP
)
@Import(DefaultCorsFilter.class)
public class BidragBeregnForskudd {

  public static final Logger SECURE_LOGGER = LoggerFactory.getLogger("secureLogger");

  public static void main(String[] args) {
    SpringApplication.run(BidragBeregnForskudd.class, args);
  }
}
