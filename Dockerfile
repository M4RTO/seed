FROM hub.fif.tech/base/openjdk-jdk:11-latest as build-env
COPY . /app
WORKDIR /app

RUN apk add --no-cache wget ca-certificates unzip && \
   wget --no-check-certificate https://releases.hashicorp.com/envconsul/0.11.0/envconsul_0.11.0_linux_amd64.zip && \
   unzip envconsul_0.11.0_linux_amd64.zip -d /app && \
   rm envconsul_0.11.0_linux_amd64.zip
RUN ./gradlew clean build jacocoTestReport --info --stacktrace --no-daemon

FROM hub.fif.tech/base/openjdk-jre:11-latest
WORKDIR /app
ADD ./java-entrypoint.sh /app/java-entrypoint.sh
COPY --from=build-env /app/build/libs/*.jar /app/app.jar
COPY --from=build-env /app/envconsul /app/envconsul

USER root
RUN apk --no-cache add curl && \
    chmod a+x /app/java-entrypoint.sh

USER java

ENTRYPOINT ["./java-entrypoint.sh"]
