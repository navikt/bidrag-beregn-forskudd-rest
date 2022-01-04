package no.nav.bidrag.beregn.forskudd.rest.service;

import java.util.List;
import no.nav.bidrag.beregn.forskudd.rest.consumer.BidragGcpProxyConsumer;
import no.nav.bidrag.beregn.forskudd.rest.consumer.Sjablontall;
import no.nav.bidrag.commons.web.HttpResponse;
import org.springframework.core.ParameterizedTypeReference;

public class SjablonService {

  private static final ParameterizedTypeReference<List<Sjablontall>> SJABLON_SJABLONTALL_LISTE = new ParameterizedTypeReference<>() {};

  private final BidragGcpProxyConsumer bidragGcpProxyConsumer;


  public SjablonService(BidragGcpProxyConsumer bidragGcpProxyConsumer) {
    this.bidragGcpProxyConsumer = bidragGcpProxyConsumer;
  }

  public HttpResponse<List<Sjablontall>> hentSjablonSjablontall() {
    String sjablonSjablontallUrl = "/sjablon/sjablontall?all=true";
    return bidragGcpProxyConsumer.hentSjablonListe(sjablonSjablontallUrl, SJABLON_SJABLONTALL_LISTE);
  }
}
