package no.nav.bidrag.beregn.forskudd.rest;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = BidragBeregnForskudd.class, webEnvironment = RANDOM_PORT)
public class BidragBeregnForskuddRestApplicationTest {

  @Test
  void contextLoads() {
  }
}
