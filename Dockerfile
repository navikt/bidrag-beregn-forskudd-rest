FROM navikt/java:13
LABEL maintainer="Team Bidrag" \
      email="bidrag@nav.no"

COPY ./target/app-exec.jar app.jar
COPY init-scripts /init-scripts

EXPOSE 8080
