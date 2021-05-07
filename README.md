# bidrag-beregn-forskudd-rest

![](https://github.com/navikt/bidrag-beregn-forskudd-rest/workflows/continuous%20integration/badge.svg)
[![test build on pull request](https://github.com/navikt/bidrag-beregn-forskudd-rest/actions/workflows/pr.yaml/badge.svg)](https://github.com/navikt/bidrag-beregn-forskudd-rest/actions/workflows/pr.yaml)
[![release bidrag-beregn-forskudd-rest](https://github.com/navikt/bidrag-beregn-forskudd-rest/actions/workflows/release.yaml/badge.svg)](https://github.com/navikt/bidrag-beregn-forskudd-rest/actions/workflows/release.yaml)

Mikrotjeneste / Rest-API for beregning av bidragsforskudd, som er satt opp til å kjøre på NAIS.

### Tilgjengelige tjenester (endepunkter) 
Request-URL: https://bidrag-beregn-forskudd-rest.nais.preprod.local/bidrag-beregn-forskudd-rest/beregn/forskudd<br/>
Swagger-UI: https://bidrag-beregn-forskudd-rest.nais.preprod.local/bidrag-beregn-forskudd-rest/swagger-ui.html#/beregn-forskudd-controller

### Input/output
Tjenesten kalles med en POST-request, hvor input-dataene legges i request-bodyen. For nærmere detaljer, se Swagger.

### Avhengigheter
bidrag-beregn-forskudd-rest kaller maven-modul bidrag-beregn-forskudd-core, hvor selve beregningen gjøres.<br/>
Sjablonverdier hentes ved å kalle tjeneste bidrag-sjablon.

### Sikkerhet
Det er ingen sikkerhet, da tjenesten ikke behandler sensitive data.

### Funksjonalitet
Tjenesten tar inn parametre knyttet til bidragsmottaker og barnet det søkes om bidrag for. Sjablonverdier som er nødvendige for beregningen hentes fra tjeneste bidrag-sjablon. Det gjøres en mapping fra rest-tjenestens input-grensesnitt til core-tjenestens input-grensesnitt før denne kalles. Ifbm. med mappingen gjøres det en kontroll på felter som ikke kan inneholde null. Hvis noen av disse inneholder null, kastes UgyldigInputException, som resulterer i statuskode 400 (Bad Request). Ved feil i kall til bidrag-sjablon kastes SjablonConsumerException, som resulterer i statuskode 500 (Internal Server Error). Kall til beregningen (i core-modulen), kan også resultere i feil som vil kaste exception (avhengig av type feil).

