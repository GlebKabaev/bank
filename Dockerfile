FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/bank_rest-0.0.1-SNAPSHOT.jar bank_rest.jar
ENTRYPOINT ["java", "-jar", "bank_rest.jar"]