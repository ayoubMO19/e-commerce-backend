# VEXA E-Commerce Backend

Production-oriented e-commerce backend built with Spring Boot, focused on clean architecture, secure authentication, strict business rules, and real Stripe payment integration.

## 📚 Detailed Documentation

For a complete breakdown of the project architecture, implementation decisions, development process, and technical documentation:

👉 **[View Full Notion Documentation](https://aged-stag-a8e.notion.site/Java-Spring-Boot-E-commerce-2b1e038a025c80cfb7acd698e9766724?pvs=74)**

---

## 🚀 Tech Stack

* Java 21
* Spring Boot
* Spring Security
* JWT Authentication
* PostgreSQL
* Stripe API
* JPA / Hibernate
* JUnit & Mockito

---

## ✨ Key Features

### Authentication & Security

* JWT-based stateless authentication
* Role-based authorization (`USER` / `ADMIN`)
* Password hashing with BCrypt
* Email verification workflow
* Password recovery flow

### E-Commerce Core

* Product and category management
* Shopping cart system
* Order creation and tracking
* Historical order records
* Inventory management

### Stripe Integration

* PaymentIntent workflow
* Verified Stripe webhooks
* Secure payment confirmation
* Idempotent payment processing

### Business Rules

* Backend never trusts client-side data
* User identity extracted exclusively from JWT
* Order state transitions strictly controlled
* Product prices stored historically in order items
* Stock automatically updated when orders are created

---

## 🏗️ Architecture

The project follows a domain-oriented modular architecture.

Each domain contains its own:

* Controllers
* Services
* Repositories
* DTOs
* Entities

Main domains:

* Auth
* Users
* Products
* Categories
* Cart
* Orders
* Payments

The goal is to keep business logic isolated from infrastructure concerns and external integrations.

---

## 💳 Payment Flow

1. User creates an order
2. Backend generates a Stripe PaymentIntent
3. Frontend completes payment through Stripe Elements
4. Stripe sends a signed webhook
5. Backend verifies the signature
6. Order status changes from `PENDING` to `PAID`

The frontend can never mark an order as paid.

---

## 🧪 Testing

* Unit tests with JUnit and Mockito
* Stripe integrations fully mocked
* No real Stripe calls during testing

---

## ▶️ Run Locally

```bash
mvn spring-boot:run
```

Configure your PostgreSQL database and Stripe credentials inside:

```yaml
application.yaml
```

---

## 👨‍💻 Author

**Ayoub Morghi Ouhda**

Full Stack Developer | Node.js · TypeScript · React · Java · SQL/NoSQL
