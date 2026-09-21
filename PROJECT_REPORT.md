# Gift Card and Voucher Management System
## Engineering College Final-Year Project Report

**Project Title:** Gift Card and Voucher Management System  
**Project Type:** Full-stack web application  
**Academic Year:** ____________________  
**Submitted by:** ____________________  
**Register Number:** ____________________  
**Department:** ____________________  
**Institution:** ____________________  
**Guide:** ____________________

> **Implementation note:** This report is based on the source code in the supplied `secure-voucher-system` project. Features described as implemented are limited to the controllers, services, entities, security configuration, frontend, and test files present in that project.

---

## 1. Title Page

**GIFT CARD AND VOUCHER MANAGEMENT SYSTEM**

A project report submitted in partial fulfillment of the requirements for the award of the degree of

**BACHELOR OF ENGINEERING / BACHELOR OF TECHNOLOGY**

in

**________________________________________**

Submitted by

**________________________________________**  
**Register Number: ________________________**

Under the guidance of

**________________________________________**

**Department of __________________________**  
**________________________________ College**  
**Academic Year: __________________________**

---

## 2. Bonafide Certificate

This is to certify that the project report entitled **"Gift Card and Voucher Management System"** is a bonafide record of the project work carried out by **____________________________**, Register Number **________________**, of the Department of **____________________________**, **____________________________ College**, during the academic year **________________**.

The work was completed under my supervision and guidance in partial fulfillment of the requirements for the award of the degree of **____________________________**.

**Project Guide:** ____________________  
Signature: ____________________  Date: __________

**Head of the Department:** ____________________  
Signature: ____________________  Date: __________

**Internal Examiner:** ____________________  
Signature: ____________________  Date: __________

**External Examiner:** ____________________  
Signature: ____________________  Date: __________

---

## 3. Declaration

I/We hereby declare that the project report entitled **"Gift Card and Voucher Management System"** is the original work carried out by me/us under the guidance of **____________________________**. This report has not been submitted, in whole or in part, to any other university or institution for the award of any degree, diploma, or certificate.

I/We further declare that the implementation details presented in this report are based on the project source code and available project documentation.

**Name:** ____________________  
**Register Number:** ____________________  
**Signature:** ____________________  
**Date:** ____________________  
**Place:** ____________________

---

## 4. Acknowledgement

I/We express sincere gratitude to the management of **____________________________ College**, the Head of the Department of **____________________________**, and the project guide **____________________________** for their guidance and support throughout this project.

I/We thank the teaching and non-teaching staff for providing the facilities and technical environment required for development and testing. I/We also thank our friends and family for their encouragement and support.

Finally, I/We acknowledge the open-source Java, Spring Boot, MySQL, and frontend technologies used to implement the system.

---

## 5. Abstract

The Gift Card and Voucher Management System is a full-stack web application for managing digital vouchers and monetary gift cards. The application provides separate capabilities for administrators and customers. Administrators can create, view, update, and deactivate vouchers, create and deactivate gift cards, and view voucher redemption history. Customers can register, log in, view available vouchers and gift cards, redeem vouchers, redeem gift-card balances, and view their voucher redemption history.

The backend is implemented using Java 17, Spring Boot, Spring Security, Spring Data JPA, Hibernate, JWT, Maven, REST APIs, and Jakarta Bean Validation. MySQL is used for persistence. The repository retains the original static frontend for compatibility and now includes a Vite + React frontend using Axios under `frontend/`.

Security is implemented using BCrypt password hashing, stateless JWT authentication, and role-based authorization for the `ADMIN` and `CUSTOMER` roles. Business rules include voucher expiry validation, voucher maximum usage validation, duplicate voucher redemption prevention, gift-card expiry validation, duplicate code validation, gift-card balance reduction, and insufficient-balance rejection. Global exception handling returns structured error responses for validation, bad-request, and conflict conditions.

The project demonstrates the design of a secure REST-based application with persistence, role separation, validation, and a browser-based user interface. Voucher and gift-card redemptions are recorded in dedicated tables.

**Keywords:** gift card, voucher, redemption, Spring Boot, JWT, Spring Security, MySQL, REST API, role-based authorization.

---

## 6. Table of Contents

1. Title Page  
2. Bonafide Certificate  
3. Declaration  
4. Acknowledgement  
5. Abstract  
6. Table of Contents  
7. List of Figures  
8. List of Tables  
9. Introduction  
10. Problem Statement  
11. Existing System  
12. Limitations of Existing System  
13. Proposed System  
14. Objectives  
15. Scope of the Project  
16. System Requirements  
17. Technology Description  
18. System Architecture  
19. System Modules  
20. Database Design  
21. ER Diagram Description  
22. Database Tables  
23. API Design  
24. Authentication and Authorization Flow  
25. Voucher Redemption Workflow  
26. Gift Card Redemption Workflow  
27. Implementation Details  
28. Validation and Exception Handling  
29. Security Implementation  
30. Testing  
31. Test Cases  
32. Results  
33. Screenshots Section  
34. Advantages  
35. Limitations  
36. Future Enhancements  
37. Conclusion  
38. References  
39. Complete Project Summary  
40. Key Achievements  
41. Viva Questions and Answers

---

## 7. List of Figures

| Figure No. | Figure Title |
|---|---|
| Figure 1 | High-level system architecture |
| Figure 2 | Use-case diagram description |
| Figure 3 | Authentication and authorization flow |
| Figure 4 | Voucher redemption flow |
| Figure 5 | Gift-card redemption flow |
| Figure 6 | Entity relationship diagram description |
| Figure 7 | Login page screenshot placeholder |
| Figure 8 | Admin dashboard screenshot placeholder |
| Figure 9 | Customer dashboard screenshot placeholder |
| Figure 10 | Swagger/OpenAPI screenshot placeholder |

---

## 8. List of Tables

