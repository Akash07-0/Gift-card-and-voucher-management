# Secure Voucher System

A full-stack voucher management system built with Spring Boot, MySQL, JWT authentication, and HTML/CSS/JavaScript.

## Features

- Customer registration and login
- Admin login
- BCrypt password hashing
- JWT authentication
- ADMIN / CUSTOMER role-based authorization
- Admin voucher creation
- Voucher update and deactivation
- Voucher expiry validation
- Maximum usage limit
- Duplicate redemption prevention
- Customer redemption history
- Admin redemption history
- MySQL database persistence
- Bean validation
- Global exception handling
- Swagger / OpenAPI documentation

## Technology Stack

### Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- JWT
- Maven

### Frontend

- HTML
- CSS
- JavaScript

## Main API Flow

### Authentication

POST /api/auth/register

POST /api/auth/login

### Admin

POST /api/vouchers

GET /api/vouchers

PUT /api/vouchers/{id}

DELETE /api/vouchers/{id}

GET /api/redemptions

### Customer

GET /api/vouchers/available

POST /api/redemptions

GET /api/redemptions/my-history

## Security

The application uses:

- JWT authentication
- Role-based authorization
- BCrypt password hashing
- Voucher expiry validation
- Maximum usage validation
- Duplicate redemption prevention

Production secrets such as JWT secrets and database passwords should be provided through environment variables and should not be committed to GitHub.

## Running Locally

### Requirements

- Java 17+
- Maven
- MySQL 8+

### Backend

```bash
mvn spring-boot:run