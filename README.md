# Gift Card and Voucher Management System

A full-stack **Gift Card and Voucher Management System** built with **Spring Boot, MySQL, JWT authentication, Spring Security, and HTML/CSS/JavaScript**.

The system allows administrators to create and manage vouchers and gift cards, while customers can securely redeem vouchers and gift cards. Role-based authorization ensures that admin and customer operations are properly protected.

## Features

### Authentication & Authorization

* Customer registration and login
* Admin login
* BCrypt password hashing
* JWT authentication
* ADMIN / CUSTOMER role-based authorization
* Protected REST APIs
* Unauthorized access prevention

### Voucher Management

* Admin voucher creation
* View all vouchers
* Voucher update
* Voucher deactivation
* Voucher expiry validation
* Maximum usage limit
* Duplicate redemption prevention
* Customer voucher redemption
* Customer redemption history
* Admin redemption history

### Gift Card Management

* Admin gift card creation
* View all gift cards
* Gift card balance management
* Gift card deactivation
* Gift card expiry validation
* Customer available gift card viewing
* Customer gift card redemption
* Multiple gift card redemptions
* Insufficient balance validation
* Gift card code duplicate validation
* Automatic balance reduction after redemption

### Database & Validation

* MySQL database persistence
* Spring Data JPA
* Hibernate ORM
* Bean validation
* Global exception handling
* Unique voucher and gift card codes
* Expiry date validation
* Positive amount validation

### API Documentation

* Swagger / OpenAPI documentation
* RESTful API endpoints
* JWT protected API operations

## Technology Stack

### Backend

* Java 17
* Spring Boot 3.5.5
* Spring Security
* Spring Data JPA
* Hibernate
* MySQL
* JWT
* Maven
* Jakarta Bean Validation

### Frontend

* HTML5
* CSS3
* JavaScript
* Fetch API

### Documentation

* Swagger / OpenAPI

## Project Structure

```text
secure-voucher-system/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/voucher/
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── VoucherController.java
│   │   │       │   ├── RedemptionController.java
│   │   │       │   └── GiftCardController.java
│   │   │       │
│   │   │       ├── dto/
│   │   │       │   ├── GiftCardRequest.java
│   │   │       │   ├── GiftCardResponse.java
│   │   │       │   └── GiftCardRedeemRequest.java
│   │   │       │
│   │   │       ├── entity/
│   │   │       │   ├── User.java
│   │   │       │   ├── Voucher.java
│   │   │       │   ├── Redemption.java
│   │   │       │   └── GiftCard.java
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── VoucherRepository.java
│   │   │       │   ├── RedemptionRepository.java
│   │   │       │   └── GiftCardRepository.java
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── VoucherService.java
│   │   │       │   ├── RedemptionService.java
│   │   │       │   └── GiftCardService.java
│   │   │       │
│   │   │       └── security/
│   │   │           ├── SecurityConfig.java
│   │   │           ├── JwtAuthenticationFilter.java
│   │   │           ├── JwtUtil.java
│   │   │           └── CustomUserDetailsService.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── index.html
│
├── pom.xml
└── README.md
```

## Main API Flow

### Authentication

```text
POST /api/auth/register
POST /api/auth/login
```

### Voucher - Admin

```text
POST   /api/vouchers
GET    /api/vouchers
PUT    /api/vouchers/{id}
DELETE /api/vouchers/{id}
GET    /api/redemptions
```

### Voucher - Customer

```text
GET  /api/vouchers/available
POST /api/redemptions
GET  /api/redemptions/my-history
```

### Gift Card - Admin

```text
POST   /api/gift-cards
GET    /api/gift-cards
DELETE /api/gift-cards/{id}
```

### Gift Card - Admin / Customer

```text
GET /api/gift-cards/available
```

### Gift Card - Customer

```text
POST /api/gift-cards/redeem
```

## Gift Card Redemption Flow

A gift card contains a monetary balance.

For example:

```text
Gift Card Amount = ₹1000
Initial Balance  = ₹1000

Redeem ₹300
Remaining Balance = ₹700

Redeem ₹200
Remaining Balance = ₹500
```

The customer can continue redeeming the gift card until the balance reaches zero.

If the requested redemption amount is greater than the available balance, the system rejects the transaction.

## Voucher Redemption Flow

A voucher is a discount/usage-based code.

Example:

```text
Voucher Code: SAVE200
Maximum Usage: 5
```

The system checks:

1. Voucher exists
2. Voucher is active
3. Voucher is not expired
4. Maximum usage is not exceeded
5. Customer has not already redeemed the voucher

If the same customer tries to redeem the same voucher again, the system rejects the request.

## Security

The application uses:

* JWT authentication
* Spring Security
* Role-based authorization
* BCrypt password hashing
* Protected REST APIs
* Voucher expiry validation
* Gift card expiry validation
* Maximum voucher usage validation
* Duplicate voucher redemption prevention
* Gift card balance validation

### Role Access