| Table No. | Table Title |
|---|---|
| Table 1 | Hardware requirements |
| Table 2 | Software requirements |
| Table 3 | User role permissions |
| Table 4 | `users` table |
| Table 5 | `vouchers` table |
| Table 6 | `gift_cards` table |
| Table 7 | `redemptions` table |
| Table 8 | Authentication API |
| Table 9 | Voucher API |
| Table 10 | Redemption API |
| Table 11 | Gift-card API |
| Table 12 | Test cases |

---

# 9. Introduction

Digital vouchers and gift cards are commonly used for promotional offers, customer rewards, employee benefits, and electronic payments. Managing these assets manually can result in duplicate codes, expired offers being accepted, inaccurate usage counts, weak access control, and difficulty tracking redemptions.

The Gift Card and Voucher Management System addresses these issues through a web-based application. It provides a REST backend connected to a MySQL database and a browser-based frontend. The system has two roles:

- **ADMIN:** manages vouchers and gift cards and views voucher redemption records.
- **CUSTOMER:** registers or logs in, views available offers, redeems vouchers and gift-card balances, and views personal voucher redemption history.

A voucher in this project is a usage-limited discount code. A gift card is a monetary-value code with an initial amount and a decreasing balance. Both instruments have an active status and an expiry date.

The project is organized using controller, service, repository, entity, DTO, security, and exception packages. This separation supports maintainability and keeps HTTP handling, business rules, database access, and security concerns distinct.

## 9.1 Project Implementation Boundary

The supplied source code confirms the following:

- The backend is implemented with Spring Boot and Java 17.
- The frontend is implemented as a static HTML document with embedded CSS and JavaScript.
- The backend exposes REST endpoints and OpenAPI/Swagger dependencies are included.
- React is not present in the supplied project files. React is therefore treated as a planned migration option, not an implemented technology.
- Voucher redemptions are persisted in `redemptions`.
- Gift-card redemption changes `gift_cards.balance`; no gift-card redemption entity or history endpoint is present.

---

# 10. Problem Statement

Organizations that issue vouchers and gift cards need a reliable way to create, distribute, validate, and redeem them. A basic manual or loosely controlled system may allow:

- unauthorized users to create or deactivate offers;
- the same voucher to be redeemed repeatedly by one customer;
- vouchers to be used after expiry or after reaching their usage limit;
- gift cards to be redeemed for more than their remaining balance;
- duplicate voucher or gift-card codes;
- passwords to be stored or transmitted insecurely;
- insufficient visibility of redemption records.

The problem is to design and implement a secure application that manages these resources, applies the required business rules, stores data persistently, and provides role-specific operations through a web interface and REST APIs.

---

# 11. Existing System

In a conventional manual or spreadsheet-based process, an administrator may maintain voucher codes, gift-card amounts, expiry dates, and customer redemptions in separate files. Customers may contact staff or use an unprotected page to redeem an offer.

Such a process depends heavily on manual verification. The system may not provide a single source of truth for current usage, available balance, account role, or redemption status. It may also lack an auditable service layer and consistent validation.

---

# 12. Limitations of Existing System

Typical limitations are:

1. Manual data entry can introduce errors.
2. Duplicate code detection may be inconsistent.
3. Expired vouchers or cards may be accepted accidentally.
4. Usage limits and balances require manual calculation.
5. Customer and administrator permissions may not be separated.
6. Passwords and sessions may not be protected adequately.
7. Redemption history may be incomplete or difficult to search.
8. Data may not be available through a consistent API.
9. There may be no structured error response for invalid requests.

---

# 13. Proposed System

The proposed system provides a centralized web application with:

- customer registration and login;
- administrator login and seeded administrator support;
- BCrypt password hashing;
- JWT-based stateless authentication;
- `ADMIN` and `CUSTOMER` role authorization;
- administrator voucher and gift-card management;
- available-offer filtering by active status, expiry, usage, and balance;
- voucher redemption with duplicate prevention and maximum usage enforcement;
- gift-card redemption with expiry and balance checks;
- MySQL persistence through JPA/Hibernate;
- Bean Validation on request DTOs;
- structured global exception responses;
- static browser frontend using HTML, CSS, and JavaScript;
- Swagger/OpenAPI support through the project dependency.

The proposed system reduces manual work and centralizes the rules that determine whether a voucher or gift card can be used.

---

# 14. Objectives

The objectives of the project are:

1. To build a full-stack application for digital voucher and gift-card management.
2. To provide secure customer registration and login.
3. To provide role-based separation between administrative and customer operations.
4. To allow administrators to create, view, update, and deactivate vouchers.
5. To allow administrators to create, view, and deactivate gift cards.
6. To validate voucher expiry and maximum usage.
7. To prevent duplicate voucher redemption by the same customer.
8. To validate gift-card expiry and remaining balance.
9. To prevent duplicate voucher and gift-card codes.
10. To persist system data in MySQL.
11. To expose the functionality through REST APIs.
12. To provide a simple browser interface for the main workflows.
13. To demonstrate testing of a representative business rule.

---

# 15. Scope of the Project

## 15.1 Implemented Scope

The implemented scope includes two roles, authentication, voucher management, gift-card management, voucher redemption, gift-card balance redemption, redemption histories, persistence, validation, security, exception handling, and both static and React frontends.

## 15.2 Out of Scope or Not Confirmed

The following are not confirmed as implemented in the supplied source code:

- payment-gateway integration;
- email or SMS notifications;
- multi-tenant organization management;
- refresh tokens;
- password reset and email verification;
- audit logs for every administrative change;
- automated frontend end-to-end tests;
- a React frontend;
- deployment to a production cloud environment.

These items can be considered future enhancements rather than current features.

---

# 16. System Requirements

## 16.1 Hardware Requirements

**Table 1: Hardware requirements**

| Component | Minimum Requirement | Recommended Requirement |
|---|---|---|
| Processor | Dual-core processor | Quad-core processor |
| RAM | 4 GB | 8 GB or more |
| Storage | 5 GB free space | 10 GB or more |
| Display | 1366 x 768 resolution | Full HD resolution |
| Network | Localhost or LAN connectivity | Stable broadband connection |

## 16.2 Software Requirements

**Table 2: Software requirements**

