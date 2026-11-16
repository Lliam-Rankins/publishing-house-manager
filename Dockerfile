FROM maven:3.9-eclipse-temurin-21
WORKDIR /opt/app

COPY pom.xml .
COPY src ./src

ENV TEST="false"
ENV LINT="false"
EXPOSE 8080
CMD if [ "$LINT" = "true" ]; then mvn compile -Xlint:all -Werror; elif [ "$TEST" = "true" ]; then mvn test; else mvn compile exec:java -Dexec.mainClass="publishinghouse.Main"; fi  