| Operation                   | ADMIN | CUSTOMER |
| --------------------------- | ----: | -------: |
| Create Voucher              |     ✅ |        ❌ |
| Update Voucher              |     ✅ |        ❌ |
| Deactivate Voucher          |     ✅ |        ❌ |
| View All Vouchers           |     ✅ |        ❌ |
| View Available Vouchers     |     ✅ |        ✅ |
| Redeem Voucher              |     ❌ |        ✅ |
| View My Redemption History  |     ❌ |        ✅ |
| View All Redemption History |     ✅ |        ❌ |
| Create Gift Card            |     ✅ |        ❌ |
| View All Gift Cards         |     ✅ |        ❌ |
| Deactivate Gift Card        |     ✅ |        ❌ |
| View Available Gift Cards   |     ✅ |        ✅ |
| Redeem Gift Card            |     ❌ |        ✅ |

## Validation & Error Handling

The application validates invalid requests and returns appropriate HTTP responses.

Examples:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
```

Examples of validation:

* Empty voucher code
* Empty gift card code
* Negative gift card amount
* Invalid expiry date
* Duplicate voucher code
* Duplicate gift card code
* Expired voucher
* Expired gift card
* Insufficient gift card balance
* Duplicate voucher redemption
* Unauthorized role access

## Database

The application uses **MySQL** for persistent data storage.

Main entities include:

```text
User
Voucher
Redemption
GiftCard
```

### Gift Card Data

A gift card stores:

```text
ID
Code
Amount
Balance
Expiry Date
Active Status
Created By
```

## Running Locally

### Requirements

Install the following:

* Java 17 or later
* Maven
* MySQL 8
* Git

### Clone and Build

```bash
git clone <repository-url>
cd secure-voucher-system
mvn clean test
mvn clean package -DskipTests
```

### Local Environment

Spring Boot reads database and JWT settings from environment variables. A root `.env` file is not loaded automatically. In PowerShell:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/voucher_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "<your-local-mysql-password>"
$env:JWT_SECRET = "<long-random-secret>"
$env:FRONTEND_URL = "http://localhost:5173"
mvn spring-boot:run
```

The backend runs at `http://localhost:8080` locally unless `PORT` is set. Health is available at `http://localhost:8080/health` and Swagger at `http://localhost:8080/swagger-ui/index.html`.

### Start React Frontend Locally

```powershell
cd frontend
npm install
npm run dev
```

The Vite frontend uses `VITE_API_URL`. For local development, use `http://localhost:8080/api` or leave it unset to use the Vite `/api` proxy.

## Application Workflow

```text
                 ┌──────────────────┐
                 │      Login       │
                 └────────┬─────────┘
                          │
             ┌────────────┴────────────┐
             │                         │
          ADMIN                    CUSTOMER
             │                         │
     ┌───────┴────────┐       ┌────────┴────────┐
     │                │       │                 │
  Vouchers        Gift Cards  Vouchers      Gift Cards
     │                │       │                 │
 Create/Update     Create     View/Redeem     View/Redeem
 Deactivate        Manage     History         Balance
     │                │       │                 │
     └────────┬───────┘       └────────┬────────┘
              │                        │
              └──────────┬─────────────┘
                         │
                    MySQL Database
```

## Render and Railway Deployment

Deployment architecture:

```text
React + Vite (Render) -> HTTPS REST API -> Spring Boot (Render) -> MySQL (Railway)
```

### Railway MySQL

1. Create a MySQL service in Railway.
2. Copy its host, port, database name, username, and password into a JDBC URL.
3. In the Render backend environment, map the values to:

```text
DB_URL=jdbc:mysql://<railway-host>:<railway-port>/<railway-database>?useSSL=true&serverTimezone=UTC
DB_USERNAME=<railway-username>
DB_PASSWORD=<railway-password>
JWT_SECRET=<long-random-secret>
FRONTEND_URL=https://<frontend-name>.onrender.com
```

If Railway provides a `MYSQL_URL` or another connection variable, copy its host, port, and database values into the `DB_URL` JDBC format above. Do not place the Railway URL or credentials in Git. The application keeps `spring.jpa.hibernate.ddl-auto=update` so Hibernate can create or update the five application tables without destructive SQL.

### Render Backend

Create a Render Web Service using the repository root:

```text
Build Command: mvn clean package -DskipTests
Start Command: java -jar target/secure-voucher-system-0.0.1-SNAPSHOT.jar
Health Check Path: /health
```

Render supplies `PORT`; Spring Boot uses `server.port=${PORT:8080}`. Add `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, and `FRONTEND_URL` in the Render environment settings. Do not add them to workflow files or committed configuration.

### Render Frontend

Create a separate Render Static Site using the `frontend` directory:

```text
Build Command: npm install && npm run build
Publish Directory: dist
```

Set this public environment variable in Render:

```text
VITE_API_URL=https://<backend-name>.onrender.com/api
```

Only the public API URL belongs in the frontend environment. Never expose `JWT_SECRET`, database credentials, or other backend secrets to Vite.

After the frontend deploys, update the backend `FRONTEND_URL` to the actual frontend Render URL and redeploy the backend. Test `/health`, customer registration/login, admin login, voucher operations, gift-card operations, and both gift-card history endpoints.

## Security Best Practices

Production secrets must remain in Render/Railway environment settings or a secret manager. The tracked `.env.example` files contain placeholders only. The React build receives only `VITE_API_URL`.

## Future Enhancements

Possible future improvements include:

* Gift card transaction history
* Customer gift card history
* Email notifications
* QR code based redemption
* Payment gateway integration
* Admin analytics dashboard
* Transaction reports
* PDF report generation
* Cloud deployment
* Docker support

## License

This project is intended for educational and academic purposes.
