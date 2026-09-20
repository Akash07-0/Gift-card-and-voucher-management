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

### Clone Repository

```bash
git clone https://github.com/Akash07-0/Gift-card-and-voucher-management.git
```

### Navigate to Project

```bash
cd Gift-card-and-voucher-management
```

### Configure Database

Update the database configuration in:

```text
src/main/resources/application.properties
```

Use your own MySQL username, password, database name, and JWT secret.

### Start Backend

```bash
mvn spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

### Start Frontend

Navigate to the frontend folder and start the frontend server according to the project setup.

The frontend is available at:

```text
http://127.0.0.1:5500
```

## Swagger Documentation

After starting the Spring Boot application, open:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger can be used to test and explore the REST APIs.

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

## Security Best Practices

Production secrets should never be committed to GitHub.

The following should be stored securely using environment variables or a secret-management system:

* Database passwords
* JWT secret keys
* Other sensitive configuration values

Example:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
```

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
