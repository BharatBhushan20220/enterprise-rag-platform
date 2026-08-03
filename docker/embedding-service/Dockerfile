FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY common-library/pom.xml common-library/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY document-service/pom.xml document-service/pom.xml
COPY embedding-service/pom.xml embedding-service/pom.xml
COPY search-service/pom.xml search-service/pom.xml
COPY chat-service/pom.xml chat-service/pom.xml
COPY common-library common-library
COPY auth-service auth-service
COPY api-gateway api-gateway
COPY document-service document-service
COPY embedding-service embedding-service
COPY search-service search-service
COPY chat-service chat-service
RUN mvn -pl embedding-service -am -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/embedding-service/target/embedding-service-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