| Software | Requirement |
|---|---|
| Operating system | Windows, Linux, or macOS capable of running Java and MySQL |
| Java | Java Development Kit 17 |
| Backend framework | Spring Boot 3.5.5 as declared in `pom.xml` |
| Build tool | Apache Maven |
| Database | MySQL, database name configured as `voucher_db` |
| Browser | Modern browser with JavaScript and Fetch API support |
| API documentation | Springdoc OpenAPI / Swagger UI dependency |
| Frontend server | Static server such as a local development server on port 5500, matching the configured CORS origins |
| Source control | Git, if used by the development team |

---

# 17. Technology Description

## 17.1 Java 17

Java 17 is the language and runtime baseline configured in the Maven project. The code uses Java classes, records for request and response DTOs, enumerations, the Java time API, and object-oriented service design.

## 17.2 Spring Boot

Spring Boot provides application startup, dependency injection, web MVC, configuration, and integration with the Spring ecosystem. The main application is under `com.example.voucher` and uses Spring Boot starters for web, data access, security, and validation.

## 17.3 Spring Security

Spring Security protects REST endpoints and integrates authentication with the user repository. Method security is enabled so that controller methods can use expressions such as `hasRole('ADMIN')` and `hasRole('CUSTOMER')`.

## 17.4 JWT

JSON Web Tokens are generated after successful registration or login. The token contains the email as the subject and the role as a claim. A request carrying a valid `Authorization: Bearer <token>` header is processed by the JWT filter before the standard username-password filter.

## 17.5 JPA and Hibernate

Spring Data JPA repositories provide persistence operations for users, vouchers, gift cards, and redemptions. Hibernate maps the Java entity classes to database tables and relationships.

## 17.6 MySQL

MySQL is the configured relational database. The application connects to the `voucher_db` database. Hibernate is configured with `ddl-auto=update`, which allows the schema to be updated from entity mappings during development.

## 17.7 HTML, CSS, and JavaScript

The supplied frontend is a static `index.html` page with embedded styles and JavaScript. It includes login, role-specific dashboards, forms, tables, available voucher and gift-card views, redemption actions, and Fetch API calls to the backend.

## 17.8 React Migration Option

React is not present in the supplied project files and is not claimed as implemented. If required by the project specification, the static frontend may later be migrated to React while preserving the existing REST API contract.

## 17.9 Maven

Maven manages the project dependencies and build lifecycle. The project includes Spring Boot, MySQL Connector/J, JJWT, Springdoc OpenAPI, Lombok, and test dependencies.

---

# 18. System Architecture

The system follows a layered client-server architecture.

```text
+-----------------------------+
| Browser Frontend             |
| HTML + CSS + JavaScript     |
| Fetch API                   |
+-------------+---------------+
              | HTTP/JSON
              | Authorization: Bearer JWT
+-------------v---------------+
| Spring Boot REST Layer      |
| Auth, Voucher, Gift Card,   |
| Redemption Controllers      |
+-------------+---------------+
              |
+-------------v---------------+
| Service Layer               |
| AuthService                 |
| VoucherService              |
| GiftCardService             |
+-------------+---------------+
              |
+-------------v---------------+
| Security Layer              |
| SecurityConfig              |
| JwtAuthenticationFilter    |
| JwtUtil + BCrypt            |
+-------------+---------------+
              |
+-------------v---------------+
| Repository Layer            |
| Spring Data JPA             |
+-------------+---------------+
              |
+-------------v---------------+
| MySQL Database              |
| users, vouchers,            |
| gift_cards, redemptions     |
+-----------------------------+
```

**Figure 1: High-level system architecture.** The browser communicates with the Spring Boot API. Controllers receive requests, services apply business rules, repositories access MySQL, and the security filter establishes the authenticated user before protected controller methods execute.

## 18.1 Use-Case Diagram Description

Actors:

- **Administrator:** logs in, creates/updates/deactivates vouchers, views vouchers, creates/deactivates gift cards, views all gift cards, and views all voucher redemptions.
- **Customer:** registers, logs in, views available vouchers and gift cards, redeems vouchers, redeems gift-card balances, and views personal voucher redemption history.

Use cases:

```text
ADMIN    --> Login
ADMIN    --> Create Voucher
ADMIN    --> View All Vouchers
ADMIN    --> Update Voucher
ADMIN    --> Deactivate Voucher
ADMIN    --> Create Gift Card
ADMIN    --> View All Gift Cards
ADMIN    --> Deactivate Gift Card
ADMIN    --> View All Voucher Redemptions

CUSTOMER --> Register
CUSTOMER --> Login
CUSTOMER --> View Available Vouchers
CUSTOMER --> Redeem Voucher
CUSTOMER --> View My Voucher History
CUSTOMER --> View Available Gift Cards
CUSTOMER --> Redeem Gift Card Balance
```

---

# 19. System Modules

## 19.1 Authentication Module

The authentication module exposes registration and login endpoints. Registration accepts a name, email, and password. New registrations are assigned the `CUSTOMER` role. The password is encoded using BCrypt before persistence. Login delegates credential verification to Spring Security's authentication manager and returns a JWT plus the user's role.

The supplied `DataInitializer` also creates a default administrator and a default customer if accounts with the configured email addresses do not already exist. Credentials should be changed or externalized before deployment.

## 19.2 Admin Module

The admin module is implemented through role-protected controller methods. An administrator can create and maintain vouchers, create and deactivate gift cards, view all gift cards, and view all voucher redemption records. The creator of a voucher or gift card is associated with the authenticated administrator in the entity model.

## 19.3 Customer Module

The customer module provides access to available vouchers and gift cards, voucher redemption, gift-card balance redemption, and personal voucher history. Customer-only actions are protected with `hasRole('CUSTOMER')`.

## 19.4 Voucher Module

A voucher contains a unique code, description, discount, expiry date, maximum usage, current usage, active status, and creator. The administrator can create, view, update, and deactivate vouchers. Available vouchers are filtered by active status, future expiry date, and remaining usage capacity.

