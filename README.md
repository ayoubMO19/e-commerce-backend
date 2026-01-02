# VEXA E-Commerce Backend
Spring Boot · JWT · Stripe · PostgreSQL

Backend de e-commerce desarrollado en **Spring Boot** siguiendo una arquitectura limpia por capas, con **seguridad stateless basada en JWT**, control estricto de acceso a recursos y **pagos reales integrados con Stripe** mediante PaymentIntent y Webhooks.

El proyecto está pensado como backend real de producción: reglas claras de dominio, separación de responsabilidades, validaciones, excepciones personalizadas y tests unitarios.

---

## 📌 Descripción del proyecto

VEXA es un backend de e-commerce que cubre el flujo completo de compra:

- Autenticación segura con JWT
- Gestión de usuarios, productos, carrito y pedidos
- Creación de pedidos desde carrito
- Pagos reales con Stripe
- Confirmación de pago vía Webhooks
- Gestión de estados del pedido
- Seguridad total frente a manipulación desde frontend

El backend **no confía nunca en datos críticos enviados por el cliente** (userId, estado del pedido, pago).

---

## 🧱 Arquitectura

Arquitectura por capas clara y desacoplada:

### Controller
- Expone endpoints REST
- Valida input (DTOs)
- No contiene lógica de negocio
- Llama únicamente a servicios

### Service
- Contiene toda la lógica de negocio
- Orquesta flujos (orders, payments, auth)
- Aplica reglas de dominio
- No accede directamente a HTTP ni a SDKs externos

### Repository
- Acceso a datos mediante JPA
- Sin lógica de negocio
- Queries explícitas cuando es necesario

### Integraciones externas
- Stripe aislado mediante **StripeClient (wrapper)**
- Permite mockeo en tests
- Evita dependencia directa del SDK en servicios

---

## 🔐 Flujo de autenticación

1. Usuario se registra
2. Se envía email de verificación
3. Usuario verifica email
4. Login devuelve JWT
5. JWT se envía en `Authorization: Bearer <token>`
6. El backend:
  - Extrae userId y roles del JWT
  - Nunca acepta userId desde request
  - Protége endpoints por rol

Características clave:
- Stateless
- BCrypt para contraseñas
- Roles: USER / ADMIN
- Acceso a recursos validado siempre contra JWT

---

## 💳 Flujo de pagos con Stripe

Flujo completo y seguro end-to-end:

1. Usuario crea un pedido desde el carrito  
   → Order queda en estado **PENDING**
2. Backend crea un **PaymentIntent** en Stripe
3. Backend devuelve `clientSecret` al frontend
4. Frontend confirma el pago con Stripe Elements
5. Stripe envía webhook `payment_intent.succeeded`
6. Backend:
  - Verifica firma del webhook
  - Valida tipo de evento
  - Busca order por `paymentIntentId`
  - Cambia estado del pedido a **PAID**

Reglas clave:
- El frontend **nunca** marca pedidos como pagados
- Solo el webhook válido puede cambiar el estado
- Flujo idempotente (múltiples eventos, una sola actualización)

---

## 📦 Estados de Order y reglas

Estados posibles del pedido:

- **PENDING**
- **PAID**
- **SHIPPED**
- **DELIVERED**
- **CANCELLED**

Reglas de negocio:
- Un pedido solo puede pagarse si está en PENDING
- Stripe solo puede mover PENDING → PAID
- No se permite modificar pedidos pagados
- Stock se reduce al crear el pedido
- Precio del producto se copia al order item (histórico)

---

## 🧩 Módulos principales

### Users
- Registro
- Login
- Perfil
- Roles
- Verificación de email
- Reset de contraseña

### Categories
- Crear (ADMIN)
- Listar

### Products
- CRUD (ADMIN)
- Relación con Category
- Validaciones de precio y stock

### Cart
- Un carrito por usuario
- Añadir / actualizar / eliminar items
- userId siempre desde JWT

### Orders
- Crear pedido desde carrito
- Copia de items y precios
- Cálculo automático del total
- Historial por usuario

### Payments
- Crear PaymentIntent
- Webhook seguro
- Verificación de firma
- Cambio de estado controlado

---

## 🗄️ Base de datos

Tablas principales:
- users
- roles
- categories
- products
- cart
- cart_items
- orders
- order_items

Relaciones JPA:
- OneToMany
- ManyToOne
- EmbeddedId (cart_items, order_items)

---

## ▶️ Cómo ejecutar el proyecto

### 1️⃣ Crear base de datos

```sql
CREATE DATABASE vexadb;
```
### 2️⃣ Configurar application.yaml

```yaml
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
```

### 3️⃣ Ejecutar backend

```bash
mvn spring-boot:run
```

## 🧪 Testing
- Tests unitarios con JUnit + Mockito 
- Stripe aislado mediante wrapper 
- Sin llamadas reales a Stripe en tests

#### Stripe CLI para webhooks locales:
```bash
stripe listen --forward-to localhost:8082/api/payments/webhook
```

#### Tarjeta de prueba:
- 4242 4242 4242 4242 
- Fecha futura 
- CVC cualquiera

## 📬 Endpoints (resumen)
### Auth
- POST /auth/register 
- POST /auth/login 
- GET /auth/me

### Cart
- GET /api/cart 
- POST /api/cart/add 
- PUT /api/cart/update 
- DELETE /api/cart/delete

### Orders
- POST /api/orders 
- GET /api/orders/me

### Payments
- POST /api/payments/create-intent 
- POST /api/payments/webhook

## 🧭 Roadmap
- ✔ Auth + JWT 
- ✔ Seguridad real 
- ✔ Stripe end-to-end 
- ✔ Wrapper + tests 
- ✔ Arquitectura limpia

### Próximos pasos:
- Frontend 
- Docker 
- CI/CD 
- Logs estructurados

## 🧑‍💻 Autor
Ayoub Morghi - Backend Developer · Java · Spring Boot

Proyecto desarrollado con enfoque en arquitectura limpia, seguridad real y estándares profesionales.