FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy all pom.xml files
COPY pom.xml .
COPY app/pom.xml app/
COPY chatinteraction/pom.xml chatinteraction/
COPY common/pom.xml common/
COPY config/pom.xml config/
COPY documentingestion/pom.xml documentingestion/
COPY identityaccess/pom.xml identityaccess/

# Copy the Maven wrapper files
COPY .mvn .mvn
COPY mvnw .

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY . .

# Build the application
RUN ./mvnw clean install -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/app/target/app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
