# Base de Datos — Documentación

## 🧩 Tablas principales
- users
- products
- categories
- comments
- carts
- cart_items
- orders
- order_items

## 📌 Relaciones

### Users ↔ Orders
1:N

### Users ↔ Cart
1:1

### Products ↔ Categories
N:1

### Cart ↔ Products
N:N mediante `cart_items` (ID compuesta)

### Orders ↔ Products
N:N mediante `order_items` (ID compuesta)

---

## 🗃 Claves compuestas
### cart_items
- cart_id
- product_id

### order_items
- order_id
- product_id

Ambas usan:
- `@Embeddable`
- `@EmbeddedId`

---

## 📄 SQL completo
Ver `/docs/sql/schema.sql`