## 19.5 Gift Card Module

A gift card contains a unique code, original amount, current balance, expiry date, active status, and creator. The initial balance is set equal to the requested amount. Customers can redeem a positive amount while the card remains active, unexpired, and sufficiently funded. Multiple redemptions are supported because each successful redemption decreases the current balance.

## 19.6 Redemption Module

The redemption module handles voucher and gift-card redemption history. A voucher redemption creates a `Redemption` record with user, voucher, timestamp, and status. A unique database constraint on user and voucher supports duplicate redemption prevention. Gift-card redemption updates the balance and creates a `GiftCardRedemption` record containing the card, customer, amount, and timestamp.

---

# 20. Database Design

The application uses a relational database mapped through JPA entities. The principal entities are:

- `User`: identity and role information.
- `Voucher`: usage-based discount offer.
- `GiftCard`: monetary stored-value offer.
- `Redemption`: successful voucher redemption record.
- `Role`: Java enum containing `ADMIN` and `CUSTOMER`.

Entity relationships:

- One user can create many vouchers.
- One user can create many gift cards.
- One user can have many voucher redemptions.
- One voucher can have many redemption records, subject to the user-voucher uniqueness rule.
- A redemption belongs to one user and one voucher.

---

# 21. ER Diagram Description

The following structured representation describes the ER diagram.

```text
USER
----
PK id
name
UK email
password
role
   | 1
   | creates
   | N
VOUCHER
-------
PK id
UK code
description
discount
expiry_date
max_usage
current_usage
active
FK created_by -> USER.id
   | 1                         | 1
   | used in                    | redeemed by
   | N                         | N
REDEMPTION
----------
PK id
FK user_id -> USER.id
FK voucher_id -> VOUCHER.id
redeemed_at
status

USER
  | 1
  | creates
  | N
GIFT_CARD
---------
PK id
UK code
amount
balance
expiry_date
active
FK created_by -> USER.id
```

The `redemptions` table has a unique constraint on `(user_id, voucher_id)`. This prevents the same customer from receiving more than one persisted voucher redemption for the same voucher. There is no `gift_card_redemptions` entity in the supplied source code.

---

# 22. Database Tables

## 22.1 `users`

**Table 4: `users` table**

| Column | Type/Mapping | Constraints | Purpose |
|---|---|---|---|
| `id` | `Long`, identity | Primary key | User identifier |
| `name` | `String` | Not null | Display name |
| `email` | `String` | Not null, unique | Login identity |
| `password` | `String` | Not null | BCrypt encoded password |
| `role` | `Role` as string | Not null | `ADMIN` or `CUSTOMER` |

## 22.2 `vouchers`

**Table 5: `vouchers` table**

| Column | Type/Mapping | Constraints | Purpose |
|---|---|---|---|
| `id` | `Long`, identity | Primary key | Voucher identifier |
| `code` | `String` | Not null, unique, max length 50 | Voucher code |
| `description` | `String` | Not null | Offer description |
| `discount` | `Double` | Not null; request must be positive | Discount value |
| `expiry_date` | `LocalDate` | Not null; request must be future | Expiry date |
| `max_usage` | `Integer` | Not null; request must be positive | Maximum redemptions |
| `current_usage` | `Integer` | Not null; initialized to 0 | Redemptions used |
| `active` | `boolean` | Not null; initialized to true | Availability flag |
| `created_by` | Many-to-one `User` | Not null | Creating administrator |

## 22.3 `gift_cards`

**Table 6: `gift_cards` table**

| Column | Type/Mapping | Constraints | Purpose |
|---|---|---|---|
| `id` | `Long`, identity | Primary key | Gift-card identifier |
| `code` | `String` | Not null, unique | Gift-card code |
| `amount` | `Double` | Not null; request must be positive | Original value |
| `balance` | `Double` | Not null | Remaining value |
| `expiry_date` | `LocalDate` | Not null; request must be future | Expiry date |
| `active` | `boolean` | Not null; initialized to true | Availability flag |
| `created_by` | Many-to-one `User` | Nullable in entity mapping | Creating administrator |

## 22.4 `redemptions`

**Table 7: `redemptions` table**

| Column | Type/Mapping | Constraints | Purpose |
|---|---|---|---|
| `id` | `Long`, identity | Primary key | Redemption identifier |
| `user_id` | Many-to-one `User` | Not null | Redeeming customer |
| `voucher_id` | Many-to-one `Voucher` | Not null | Redeemed voucher |
| `redeemed_at` | `LocalDateTime` | Not null | Redemption timestamp |
| `status` | `String` | Not null | Current implementation stores `SUCCESS` |
| `(user_id, voucher_id)` | Unique constraint | Unique | Duplicate voucher prevention |

---

# 23. API Design

The API uses JSON request and response bodies. Authentication endpoints are public. Business endpoints require a JWT and appropriate role.

## 23.1 Authentication API

**Table 8: Authentication API**

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register a customer and return JWT and role |
| `POST` | `/api/auth/login` | Public | Authenticate a user and return JWT and role |

Registration request fields: `name`, `email`, `password`. Login request fields: `email`, `password`. Authentication response fields: `token`, `role`.

## 23.2 Voucher API

**Table 9: Voucher API**

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| `POST` | `/api/vouchers` | ADMIN | Create a voucher |
| `GET` | `/api/vouchers` | ADMIN | View all vouchers |
| `GET` | `/api/vouchers/available` | ADMIN or CUSTOMER | View active, unexpired, unused-capacity vouchers |
| `PUT` | `/api/vouchers/{id}` | ADMIN | Update a voucher |
| `DELETE` | `/api/vouchers/{id}` | ADMIN | Deactivate a voucher |

Voucher request fields: `code`, `description`, `discount`, `expiryDate`, and `maxUsage`. Voucher response fields include the identifier, code, description, discount, expiry date, usage values, and active state.

## 23.3 Redemption API

**Table 10: Redemption API**

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| `POST` | `/api/redemptions` | CUSTOMER | Redeem a voucher by code |
| `GET` | `/api/redemptions/my-history` | CUSTOMER | View the authenticated customer's voucher history |
| `GET` | `/api/redemptions` | ADMIN | View all voucher redemption records |

