FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY app/mvnw .
COPY app/.mvn .mvn
COPY app/pom.xml .
COPY app/src src
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
