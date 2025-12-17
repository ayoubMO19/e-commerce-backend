# VEXA E-Commerce Backend — Spring Boot · JWT · Stripe · PostgreSQL

Backend de e-commerce desarrollado en **Spring Boot** con arquitectura modular, seguridad completa con **JWT + roles**, verificación por email, recuperación de contraseña y **pagos reales integrados con Stripe** (PaymentIntent + Webhooks).

El proyecto sigue una evolución progresiva orientada a **buenas prácticas**, **seguridad real**, **flujo end-to-end** y preparación para entorno profesional.

---

## 🚀 Tecnologías

- **Java 17**
- **Spring Boot 3**
  - Web
  - Spring Security
  - JPA / Hibernate
  - Validation
- **JWT (Auth stateless)**
- **Stripe API (PaymentIntent + Webhooks)**
- **PostgreSQL**
- **Maven**
- **Lombok**
- **Postman (testing manual)**
- **Stripe CLI (testing webhooks en local)**

---

## 🔐 Autenticación y Seguridad

- Registro de usuario
- Login con JWT
- Roles:
  - USER
  - ADMIN
- Endpoints protegidos por rol
- Acceso a recursos **siempre desde el JWT** (nunca desde el request)
- Verificación de email
- Reset de contraseña por email
- Password hashing con **BCrypt**
- Protección total frente a acceso a recursos de otros usuarios

---

## 💳 Pagos con Stripe (End-to-End)

Implementación completa de pagos reales con Stripe:

- Creación de PaymentIntent desde el backend
- Asociación del orderId en metadata
- Confirmación del pago desde frontend (Stripe Elements)
- Recepción de eventos mediante **Webhooks**
- Verificación de firma del webhook
- Actualización segura del estado del pedido (PENDING → PAID)
- Manejo idempotente (múltiples eventos, una sola actualización)

> El backend **no confía nunca** en el frontend para marcar pedidos como pagados.

---

## 📦 Módulos implementados

### ✔ Users
- Registro
- Login
- Perfil /me
- Roles
- Verificación de email
- Reset de contraseña

### ✔ Categories
- Crear categorías (ADMIN)
- Listar categorías

### ✔ Products
- CRUD (ADMIN)
- Relación con Category
- Validaciones:
  - precio > 0
  - stock ≥ 0

### ✔ Cart
- 1 carrito por usuario
- Añadir productos
- Actualizar cantidades
- Eliminar productos
- Obtener carrito
- **UserId siempre obtenido del JWT**

### ✔ Orders
- Crear pedido desde carrito
- Copia de items:
  - cantidad
  - precio pagado
- Reducción de stock automática
- totalPrice calculado automáticamente
- Estados:
  - PENDING
  - PAID
  - SHIPPED
  - DELIVERED
  - CANCELLED
- Historial de pedidos por usuario

### ✔ Payments
- Crear PaymentIntent
- Webhook seguro (/api/payments/webhook)
- Validación de firma Stripe
- Actualización del estado del pedido solo si:
  - Evento = payment_intent.succeeded
  - Order está en PENDING

---

## 🔄 Flujo completo de compra

1. Usuario autenticado añade productos al carrito  
2. Consulta su carrito  
3. Crea pedido (order queda en PENDING)  
4. Backend crea PaymentIntent (Stripe)  
5. Frontend confirma pago con Stripe Elements  
6. Stripe envía webhook al backend  
7. Backend valida firma y evento  
8. Pedido pasa a PAID  

---

## 🗄️ Base de Datos

### Tablas principales
- users
- roles
- categories
- products
- cart
- cart_items
- orders
- order_items

Relaciones JPA:
- @OneToMany
- @ManyToOne
- @JoinColumn
- @EmbeddedId (CartItems / OrderItems)

---

## ▶️ Cómo ejecutar el proyecto

### 1️⃣ Crear base de datos

    CREATE DATABASE vexadb;

### 2️⃣ Configurar application.yaml

    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/vexadb
        username: admin
        password: vexa
      jpa:
        hibernate:
          ddl-auto: update

    server:
      port: 8082

    jwt:
      secret: your_jwt_secret

    stripe:
      secret-key: sk_test_...
      webhook-secret: whsec_...

### 3️⃣ Ejecutar backend

    mvn spring-boot:run

---

## 🧪 Testing

Testing manual completo con Postman.

Stripe CLI para webhooks:

    stripe listen --forward-to localhost:8082/api/payments/webhook

Tarjeta de prueba Stripe:

- 4242 4242 4242 4242  
- Cualquier fecha futura  
- CVC cualquiera  

---

## 📬 Endpoints (resumen)

Todos los endpoints sensibles requieren JWT.

### Auth
- POST /auth/register
- POST /auth/login
- GET  /auth/me
- POST /auth/verify-email
- POST /auth/reset-password

### Cart
- GET    /api/cart
- POST   /api/cart/add
- PUT    /api/cart/update
- DELETE /api/cart/delete

### Orders
- POST /api/orders
- GET  /api/orders/me

### Payments
- POST /api/payments/create-intent
- POST /api/payments/webhook

---

## 🧭 Roadmap

✔ Auth + JWT + Roles  
✔ Email verification  
✔ Password reset  
✔ Stripe payments end-to-end  
✔ Seguridad real en endpoints  
✔ Webhooks seguros  
✔ Refactor de userId desde JWT  

### ⏳ Próximos pasos

- Tests unitarios (JUnit)
- Frontend (React)
- CI/CD
- Docker
- Caching
- Logs estructurados

---

## 🧑‍💻 Autor

**Ayoub Morghi**  
Backend Developer · Java · Spring Boot  

Proyecto desarrollado con enfoque en:

- arquitectura limpia  
- seguridad real  
- buenas prácticas  
- preparación profesional
