# bidrag-beregn-forskudd-rest

[![](https://github.com/navikt/bidrag-beregn-forskudd-rest/actions/workflows/ci.yaml/badge.svg)](https://github.com/navikt/bidrag-beregn-forskudd-rest/actions/workflows/ci.yaml)
[![](https://github.com/navikt/bidrag-beregn-forskudd-rest/actions/workflows/pr.yaml/badge.svg)](https://github.com/navikt/bidrag-beregn-forskudd-rest/actions/workflows/pr.yaml)
[![](https://github.com/navikt/bidrag-beregn-forskudd-rest/actions/workflows/release.yaml/badge.svg)](https://github.com/navikt/bidrag-beregn-forskudd-rest/actions/workflows/release.yaml)

Mikrotjeneste / Rest-API for beregning av bidragsforskudd, som er satt opp til å kjøre på NAIS i GCP.

### Tilgjengelige tjenester i dev (endepunkter)
Request-URL: [https://bidrag-beregn-forskudd-rest.intern.dev.nav.no/beregn/forskudd](https://bidrag-beregn-forskudd-rest.intern.dev.nav.no/beregn/forskudd)

Swagger-UI: [https://bidrag-beregn-forskudd-rest.intern.dev.nav.no/](https://bidrag-beregn-forskudd-rest.intern.dev.nav.no/)

### Input/output
Tjenesten kalles med en POST-request, hvor input-dataene legges i request-bodyen. For nærmere detaljer, se Swagger.

### Avhengigheter
`bidrag-beregn-forskudd-rest` kaller maven-modul `bidrag-beregn-forskudd-core`, hvor selve beregningen gjøres. Sjablonverdier hentes ved å kalle tjenesten `bidrag-sjablon`.

### Sikkerhet
Tjenesten er sikret med Azure AD JWT tokens. Konsumenter av tjenesten er derfor nødt til å registere seg og benytte gyldig token i `Authorization` header ved REST-kall. Dersom en ny applikasjon skal ha tilgang må dette også registreres i henholdsvis `nais.yaml` og `nais-p.yaml` i denne applikasjonen.

### Funksjonalitet
Tjenesten tar inn parametre knyttet til bidragsmottaker og barnet det søkes om bidrag for. Sjablonverdier som er nødvendige for beregningen hentes fra tjenesten `bidrag-sjablon`.

Det gjøres en mapping fra rest-tjenestens input-grensesnitt til core-tjenestens input-grensesnitt før denne kalles. Ifbm. med mappingen gjøres det en kontroll på felter som ikke kan inneholde null. Hvis noen av disse inneholder null, kastes UgyldigInputException, som resulterer i statuskode 400 (Bad Request). Ved feil i kall for henting av sjablonverdier kastes `SjablonConsumerException`, som resulterer i statuskode 500 (Internal Server Error). Kall til beregningen (i core-modulen), kan også resultere i feil som vil kaste exception (avhengig av type feil).

### Kjøre applikasjon lokalt
Applikasjonen kan kjøres opp lokalt med fila `BidragBeregnForskuddLocal`. Applikasjonen kjøres da opp på [http://localhost:8080/](http://localhost:8080/) og kan testes med Swagger. Også når applikasjonen kjøres lokalt kreves et gyldig JWT-token, men her kreves ikke et gyldig Azure AD token. Lokalt er applikasjonen konfugurert til å bruke en lokalt kjørende MockOAuth-service for å utstede og validere JWT-tokens. For å utstede et gylig token til testing kan man benytte endepunktet `GET /local/cookie?issuerId=aad&audience=aud-localhost`. Viktig at `issuerId=aad` og `audience=aud-locahost`.

## Utstede gyldig token i dev-gcp
For å kunne teste applikasjonen i `dev-gcp` trenger man et gyldig AzureAD JWT-token. For å utstede et slikt token trenger man miljøvariablene `AZURE_APP_CLIENT_ID` og `AZURE_APP_CLIENT_SECRET`. Disse ligger tilgjengelig i de kjørende pod'ene til applikasjonen.

Koble seg til en kjørende pod (feature-branch):
```
kubectl -n bidrag exec -i -t bidrag-beregn-forskudd-rest-feature-<sha> -c bidrag-beregn-forskudd-rest-feature -- /bin/bash
```

Koble seg til en kjørende pod (main-branch):
```
kubectl -n bidrag exec -i -t bidrag-beregn-forskudd-rest-<sha> -c bidrag-beregn-forskudd-rest -- /bin/bash
```

Når man er inne i pod'en kan man hente ut miljøvariablene på følgende måte:
```
echo "$( cat /var/run/secrets/nais.io/azure/AZURE_APP_CLIENT_ID )"
echo "$( cat /var/run/secrets/nais.io/azure/AZURE_APP_CLIENT_SECRET )"
```

Deretter kan et gyldig Azure AD JWT-token hentes med følgende kall (feature-branch):
```
curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -d 'client_id=<AZURE_APP_CLIENT_ID>&scope=api://dev-gcp.bidrag.bidrag-beregn-forskudd-rest-feature/.default&client_secret=<AZURE_APP_CLIENT_SECRET>&grant_type=client_credentials' 'https://login.microsoftonline.com/966ac572-f5b7-4bbe-aa88-c76419c0f851/oauth2/v2.0/token'
```

Deretter kan et gyldig Azure AD JWT-token hentes med følgende kall (main-branch):
```
curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -d 'client_id=<AZURE_APP_CLIENT_ID>&scope=api://dev-gcp.bidrag.bidrag-beregn-forskudd-rest/.default&client_secret=<AZURE_APP_CLIENT_SECRET>&grant_type=client_credentials' 'https://login.microsoftonline.com/966ac572-f5b7-4bbe-aa88-c76419c0f851/oauth2/v2.0/token'
```
### Kjøre lokalt
Kjør ```initLocalEnv.sh``` skriptet for å sette opp miljøvariabler for lokal kjøring.
<br/>
Dette vil hente Azure hemmeligheter og diverse miljøvariabler fra POD kjørende i dev

Hvis du ikke får `permission denied` når du prøver å kjøre skriptet så må du gi deg selv tilgang til å kjøre shell skript med følgende kommand:
```bash
Kjør chmod +x ./initLocalEnv.sh
```

Du kan da starte opp applikasjonen ved å kjøre [BidragBeregnForskuddLokalNais.kt](src/test/kotlin/no/nav/bidrag/beregn/forskudd/rest/BidragBeregnForskuddLokalNais.kt)