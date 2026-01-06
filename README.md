# VEXA E-Commerce Backend
📚 **Documentación detallada**
- Si desea una documentación más detallada con cada checklist realizado para llevar a cabo el proyecto:  
[**Java Spring Boot E-commerce - Notion Documentation**](https://aged-stag-a8e.notion.site/Java-Spring-Boot-E-commerce-2b1e038a025c80cfb7acd698e9766724?pvs=74)

**Spring Boot · JWT · Stripe · PostgreSQL**

Backend de e-commerce desarrollado en Spring Boot, orientado a producción, con arquitectura modular por dominios, seguridad stateless con JWT, control estricto de acceso y pagos reales con Stripe usando PaymentIntent y Webhooks verificados.

El sistema está diseñado para **no confiar nunca en el cliente**, aplicar **reglas de dominio explícitas** y mantener aislamiento entre lógica de negocio, infraestructura y frameworks externos.

## 📌 Descripción del proyecto

VEXA E-commerce cubre el flujo completo de un e-commerce real:
- Autenticación segura con JWT
- Gestión de usuarios, productos, carrito y pedidos
- Creación de pedidos desde carrito
- Pagos reales con Stripe
- Confirmación de pago vía Webhooks
- Gestión estricta de estados del pedido
- Protección total frente a manipulación desde frontend

> **El backend nunca acepta datos críticos desde el cliente** como `userId`, precios, estados de pedido o pagos.

## 🧱 Arquitectura

### Arquitectura por dominios

El proyecto está organizado por bounded contexts funcionales:
- `Auth`
- `Users`
- `Categories`
- `Products`
- `Cart`
- `Orders`
- `Payment`
- `Security`
- `Comments`
- `Exceptions`
- `Utils`
- `config`

Cada dominio es autónomo, sin dependencias circulares, y contiene internamente sus propias capas.

### Estructura interna de un dominio

Dentro de cada dominio se sigue una separación clara:

- **Controller**
   - Endpoints REST
   - Validación de entrada mediante DTOs
   - Sin lógica de negocio

- **Service**
   - Lógica de negocio
   - Reglas de dominio
   - Orquestación de flujos
   - No conoce HTTP ni detalles de frameworks externos

- **Repository**
   - Acceso a datos con JPA
   - Queries explícitas cuando es necesario
   - Sin lógica de negocio

- **DTOs**
   - Requests y Responses
   - Separación clara del modelo de dominio

- **Entities**
   - Modelo persistente
   - Relaciones JPA bien definidas

## 🔌 Integraciones externas

### Stripe
- Integración aislada mediante `StripeClient` (wrapper propio)
- Los servicios de dominio no dependen directamente del SDK de Stripe
- Facilita testing y mockeo
- Webhooks verificados por firma

## 🔐 Flujo de autenticación

1. Usuario se registra
2. Se envía email de verificación
3. Usuario verifica email
4. Login devuelve JWT
5. JWT se envía en `Authorization: Bearer <token>`

El backend:
- Extrae `userId` y roles del JWT
- **Nunca** acepta `userId` desde la request
- Protege endpoints por rol

**Características:**
- Stateless
- BCrypt para contraseñas
- Roles: `USER` / `ADMIN`
- Autorización basada exclusivamente en JWT

## 💳 Flujo de pagos con Stripe

Flujo seguro end-to-end:

1. Usuario crea un pedido desde el carrito  
   → `Order` queda en estado `PENDING`
2. Backend crea un `PaymentIntent` en Stripe
3. Backend devuelve `clientSecret`
4. Frontend confirma el pago con Stripe Elements
5. Stripe envía webhook `payment_intent.succeeded`
6. Backend:
   - Verifica la firma del webhook
   - Valida el tipo de evento
   - Busca el pedido por `paymentIntentId`
   - Cambia el estado a `PAID`

**Reglas:**
- El frontend **nunca** marca pedidos como pagados
- Solo Stripe vía webhook puede hacerlo
- Flujo idempotente

## 📦 Estados de Order

**Estados posibles:**
- `PENDING`
- `PAID`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

**Reglas de dominio:**
- Solo pedidos `PENDING` pueden pagarse
- Stripe solo puede mover `PENDING` → `PAID`
- Pedidos pagados no se modifican
- El stock se reduce al crear el pedido
- El precio del producto se copia al order item (histórico)

## 🧩 Dominios principales

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
- `userId` siempre desde JWT

### Orders
- Crear pedido desde carrito
- Copia de productos y precios
- Cálculo automático del total
- Historial por usuario

### Payments
- Creación de PaymentIntent
- Webhook seguro
- Verificación de firma
- Cambio de estado controlado

## 🗄️ Base de datos

**Tablas principales:**
- `users`
- `roles`
- `categories`
- `products`
- `cart`
- `cart_items`
- `orders`
- `order_items`

**Relaciones:**
- OneToMany
- ManyToOne
- EmbeddedId (`cart_items`, `order_items`)

## ▶️ Ejecución del proyecto

### 1️⃣ Crear base de datos
```sql
CREATE DATABASE vexadb; # O el nombre que hayamos configurado
```

### 2️⃣ Configurar `application.yaml`
```yaml
# ADAPTAR VALORES SEGÚN CONFIGURACIÓN PROPIA
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
   mail:
      host: localhost
      port: 1025
      properties:
         mail:
            smtp:
               auth: false
               starttls:
                  enable: false
      test-connection: false  # Opcional para debug

app:
   mail:
      from: remittentTest@remittentTest.com

server:
   port: 8082

logging:
   level:
      org.springframework.mail: DEBUG

jwt:
   secret: TX...

stripe:
   secret-key: sk_test...
   webhook-secret: whsec_...

springdoc:
   enable-native-support: true
```

### 3️⃣ Ejecutar
```shell
mvn spring-boot:run
```

## 🧪 Testing

- Tests unitarios con JUnit + Mockito
- Stripe completamente mockeado
- Sin llamadas reales a Stripe en tests

**Webhooks locales:**
```shell
stripe listen --forward-to localhost:8082/api/payments/webhook
```

**Tarjeta de prueba:**
- `4242 4242 4242 4242`
- Fecha futura
- CVC cualquiera

## 📬 Endpoints (resumen)

### Auth
- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/me`

### Cart
- `GET /api/cart`
- `POST /api/cart/add`
- `PUT /api/cart/update`
- `DELETE /api/cart/delete`

### Orders
- `POST /api/orders`
- `GET /api/orders/me`

### Payments
- `POST /api/payments/create-intent`
- `POST /api/payments/webhook`

## 🧭 Roadmap

### ✅ Completado
- Arquitectura por dominios
- Seguridad real con JWT
- Stripe end-to-end
- Wrapper externo + tests
- Reglas de negocio estrictas

### Próximos pasos
- Frontend
- Docker
- CI/CD
- Logging estructurado

## 👨‍💻 Autor

**Ayoub Morghi**  
Backend Developer · Java · Spring Boot

> Proyecto desarrollado con foco en arquitectura limpia, reglas de dominio y seguridad real.
