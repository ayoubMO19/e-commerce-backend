# VEXA E-Commerce Backend (Spring Boot + PostgreSQL)

Backend educativo desarrollado en **Java + Spring Boot** para aprender arquitectura real de un sistema E-Commerce:  
gestión de usuarios, productos, categorías, carrito, pedidos y flujo completo del checkout.

El proyecto crece semana a semana siguiendo una planificación enfocada en buenas prácticas,  
arquitectura limpia y preparación para entorno profesional.

---

## 🚀 Tecnologías

- **Java 17+**
- **Spring Boot 3 (Web, JPA, Validation)**
- **PostgreSQL**
- **Hibernate**
- **Maven**
- **Lombok**
- **Postman (testing manual)**

*(Autenticación, seguridad y JWT se implementarán la semana siguiente)*

---

## 📌 Módulos implementados

### ✔ Users
- Crear usuario
- Obtener usuario por ID
- Validación básica de datos (email, vacío, etc.)

### ✔ Categories
- Crear categorías
- Listar categorías

### ✔ Products
- CRUD básico
- Relación con Category
- Validaciones:
  - precio > 0
  - stock ≥ 0

### ✔ Cart
- Carrito por usuario (1 carrito por user)
- Añadir productos al carrito
- Actualizar cantidades
- Eliminar productos
- Vaciar carrito

### ✔ Orders
- Crear un pedido desde el carrito
- Guardar cada item del pedido con:
  - cantidad
  - precio pagado
  - reducción de stock automática
- Historial de pedidos por usuario
- TotalPrice automático

---

## 📦 Flujo del carrito → pedido

1️⃣ El usuario añade productos al carrito  
2️⃣ Consulta su carrito cuando quiera (GET)  
3️⃣ Hace checkout llamando a:  
```
POST /orders/{userId}
```
4️⃣ Se genera el pedido:
- Items se copian desde el carrito  
- Stock se descuenta  
- totalPrice se calcula  
- Carrito se vacía  

5️⃣ El usuario puede ver su historial:  
```
GET /orders/user/{userId}
```

---

## 🗄️ Base de Datos

### Tablas principales:
- users  
- categories  
- products  
- cart  
- cart_items  
- orders  
- order_items

Las relaciones están correctamente mapeadas con JPA usando:
- @OneToMany  
- @ManyToOne  
- @JoinColumn  
- @EmbeddedId (para OrderItemsId y CartItemsId)

---

## ▶️ Cómo ejecutar el proyecto

### 1. Configurar PostgreSQL
Crear base de datos:
```sql
CREATE DATABASE vexadb;
```

### 2. Configurar `application.yaml`
```
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/vexadb
    username: admin
    password: vexa
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

server:
  port: 8082
```

### 3. Ejecutar Spring Boot
Desde IntelliJ:
- Botón "Run"

O por terminal:
```bash
mvn spring-boot:run
```

---

## 📬 Endpoints (resumen)

La documentación completa está en Notion con detalles de cada endpoint.
- [Notion - Endpoints](https://aged-stag-a8e.notion.site/Endpoints-2bee038a025c80629569c161c6614f59?source=copy_link)

### Users
```
POST /users
GET  /users/{id}
```

### Categories
```
POST /categories
GET  /categories
```

### Products
```
POST /products
GET  /products
GET  /products/{id}
DELETE /products/{id}
```

### Cart
```
POST /cart/{userId}
GET  /cart/{userId}
DELETE /cart/item/{cartItemId}
DELETE /cart/clear/{userId}
```

### Orders
```
POST /orders/{userId}
GET  /orders/user/{userId}
```

---

## 📅 Roadmap

### ✔ Semana actual (completada)
- Orders
- Documentación
- Testing completo en Postman
- Flujo del carrito a pedido

### ⏳ Semana siguiente (planificada)
- Autenticación (Register + Login)
- JWT completo
- Roles (USER / ADMIN)
- Seguridad en endpoints
- Password hashing (BCrypt)
- Validaciones avanzadas
- Documentación final de Auth

---

## 🧑‍💻 Autor
**Ayoub Morghi — Backend Developer (Java & Spring Boot)**  
Proyecto creado con intención de aprendizaje real, buenas prácticas y preparación profesional.
