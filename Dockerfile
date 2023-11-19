FROM openjdk:21
VOLUME /tmp
WORKDIR /app
COPY target/banking-application-0.0.1-SNAPSHOT.jar banking-application.jar
ENTRYPOINT ["java", "-jar", "banking-application.jar"]