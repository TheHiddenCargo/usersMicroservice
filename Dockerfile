#Construccon
# Etapa 1: Construcción
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

#Produccion
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar playerManager.jar

EXPOSE 8080

# Ejecutar la aplicación
CMD ["java", "-jar", "playerManager.jar"]