# Secure Voucher System

A Spring Boot full-stack backend project for secure voucher creation, redemption,
duplicate prevention, usage tracking, JWT authentication and role-based access.

## Features

- Customer registration and login
- Admin login
- BCrypt password hashing
- JWT authentication
- ADMIN / CUSTOMER roles
- Admin voucher CRUD
- Voucher expiry and activation checks
- Maximum usage limit
- Duplicate redemption prevention
- Redemption history
- MySQL persistence
- Bean validation
- Global exception handling
- Swagger UI
- JUnit/Mockito test

## Default Admin

Email: admin@voucher.com
Password: Admin@123

Change this password for real deployment.

## Run locally

1. Install Java 17+ and Maven.
2. Start MySQL.
3. The application uses:
   - database: voucher_db
   - username: root
   - password: root
4. Run:

mvn spring-boot:run

Swagger:
http://localhost:8080/swagger-ui.html

## Main API flow

POST /api/auth/register
POST /api/auth/login

ADMIN:
POST /api/vouchers
GET /api/vouchers
PUT /api/vouchers/{id}
DELETE /api/vouchers/{id}
GET /api/redemptions

CUSTOMER:
GET /api/vouchers/available
POST /api/redemptions
GET /api/redemptions/my-history

## Important security note

Do not commit production JWT secrets or database passwords.
Use environment variables in deployment.
