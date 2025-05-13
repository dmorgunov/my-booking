# 🏨 Booking System Monolith (Spring Boot 3 + PostgreSQL + Redis)

A Java 21 monolithic booking system built with **Spring Boot**, **Spring Data JPA**, **Liquibase**, and **Redisson (Redis)** for caching and TTL-based payment emulation.

---

## ✨ Features

- **CRUD for Units and Users**
- **Booking a Unit** with availability check
- **Automatic Booking Expiration** if payment isn't made in 15 minutes (via Redis TTL)
- **Payment Management**
- **Unit Search** with pagination, sorting, and filtering
- **Caching** of available unit count (Redis via Redisson)
- **DTO-based API**
- **Liquibase** schema and initial data (10 units)
- **Postman Collection** included

---

## 🧱 Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA
- PostgreSQL (via Docker)
- Redis + Redisson
- Liquibase
- Lombok
- Gradle
- Swagger/OpenAPI (Springdoc)

---

## 🚀 Getting Started

### 1. Clone & Build

```bash
git clone https://github.com/dmorgunov/my-booking
cd booking-system
./gradlew clean build
```

### 2. Start Dependencies

```bash
docker-compose up -d
```

This runs:
- PostgreSQL on port `5432`
- Redis on port `6379`

### 3. Run the App

```bash
./gradlew bootRun
```

App will be available at: [http://localhost:8080](http://localhost:8080)

---

## 🔧 Configuration

Default `application.yml` configures:
- PostgreSQL via JDBC
- Liquibase change logs
- Redis via Redisson (localhost:6379)

---

## 🧪 API Usage

📄 Import [`booking-system-dto-api.postman_collection.json`](./booking-system-dto-api.postman_collection.json) into Postman.

Main endpoints:

| Method | URL | Description |
|--------|-----|-------------|
| `POST` | `/api/users` | Create user |
| `POST` | `/api/units` | Create unit |
| `GET`  | `/api/units/search` | Search units |
| `POST` | `/api/bookings/book` | Book a unit |
| `POST` | `/api/bookings/{id}/confirm` | Confirm payment |
| `POST` | `/api/bookings/{id}/cancel` | Cancel booking |
| `GET`  | `/api/bookings/{id}/payment` | View payment |
| `POST` | `/api/payments/save` | Save payment manually |
| `GET`  | `/api/bookings/available-units` | Cached unit availability |

---

## 🧩 Database

Liquibase runs:
- `01-create-tables.xml` – Schema
- `02-insert-units.xml` – Inserts 1 test user + 10 sample units

90 more units are inserted **at runtime** via a `@PostConstruct` loader.

---

## 📚 Swagger UI

Docs available at:

```
http://localhost:8080/swagger-ui.html
```

---

## 🤝 Contributing

PRs and feedback welcome!