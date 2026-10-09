FROM maven:3.9-eclipse-temurin-21
WORKDIR /opt/app

COPY pom.xml .
COPY src ./src

ENV TEST="false"
CMD if [ "$TEST" = "true" ]; then mvn test; fi
