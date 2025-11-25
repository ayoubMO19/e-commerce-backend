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
- Cancelación antes de "Enviado"
- Devoluciones
- Gestión de stock

🔎 **Documentación completa:**
- `/docs/requirements.md`

---

## 🗄️ Base de Datos
Ver documentación:
- `/docs/database.md`
- `/docs/sql/schema.sql`

---

## 🐳 Instalación con Docker
```bash
docker compose up -d
```

## ▶️ Ejecución local
```bash
mvn clean package
java -jar target/ecommerce-backend.jar
```

---

## 📂 Estructura
```
src/
 ├── users/
 ├── products/
 ├── comments/
 ├── categories/
 ├── orders/
 └── cart/
```

---

## 📅 Roadmap del proyecto
| Semana | Estado | Detalles |
|--------|--------|----------|
| 1 | ✅ | Modelos + relaciones |
| 2 | 🟡 | Servicios + controladores |
| 3 | ⏳ | Seguridad + JWT |

---

## 🧑‍💻 Autor
**Ayoub Morghi — Java Backend Developer**
