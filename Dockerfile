FROM navikt/java:13
LABEL maintainer="Team Bidrag" \
      email="bidrag@nav.no"

ADD ./target/bidrag-beregn-forskudd-*.jar app.jar
COPY init-scripts /init-scripts

EXPOSE 8080