Voucher redemption request field: `code`. Redemption response fields include redemption id, voucher code, discount, redemption time, status, and user email.

## 23.4 Gift-Card API

**Table 11: Gift-card API**

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| `POST` | `/api/gift-cards` | ADMIN | Create a gift card |
| `GET` | `/api/gift-cards` | ADMIN | View all gift cards |
| `GET` | `/api/gift-cards/available` | ADMIN or CUSTOMER | View active, unexpired cards with balance |
| `POST` | `/api/gift-cards/redeem` | CUSTOMER | Redeem an amount from a gift card |
| `DELETE` | `/api/gift-cards/{id}` | ADMIN | Deactivate a gift card |
| `GET` | `/api/gift-cards/my-history` | CUSTOMER | View personal gift-card redemption history |
| `GET` | `/api/gift-cards/redemptions` | ADMIN | View all gift-card redemption records |

Gift-card creation request fields: `code`, `amount`, and `expiryDate`. Gift-card redemption request fields: `code` and `amount`. A successful response returns the updated gift-card representation, including the remaining balance.

## 23.5 API Documentation

The Maven configuration includes Springdoc OpenAPI. The security configuration permits `/swagger-ui/**`, `/swagger-ui.html`, and `/v3/api-docs/**`, so the project is prepared to expose interactive API documentation when the application is running.

---

# 24. Authentication and Authorization Flow

```text
1. User submits email and password.
2. AuthController receives the validated request.
3. AuthService authenticates through AuthenticationManager for login.
4. BCrypt verifies the supplied password against the stored hash.
5. AuthService obtains the user's role.
6. JwtUtil creates a signed token with email subject, role claim, issued time, and expiry.
7. Client stores the token and sends it as a Bearer token on protected requests.
8. JwtAuthenticationFilter reads the Authorization header.
9. JwtUtil validates the signature and expiry.
10. The filter loads user details and places authentication in SecurityContext.
11. Spring Security and @PreAuthorize evaluate the requested role.
12. The controller delegates to the service only when authorization succeeds.
```

```text
Client -> /api/auth/login -> AuthController -> AuthService
AuthService -> AuthenticationManager -> BCrypt/UserRepository
AuthService -> JwtUtil -> JWT response
Client -> protected endpoint + Bearer JWT
JWT filter -> validate token -> load user -> SecurityContext
SecurityContext -> role check -> controller -> service
```

**Figure 3: Authentication and authorization flow.** The application uses stateless sessions; it does not use server-side HTTP sessions for authenticated state.

---

# 25. Voucher Redemption Workflow

```text
Customer submits voucher code
          |
          v
POST /api/redemptions
          |
          v
JWT customer authorization succeeds?
          | no -> unauthorized/forbidden response
          v
Find customer and voucher
          | not found -> bad request
          v
Check active status
          | inactive -> conflict response
          v
Check expiry date
          | expired -> conflict response
          v
Check current usage < maximum usage
          | limit reached -> conflict response
          v
Check user-voucher redemption does not exist
          | duplicate -> conflict response
          v
Create SUCCESS Redemption
Increment currentUsage
Save redemption and voucher
          |
          v
Return RedemptionResponse
```

The service method is transactional. The `Redemption` entity stores the customer, voucher, timestamp, and status. A repository uniqueness rule also supports duplicate prevention at the database level.

---

# 26. Gift Card Redemption Workflow

```text
Customer submits gift-card code and amount
          |
          v
POST /api/gift-cards/redeem
          |
          v
JWT customer authorization succeeds?
          | no -> unauthorized/forbidden response
          v
Find gift card by code
          | not found -> bad request
          v
Check active status
          | inactive -> conflict response
          v
Check expiry date
          | expired -> conflict response
          v
Check requested amount <= current balance
          | false -> conflict response
          v
Set balance = balance - requested amount
Save gift card
          |
          v
Return updated GiftCardResponse
```

Multiple redemptions are supported because the current balance is updated after each successful operation. The current source code does not associate the gift-card redemption request with a customer in a transaction history table.

---

# 27. Implementation Details

## 27.1 Package Organization

```text
com.example.voucher
|-- config       DataInitializer
|-- controller   AuthController, VoucherController,
|               RedemptionController, GiftCardController
|-- dto          Request and response records
|-- entity       User, Role, Voucher, GiftCard, Redemption, GiftCardRedemption
|-- exception    GlobalExceptionHandler
|-- repository   Spring Data JPA repositories
|-- security     SecurityConfig, JwtAuthenticationFilter,
|               JwtUtil, CustomUserDetailsService
|-- service      AuthService, VoucherService, GiftCardService
```

## 27.2 Authentication Implementation

`AuthService.register` checks whether an email already exists, encodes the password, creates a `CUSTOMER` user, saves it, and returns a JWT. `AuthService.login` delegates credential verification to Spring Security, loads the user, and generates a JWT containing the email and role.

## 27.3 Voucher Implementation

`VoucherService.create` checks code uniqueness, loads the authenticated administrator, initializes usage to zero, sets the voucher active, and saves it. `getAvailable` returns only active vouchers whose expiry date is after the current date and whose usage is below the maximum. `redeem` applies all redemption rules before incrementing usage and saving a `Redemption` entity.

## 27.4 Gift-Card Implementation

`GiftCardService.create` checks code uniqueness, initializes amount and balance to the requested amount, and associates the card with the administrator. `getAvailable` returns active cards with a future expiry date and positive balance. `redeem` verifies state, expiry, and balance, subtracts the requested amount, and records a `GiftCardRedemption` for the authenticated customer.

## 27.5 Frontend Implementation

The repository retains the supplied `src/index.html` for compatibility. The capstone frontend is now implemented under `frontend/` as a Vite + React application using Axios. It contains:

