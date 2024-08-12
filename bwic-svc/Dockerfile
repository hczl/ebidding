FROM openjdk:17

COPY ./target/bwic-svc-0.0.1-SNAPSHOT.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch bwic-svc-0.0.1-SNAPSHOT.jar'

ENTRYPOINT ["java", "-jar", "bwic-svc-0.0.1-SNAPSHOT.jar"]