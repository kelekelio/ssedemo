FROM openjdk:17-slim

EXPOSE 8080

RUN apt update

RUN apt-get --assume-yes install watch

RUN apt-get --assume-yes install net-tools

RUN mkdir -p tekton

ADD /target/ssedemo-0.0.1-SNAPSHOT.jar tekton

WORKDIR tekton

CMD java -jar ssedemo-0.0.1-SNAPSHOT.jar
