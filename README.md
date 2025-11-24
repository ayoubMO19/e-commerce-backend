# VEXA E-Commerce Backend (Java + Spring Boot)

Backend completo para un sistema E-Commerce de venta de zapatillas, desarrollado con el objetivo de aprender arquitectura backend real, gestión de relaciones complejas, autenticación, carrito por usuario, productos, categorías y flujo completo de pedidos.

El proyecto está en desarrollo continuo y sigue una planificación semanal organizada para simular un entorno profesional.

---

## 🚀 Stack Tecnológico
- **Backend:** Java 17+, Spring Boot 3  
- **Persistencia:** Hibernate/JPA + PostgreSQL  
- **Seguridad:** Spring Security + JWT  
- **Frontend:** React + Axios  
- **Infraestructura:** Docker, Docker Compose  
- **Testing:** JUnit  
- **Build:** Maven  

---

## 📌 Funcionalidades principales
- Registro e inicio de sesión (JWT)  
- Carrito por usuario  
- Gestión de productos y categorías  
- Comentarios de productos  
- Flujo completo de pedidos:  
  - Procesando → Confirmado → Preparando → Enviado  
- Cancelación de pedido (antes de Enviado)  
- Devoluciones integradas  
- Gestión de stock  

🔎 **Documentación completa:**  
- [`/docs/requirements.md`](./docs/requirements.md)

---

## 🗄️ Base de Datos (resumen)
- PostgreSQL  
- Modelo relacional con claves compuestas  
- Relaciones 1:N, N:1 y 1:1

📄 Documentación completa:  
- [`/docs/database.md`](./docs/database.md)  
- [`/docs/sql/schema.sql`](./docs/sql/schema.sql)

---

## 🐳 Instalación rápida con Docker
```bash
docker compose up -d
```

▶️ Ejecución local
```
mvn clean package
java -jar target/ecommerce-backend.jar
```
📄 Documentación completa:
- [`/docs/setup.md`](./docs/setup.md)  

---
## 📂 Estructura (Domain-Driven)
```
src/
 ├── users/
 ├── products/
 ├── comments/
 ├── categories/
 ├── orders/
 └── cart/
```