- login form;
- role badge and logout operation;
- administrator forms for voucher and gift-card creation;
- administrator tables for vouchers, gift cards, and voucher redemption history;
- customer views for available vouchers and gift cards;
- customer voucher redemption and personal voucher history;
- Axios calls to the REST endpoints with JWT bearer authentication;
- React authentication context and role-based dashboard rendering;
- responsive CSS for smaller screens.

---

# 28. Validation and Exception Handling

Request DTOs use Jakarta Bean Validation annotations:

- `@NotBlank` for required text values;
- `@Email` for email format;
- `@Positive` for discount, usage limit, amount, and redemption amount;
- `@Future` for voucher and gift-card expiry dates.

`@Valid` is used in controller methods so invalid requests are rejected before service processing. `GlobalExceptionHandler` maps:

- `MethodArgumentNotValidException` to HTTP 400 with field-level details;
- `IllegalArgumentException` to HTTP 400;
- `IllegalStateException` to HTTP 409.

Responses include a timestamp, numeric status, error label, message, and optional validation details.

Business validations include:

- duplicate user email prevention;
- duplicate voucher code prevention;
- duplicate gift-card code prevention;
- voucher active-state validation;
- voucher expiry validation;
- voucher maximum usage validation;
- duplicate voucher redemption validation;
- gift-card active-state validation;
- gift-card expiry validation;
- gift-card sufficient-balance validation.

---

# 29. Security Implementation

## 29.1 Password Security

The authentication provider uses `BCryptPasswordEncoder`. Registration stores the encoded password rather than the plain text password.

## 29.2 Token Security

`JwtUtil` signs tokens using an HMAC SHA-256 signing operation. Tokens contain an email subject, role claim, issued timestamp, and expiry timestamp. The configured expiration is 86,400,000 milliseconds, equivalent to 24 hours.

## 29.3 Endpoint Security

Authentication endpoints and OpenAPI resources are permitted without authentication. Protected operations use HTTP method matchers and method-level `@PreAuthorize` annotations. The application is configured as stateless and inserts the JWT filter before `UsernamePasswordAuthenticationFilter`.

## 29.4 CORS

CORS is configured for `http://127.0.0.1:5500` and `http://localhost:5500`, with GET, POST, PUT, DELETE, and OPTIONS methods. This supports the supplied local static frontend setup.

## 29.5 Security Deployment Note

The supplied `application.properties` contains a database password and JWT secret directly in the file. This is acceptable only as a local development arrangement. A production deployment should use environment variables or a secret manager, rotate any exposed development credentials, disable SQL logging where inappropriate, and avoid printing authentication details. The current JWT filter includes debug output of the authenticated user and authorities, which should be removed or replaced with controlled logging before production use.

---

# 30. Testing

The project contains a JUnit test class named `VoucherServiceTest`. The test uses Mockito mocks for the voucher, user, and redemption repositories. It verifies that attempting to create a voucher with an existing code throws `IllegalArgumentException`.

The Maven project also includes Spring Boot test and Spring Security test dependencies. Based on the supplied files, the explicitly visible test coverage is a focused service-level duplicate-code test. A complete integration, security, frontend, and database test suite is recommended as future work.

Testing levels relevant to this project are:

1. **Unit testing:** service rules using Mockito.
2. **Integration testing:** controller, security, repository, and MySQL interaction.
3. **API testing:** authentication, authorization, validation, and business responses.
4. **UI testing:** login, role dashboard, create, view, deactivate, and redemption flows.
5. **Regression testing:** expiry, duplicate, balance, and usage-limit scenarios.

---

# 31. Test Cases

**Table 12: Test cases**

| ID | Test Scenario | Expected Result | Status from Source Review |
|---|---|---|---|
| TC-01 | Register with a new email | Customer is created and JWT is returned | Implemented by code; automated test not supplied |
| TC-02 | Register with an existing email | HTTP 400 with duplicate email message | Implemented by code; automated test not supplied |
| TC-03 | Login with valid credentials | JWT and role are returned | Implemented by code; automated test not supplied |
| TC-04 | Create voucher with duplicate code | Request is rejected | Covered by `VoucherServiceTest` |
| TC-05 | Create voucher with past expiry date | Bean validation rejects request | Implemented by DTO validation; automated test not supplied |
| TC-06 | Customer views available vouchers | Only active, unexpired, under-limit vouchers are returned | Implemented by code |
| TC-07 | Redeem an active voucher | Usage increments and `SUCCESS` redemption is stored | Implemented by code |
| TC-08 | Redeem an expired voucher | HTTP 409 conflict response | Implemented by code |
| TC-09 | Redeem the same voucher twice as one customer | Second redemption is rejected | Implemented by code and database constraint |
| TC-10 | Reach voucher maximum usage | Further redemption is rejected | Implemented by code |
| TC-11 | Create a gift card | Amount and balance are initialized equally | Implemented by code |
| TC-12 | Redeem a gift card within balance | Balance decreases | Implemented by code |
| TC-13 | Redeem more than current gift-card balance | HTTP 409 conflict response | Implemented by code |
| TC-14 | Redeem an expired gift card | HTTP 409 conflict response | Implemented by code |
| TC-15 | Create duplicate gift-card code | Request is rejected | Implemented by code; automated test not supplied |
| TC-16 | Customer calls admin endpoint | Access is denied | Implemented by security configuration; automated test not supplied |
| TC-17 | Unauthenticated user calls protected endpoint | Authentication is required | Implemented by security configuration |
| TC-18 | Gift-card redemption history query | Customer and admin history endpoints return persisted records | Implemented by code; automated test not supplied |

---

# 32. Results

The implementation provides a working architectural foundation for a secure voucher and gift-card service. The source code demonstrates:

- role-separated REST operations;
- JWT authentication and BCrypt password encoding;
- persistent JPA entity mappings;
- unique code checks;
- voucher usage and duplicate-redemption rules;
- gift-card balance and expiry rules;
- request validation;
- structured exception responses;
- a browser frontend for primary workflows;
- a focused unit test for duplicate voucher creation.

The result is suitable as a capstone demonstration of layered Spring Boot development. The implementation should be evaluated with the recommended integration and end-to-end tests before being described as production-ready.

