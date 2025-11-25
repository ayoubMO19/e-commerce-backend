# Setup del Proyecto

## 1️⃣ Requisitos
- Java 17
- Maven
- Docker
- IntelliJ

## 2️⃣ Levantar PostgreSQL
```bash
docker compose up -d
```

## 3️⃣ Ejecutar backend
```bash
mvn clean package
java -jar target/ecommerce-backend.jar
```

## 4️⃣ Configuración en `application.yaml`
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/vexadb
    username: admin
    password: vexa
```
