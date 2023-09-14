FROM openjdk:17

EXPOSE 8080

RUN mkdir -p tekton

ADD /target/ssedemo-0.0.1-SNAPSHOT.jar tekton

WORKDIR tekton

CMD java -jar ssedemo-0.0.1-SNAPSHOT.jar