---

# 33. Screenshots Section

The following placeholders should be replaced with screenshots captured from the running application. They are intentionally marked because actual screenshots were not supplied with the source review.

### Figure 7: Login Page

`[Insert Login Page Screenshot Here]`

Suggested caption: **Login page showing email and password fields and login response area.**

### Figure 8: Admin Dashboard

`[Insert Admin Dashboard Screenshot Here]`

Suggested caption: **Administrator dashboard showing voucher, gift-card, and redemption management operations.**

### Figure 9: Create Voucher Screen

`[Insert Create Voucher Screenshot Here]`

Suggested caption: **Administrator form for creating a voucher with code, description, discount, expiry, and maximum usage.**

### Figure 10: Gift Card Screen

`[Insert Gift Card Screenshot Here]`

Suggested caption: **Gift-card management and customer balance-redemption view.**

### Figure 11: Customer Dashboard

`[Insert Customer Dashboard Screenshot Here]`

Suggested caption: **Customer dashboard showing available vouchers, available gift cards, redemption controls, and voucher history.**

### Figure 12: Swagger/OpenAPI Screen

`[Insert Swagger Screenshot Here]`

Suggested caption: **Swagger/OpenAPI documentation page for the REST endpoints.**

---

# 34. Advantages

1. Centralized management of vouchers and gift cards.
2. Clear separation of administrator and customer responsibilities.
3. Stateless JWT authentication suitable for REST APIs.
4. BCrypt password hashing improves password protection.
5. Duplicate voucher redemption is prevented per customer.
6. Voucher usage limits are enforced by the service layer.
7. Gift-card balance is automatically reduced after redemption.
8. Expired and inactive instruments are rejected.
9. MySQL persistence avoids reliance on in-memory data.
10. Bean Validation provides consistent request checks.
11. Global exception handling provides structured error responses.
12. The static frontend can be used without a separate frontend build system.
13. The REST API can support a future React frontend without changing the domain layer.

---

# 35. Limitations

1. The visible automated test suite contains only a focused duplicate-voucher unit test.
2. Gift-card history has been added, but the visible automated test suite does not yet cover it.
3. Refresh tokens, logout token revocation, password reset, and email verification are not implemented.
4. The original static frontend remains alongside the React frontend for backward compatibility.
5. Administrative and customer dashboards are separated by role in the React application.
6. Application secrets should be supplied through environment variables or a secret manager.
7. The JWT filter contains debug `System.out` statements.
8. `Double` is used for monetary values; a production financial system should consider `BigDecimal`.
9. `spring.jpa.hibernate.ddl-auto=update` is suitable for development but controlled migrations are preferable for production.
10. No evidence of payment gateway integration, notifications, audit logging, or cloud deployment is present.
11. Concurrency controls for simultaneous redemptions should be strengthened for high-volume use.

---

# 36. Future Enhancements

1. Add status and richer reporting fields to `GiftCardRedemption` if required by the business specification.
2. Expand customer-specific gift-card history and administrator reports.
3. Replace `Double` with `BigDecimal` for monetary calculations.
4. Add integration tests using a test database or Testcontainers.
5. Add Spring Security tests for every role-protected endpoint.
6. Add frontend end-to-end tests for login and redemption workflows.
7. Add refresh tokens, logout/revocation, password reset, and email verification.
8. Add audit logs for creation, update, deactivation, and redemption actions.
11. Add search, pagination, filtering, and export for administrative screens.
12. Add notification services for voucher expiry and gift-card balance changes.
13. Use environment variables or a managed secret store for credentials and JWT keys.
14. Add database migration tools such as Flyway or Liquibase.
15. Add optimistic locking or appropriate transaction isolation for concurrent redemptions.
16. Containerize and deploy the application with production monitoring and centralized logging.
17. Add configurable currencies, localization, and organization-level administration.

---

# 37. Conclusion

The Gift Card and Voucher Management System demonstrates the implementation of a secure, role-based, REST-oriented web application. The system combines Java 17, Spring Boot, Spring Security, JWT, JPA/Hibernate, MySQL, Maven, Bean Validation, and a static HTML/CSS/JavaScript frontend to manage two related digital commerce resources.

The project meets its central objectives by allowing administrators to manage offers and customers to redeem them under explicit business rules. Voucher expiry, usage capacity, duplicate redemption, gift-card expiry, and insufficient balance are handled in the service layer. The use of JWT, BCrypt, method-level authorization, and persistent entities gives the application a sound foundation for further development.

The report also identifies boundaries that should be considered during evaluation. React is a future migration option rather than part of the current implementation, voucher history is implemented while gift-card transaction history is not, and the current automated test coverage should be expanded. With these enhancements, the project can evolve from a capstone demonstration into a more complete production-oriented platform.

---

# 38. References

1. Oracle. *Java Documentation*. https://docs.oracle.com/en/java/
2. Spring. *Spring Boot Reference Documentation*. https://docs.spring.io/spring-boot/
3. Spring. *Spring Security Reference*. https://docs.spring.io/spring-security/reference/
4. Spring. *Spring Data JPA Reference Documentation*. https://docs.spring.io/spring-data/jpa/reference/
5. Hibernate. *Hibernate ORM Documentation*. https://hibernate.org/orm/documentation/
6. MySQL. *MySQL 8.0 Reference Manual*. https://dev.mysql.com/doc/
7. OWASP. *JSON Web Token Cheat Sheet*. https://cheatsheetseries.owasp.org/
8. JJWT. *JSON Web Token for Java Documentation*. https://github.com/jwtk/jjwt
9. Springdoc. *Springdoc OpenAPI Documentation*. https://springdoc.org/
10. Project source files and README supplied with the `secure-voucher-system` application.

---

# 39. Complete Project Summary

The project is a Spring Boot and MySQL application that manages vouchers and gift cards through REST APIs and a browser frontend. The application supports `ADMIN` and `CUSTOMER` roles. Administrators create and manage vouchers and gift cards. Customers view available offers and redeem them according to expiry, usage, and balance rules.

