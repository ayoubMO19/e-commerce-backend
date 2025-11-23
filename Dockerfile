# Usa Java 17 como base
FROM eclipse-temurin:17-jdk-jammy

# Directorio dentro del contenedor
WORKDIR /app

# Copia el JAR al contenedor
COPY target/ecommerce-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto de la app
EXPOSE 8080

# Comando para iniciar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
