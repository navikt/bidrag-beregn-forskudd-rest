FROM navikt/java:16
LABEL maintainer="Team Bidrag" \
      email="bidrag@nav.no"

COPY ./target/bidrag-beregn-forskudd-rest-*.jar app.jar

EXPOSE 8080