The main implemented components are:

- authentication with registration, login, JWT, and BCrypt;
- role-based endpoint authorization;
- voucher CRUD-style administration and customer redemption;
- voucher redemption history for customers and administrators;
- gift-card creation, viewing, deactivation, and balance redemption;
- MySQL persistence using JPA/Hibernate;
- request validation and global exception handling;
- HTML/CSS/JavaScript frontend using Fetch API;
- Swagger/OpenAPI dependency for API documentation;
- focused service unit testing with Mockito.

---

# 40. Key Achievements

1. Designed a layered full-stack application.
2. Implemented separate administrator and customer roles.
3. Protected REST endpoints with JWT and Spring Security.
4. Encoded passwords with BCrypt.
5. Implemented voucher expiry, usage-limit, and duplicate-redemption rules.
6. Implemented gift-card expiry, duplicate-code, and insufficient-balance rules.
7. Connected the application to MySQL through Spring Data JPA and Hibernate.
8. Added request validation with Jakarta Bean Validation.
9. Added structured global exception handling.
10. Built a browser frontend for the primary operations.
11. Added a focused automated test for duplicate voucher code rejection.
12. Kept the report aligned with the actual source implementation, including identified limitations.

---

# 41. Viva Questions and Answers

## Q1. What is the purpose of the project?

**Answer:** The project manages digital vouchers and monetary gift cards. It allows administrators to create and control them and allows customers to view and redeem them securely.

## Q2. What are the two roles in the system?

**Answer:** The roles are `ADMIN` and `CUSTOMER`. Administrators manage vouchers and gift cards. Customers redeem available vouchers and gift-card balances and view their voucher history.

## Q3. Why is JWT used?

**Answer:** JWT provides stateless authentication for REST requests. After login, the client sends the token in the Authorization header, and the server validates it for each protected request.

## Q4. Where is the JWT processed?

**Answer:** `JwtAuthenticationFilter` reads the Bearer token, validates it through `JwtUtil`, loads the user details, and places the authentication in Spring Security's context.

## Q5. How are passwords protected?

**Answer:** Passwords are encoded with `BCryptPasswordEncoder` before storage. During login, Spring Security verifies the supplied password against the encoded password.

## Q6. How is role-based authorization implemented?

**Answer:** Method security is enabled and controller methods use `@PreAuthorize`, such as `hasRole('ADMIN')`, `hasRole('CUSTOMER')`, and `hasAnyRole('ADMIN','CUSTOMER')`.

## Q7. What is the difference between a voucher and a gift card in this project?

**Answer:** A voucher is a discount and usage-limited code. A gift card has an original monetary amount and a current balance that decreases after each redemption.

## Q8. How does the system prevent duplicate voucher redemption?

**Answer:** The service checks `existsByUserIdAndVoucherId` before creating a redemption. The `redemptions` table also declares a unique constraint on `user_id` and `voucher_id`.

## Q9. How is voucher expiry checked?

**Answer:** The service compares the voucher expiry date with the current date and rejects the request if the voucher is expired. Available-voucher queries also filter out expired vouchers.

## Q10. How is the voucher maximum usage enforced?

**Answer:** The service rejects redemption when `currentUsage` is greater than or equal to `maxUsage`. Successful redemption increments `currentUsage` by one.

## Q11. How does gift-card redemption work?

**Answer:** The system finds the card by code, verifies active status and expiry, checks that the requested amount is not greater than the current balance, subtracts the amount, and saves the updated card.

## Q12. Can a gift card be redeemed more than once?

**Answer:** Yes. Multiple redemptions are supported while the card is active, unexpired, and has sufficient balance.

## Q13. What happens when a customer requests more than the gift-card balance?

**Answer:** The service throws an `IllegalStateException`, which the global handler maps to an HTTP 409 conflict response.

## Q14. What database tables are represented by the entity classes?

**Answer:** The principal tables are `users`, `vouchers`, `gift_cards`, and `redemptions`. `Role` is an enum stored as text in the users table.

## Q15. Which entity records voucher redemption history?

**Answer:** The `Redemption` entity records the customer, voucher, timestamp, and status for voucher redemptions.

## Q16. Is gift-card redemption history implemented?

**Answer:** Yes. `GiftCardRedemption` persists the card, authenticated customer, amount, and timestamp. Customers can use `/api/gift-cards/my-history`, and administrators can use `/api/gift-cards/redemptions`.

## Q17. What validation annotations are used?

**Answer:** The DTOs use `@NotBlank`, `@Email`, `@Positive`, and `@Future`. Controllers use `@Valid` to activate validation.

## Q18. What does the global exception handler do?

**Answer:** It converts validation errors into HTTP 400 responses, illegal arguments into HTTP 400 responses, and illegal state conditions such as expired or exhausted resources into HTTP 409 responses.

## Q19. What frontend technology is implemented?

**Answer:** The repository retains the original static page, and now also includes a Vite + React frontend under `frontend/` using Axios and the existing REST API.

## Q20. Why might React be considered in the future?

**Answer:** React provides a component-based frontend structure and maintainable state and routing. The existing REST API remains the backend contract.

## Q21. What test is currently present?

**Answer:** `VoucherServiceTest` verifies that creating a voucher with an already existing code throws `IllegalArgumentException`.

## Q22. Why should `Double` be replaced for money in a production system?

**Answer:** Binary floating-point values can introduce rounding errors. `BigDecimal` is more appropriate for exact monetary calculations.

## Q23. Why should secrets be moved out of `application.properties`?

**Answer:** Database passwords and JWT secrets should not be committed as source code. Environment variables or a secret manager reduce the risk of accidental exposure and support safer deployment.

## Q24. What is the purpose of `@Transactional` on voucher redemption?

**Answer:** It groups the voucher usage update and redemption persistence in one transaction so the related database operations can be committed consistently.

## Q25. What are the main future enhancements?

**Answer:** Important enhancements include stronger concurrency control, comprehensive integration and UI testing, richer audit reporting, secret management, and production deployment support.
