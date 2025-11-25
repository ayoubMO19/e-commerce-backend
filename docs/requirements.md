# Requisitos del Proyecto E-Commerce

## 🧑‍💻 Usuarios
- Registro
- Login
- JWT
- Gestión de perfil

## 🛒 Carrito
- Carrito único por usuario
- Añadir productos
- Actualizar cantidad
- Eliminar
- Validar stock
- Carrito persistente en DB

## 📦 Productos
- CRUD
- Categoría
- Stock
- Imágenes
- Precio final

## 🏷️ Categorías
- CRUD
- Relación N:1 con productos

## ⭐ Comentarios
- Solo usuarios logueados
- Fecha, texto, relación con producto

## 🧾 Pedidos
Flujo:
1️⃣ Procesando  
2️⃣ Confirmado  
3️⃣ Preparando  
4️⃣ Enviado

- No cancelable cuando está Enviado
- Devoluciones
- Stock

## 🔄 Devoluciones
- Solo después de Enviado

## 🧠 Otros
- Docker Compose
- Logs
- Validaciones DTO  
