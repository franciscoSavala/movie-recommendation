FROM maven:3.9.6-eclipse-temurin-21-alpine as build

WORKDIR /app

RUN adduser -Du 1001 nonroot

COPY pom.xml .
RUN mvn -e -B dependency:resolve

COPY src ./src

RUN mvn -e -B -DskipTests=true -Dspring.profiles.active=prod package


FROM eclipse-temurin:21-jre-alpine

COPY --from=build /etc/passwd /etc/passwd

COPY --from=build /app/target/backend-0.1.jar .

USER nonroot

EXPOSE 8080

CMD ["java", "-jar", "backend-0.1.jar"]