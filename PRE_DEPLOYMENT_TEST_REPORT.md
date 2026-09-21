# Pre-Deployment Test Report

## 1. Project Information

| Item | Result |
|---|---|
| Project name | Gift Card and Voucher Management System |
| Path | `D:\akash capstone\secure-voucher-system` |
| Audit date | 2026-09-20 |
| Java | Java 17; the last Maven run reported Java 17.0.20.1 |
| Spring Boot | 3.5.5 |
| Maven | Apache Maven; Maven executed successfully for the package and test runs |
| Node.js | Required for frontend build; exact version was not captured in the available terminal output |
| Frontend | React 19 + Vite 6 + Axios |
| Database | MySQL via MySQL Connector/J |
| Deployment target | Render frontend/backend, Railway MySQL |

### Project structure audit

Present and verified:

- `src/main/java/com/example/voucher/`: Spring Boot backend.
- `src/test/java/`: existing JUnit/Mockito test source.
- `frontend/`: React/Vite application.
- `docs/diagrams/`: DBML and diagrams.net source files.
- `.github/workflows/`: backend and frontend CI workflows.
- `README.md`, `.gitignore`, `LICENSE`, `.env.example`, `CHANGELOG.md`, `PROJECT_REPORT.md`, and `pom.xml`.
- `frontend/package.json` and `frontend/package-lock.json`.

The repository also retains the original static frontend and root-level prototype Java files under `src/`. They were intentionally not deleted during this audit.

## 2. Build Results

### Backend: `mvn clean test`

**PASS**

Evidence from Maven output:

- 38 Java source files compiled with Java release 17.
- Tests run: 1.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- Maven result: `BUILD SUCCESS`.

### Backend: `mvn clean package -DskipTests`

**PASS**

Evidence from Maven output:

- 38 Java source files compiled.
- Tests were intentionally skipped by the command.
- Spring Boot repackaged the application JAR.
- Generated artifact: `target/secure-voucher-system-0.0.1-SNAPSHOT.jar`.
- Maven result: `BUILD SUCCESS`.

### Frontend: `npm ci`

**PASS**

Evidence from terminal output:

- 90 packages installed from `package-lock.json`.
- npm reported an install-script approval warning for `esbuild`; this did not prevent the build.

### Frontend: `npm run build`

**PASS**

Evidence from Vite output:

- Vite 6.4.3.
- 91 modules transformed.
- `frontend/dist/` generated.
- Build completed successfully in 6.44 seconds.

## 3. Test Results

| Area | Status | Details |
|---|---|---|
| Backend build | PASS | `mvn clean package -DskipTests` compiled 38 sources and generated the JAR. |
| Backend tests | PASS | One existing `VoucherServiceTest` passed; no failures or errors. |
| Frontend build | PASS | `npm ci` and Vite production build passed; `dist/` generated. |
| Authentication | BLOCKED | Code supports registration/login, BCrypt, and JWT, but live API testing was blocked by MySQL authentication failure. |
| Authorization | PARTIAL | Controller annotations and security rules were inspected; live 401/403 verification was blocked by database startup failure. |
| Voucher workflow | BLOCKED | Service rules were inspected; live CRUD/redemption workflow requires a working database. |
| Gift-card workflow | BLOCKED | Service rules were inspected; live balance and redemption workflow requires a working database. |
| Gift-card history | BLOCKED | Entity, repository, service, and endpoints are wired; live persistence verification requires a working database. |
| Database | BLOCKED | Local MySQL rejected the supplied root credentials. No database modifications were attempted. |
| Security | PARTIAL | JWT, BCrypt, role authorization, CORS, and environment configuration were inspected; live authentication was blocked. |
| Swagger | PARTIAL | Springdoc dependency and OpenAPI configuration are present; live Swagger page verification was blocked by startup failure. |
| Health endpoint | PARTIAL | `/health` is implemented and permitted without authentication; live HTTP 200 verification was blocked by startup failure. |
| React frontend | PASS | Source audit and production Vite build passed. Browser interaction was not executed. |
| CI/CD | PASS | Backend workflow runs Maven verification; frontend workflow runs npm install/build. No deployment steps were added. |
| Render readiness | PASS | Render `PORT`, environment variables, build/start commands, CORS, and health path are configured/documented. |
| Railway readiness | PASS | JDBC URL, username, and password are environment-driven; no Railway credentials or resources were accessed. |

## 4. API Test Matrix

Live HTTP results are marked **BLOCKED** because Spring Boot could not initialize its JPA datasource against the local MySQL server. The expected status column is based on controller mappings and service behavior, not a claim of live execution.

| Method | Endpoint | Role | Expected status | Result | Purpose |
|---|---|---|---:|---|---|
| POST | `/api/auth/register` | Public | 200 / 400 | BLOCKED | Register a customer and return JWT/role. |
| POST | `/api/auth/login` | Public | 200 / 400 / 401 | BLOCKED | Authenticate a user and return JWT/role. |
| GET | `/health` | Public | 200 | BLOCKED | Return `{ "status": "UP" }`. |
| POST | `/api/vouchers` | ADMIN | 201 / 400 / 403 | BLOCKED | Create voucher. |
| GET | `/api/vouchers` | ADMIN | 200 / 403 | BLOCKED | List all vouchers. |
| GET | `/api/vouchers/available` | ADMIN, CUSTOMER | 200 / 401 / 403 | BLOCKED | List active, unexpired vouchers with capacity. |
| PUT | `/api/vouchers/{id}` | ADMIN | 200 / 400 / 403 | BLOCKED | Update voucher. |
| DELETE | `/api/vouchers/{id}` | ADMIN | 204 / 400 / 403 | BLOCKED | Deactivate voucher. |
| POST | `/api/redemptions` | CUSTOMER | 200 / 400 / 401 / 403 / 409 | BLOCKED | Redeem voucher. |
| GET | `/api/redemptions/my-history` | CUSTOMER | 200 / 401 / 403 | BLOCKED | Customer voucher history. |
| GET | `/api/redemptions` | ADMIN | 200 / 401 / 403 | BLOCKED | All voucher redemption history. |
| POST | `/api/gift-cards` | ADMIN | 201 / 400 / 403 | BLOCKED | Create gift card. |
| GET | `/api/gift-cards` | ADMIN | 200 / 401 / 403 | BLOCKED | List all gift cards. |
| GET | `/api/gift-cards/available` | ADMIN, CUSTOMER | 200 / 401 / 403 | BLOCKED | List active cards with balance. |
| POST | `/api/gift-cards/redeem` | CUSTOMER | 200 / 400 / 401 / 403 / 409 | BLOCKED | Redeem card amount and persist history. |
| GET | `/api/gift-cards/my-history` | CUSTOMER | 200 / 401 / 403 | BLOCKED | Customer gift-card history. |
| GET | `/api/gift-cards/redemptions` | ADMIN | 200 / 401 / 403 | BLOCKED | All gift-card history. |
| DELETE | `/api/gift-cards/{id}` | ADMIN | 204 / 400 / 403 | BLOCKED | Deactivate gift card. |
| GET | `/swagger-ui/index.html` | Public | 200 | BLOCKED | Open Swagger UI. |
| GET | `/v3/api-docs` | Public | 200 | BLOCKED | OpenAPI document. |

### Endpoint implementation observations

- Authentication controllers return HTTP 200 on successful register/login rather than HTTP 201 for registration.
- Voucher and gift-card creation correctly return HTTP 201.
- Deactivation correctly returns HTTP 204.
- Bean validation maps invalid request bodies to HTTP 400.
- Business rule conflicts map to HTTP 409 through `GlobalExceptionHandler`.
- Missing resources and duplicate codes use `IllegalArgumentException`, mapped to HTTP 400 rather than HTTP 404 or 409.

## 5. Security Findings

### Verified protections

- Passwords are encoded with `BCryptPasswordEncoder`.
- JWT uses a runtime `JWT_SECRET` property and expiration configuration.
- JWT validation checks signature and expiry through JJWT.
- Stateless Spring Security sessions are configured.
- Method-level role checks use `ADMIN` and `CUSTOMER` authorities.
- React receives only `VITE_API_URL`; the JWT secret is not part of frontend configuration.
- CORS is driven by `FRONTEND_URL` with local development defaults and does not use wildcard origins with credentials.
- `.gitignore` covers `.env`, frontend environment files, `node_modules/`, `dist/`, `target/`, and compiled classes.

### Findings

| Severity | Finding | Evidence | Recommended fix |
|---|---|---|---|
| HIGH | Default administrator/customer credentials are created in `DataInitializer.java`. | The initializer contains fixed seed account passwords in source code. Values are intentionally omitted from this report. | Remove fixed production credentials or gate the initializer behind a development profile; create production accounts through a controlled process and rotate any credentials that may have been exposed. |
| MEDIUM | JWT filter prints authenticated usernames and authorities to standard output. | `JwtAuthenticationFilter` contains debug `System.out` statements. | Remove debug output or use controlled, non-sensitive logging at an appropriate level. |
| MEDIUM | SQL statements are enabled in application configuration. | `spring.jpa.show-sql=true`. | Set it to `false` for production unless temporary diagnostics are explicitly required. |
| MEDIUM | Monetary values use `Double`. | `Voucher`, `GiftCard`, and redemption DTO/entity fields use `Double`. | Use `BigDecimal` for production-grade monetary calculations. |
| LOW | Only one automated test exists. | `VoucherServiceTest` covers duplicate voucher creation only. | Add service, controller/security, repository/integration, and frontend workflow tests. |
| LOW | Live invalid-login, token, role, and error-status behavior was not executable locally. | Application startup was blocked by MySQL authentication. | Repeat the test matrix after providing valid local or test-database credentials. |

No actual passwords, JWT secrets, API keys, or database credentials are included in this report.

## 6. Database Findings

### Required tables

The entity mappings define the following five tables:

1. `users`
2. `vouchers`
3. `redemptions`
4. `gift_cards`
5. `gift_card_redemptions`

### Relationships and constraints

- Every entity uses an identity-generated primary key.
- `users.email` is non-null and unique.
- `vouchers.code` is non-null, unique, and limited to 50 characters.
- `gift_cards.code` is non-null and unique.
- `vouchers.created_by` is a non-null many-to-one foreign key to `users`.
- `gift_cards.created_by` is a many-to-one relationship to `users`; the current mapping does not mark it non-null.
- `redemptions.user_id` and `redemptions.voucher_id` are non-null foreign keys.
- `redemptions` has a unique constraint on `(user_id, voucher_id)` to prevent duplicate voucher redemption by one customer.
- `gift_card_redemptions.gift_card_id` and `user_id` are non-null foreign keys.
- No cascade settings are declared on the inspected relationships.
- Hibernate uses `spring.jpa.hibernate.ddl-auto=update`.

### Database execution status

**DATABASE TEST BLOCKED**

The application reached MySQL but failed authentication with an access-denied error for the local `root` account. The previous empty-password failure was corrected; the current blocker is unavailable/incorrect local credentials. No SQL that drops, truncates, or deletes data was run, and no database contents were modified by this audit.

## 7. Capstone Requirements

| Requirement | Status | Evidence |
|---|---|---|
| React.js | PASS | Vite + React frontend exists under `frontend/`. |
| Bootstrap/Tailwind | MISSING / NOT REQUIRED BY CURRENT FILES | No Bootstrap or Tailwind dependency was found; the project uses custom CSS. Confirm with the college specification if one is mandatory. |
| Axios | PASS | `frontend/src/services/api.js` uses Axios. |
| Spring Boot | PASS | Spring Boot 3.5.5 parent and application source are present. |
| Java 17 | PASS | `java.version` is 17; Maven compiled with release 17. |
| Spring Security | PASS | Security filter chain, authentication provider, and method security are present. |
| JWT | PASS | JJWT dependency, token generation, and request filter are present. |
| JPA/Hibernate | PASS | Spring Data JPA dependency and entity mappings are present. |
| MySQL | PASS | MySQL Connector/J and MySQL datasource configuration are present. |
| Maven | PASS | `pom.xml` and successful Maven builds are present. |
| JUnit 5 | PASS | Existing test ran under JUnit Platform. |
| Swagger/OpenAPI | PASS | Springdoc dependency and `OpenApiConfig` are present; live page was blocked. |
| GitHub Actions | PASS | Backend and frontend workflows are present. |
| Five or more database tables | PASS | Five entity table mappings are present. |
| Two or more roles | PASS | `ADMIN` and `CUSTOMER` are implemented. |
| REST API | PASS | Controllers expose authentication, voucher, redemption, gift-card, health, and OpenAPI routes. |
| Validation | PASS | DTOs use `@Valid`, `@NotBlank`, `@Email`, `@Positive`, and `@Future`. |
| Exception handling | PASS | `GlobalExceptionHandler` maps validation, bad-request, and conflict cases. |
| Environment variables | PASS | Database, JWT, port, CORS, and Vite API configuration are environment-driven. |
| README | PASS | Local and Render/Railway deployment instructions are present. |
| Diagrams | PARTIAL | Editable DBML and diagrams.net sources are present; PNG exports are not present. |
| CI/CD | PASS | CI build/test workflows are present; no fake deployment steps were added. |
| Health endpoint | PASS | `/health` controller and unauthenticated security rule are present; live request was blocked. |
| Deployment readiness | PARTIAL | Build/configuration readiness passed; production secrets/resources and live Railway connection remain manual. |

## 8. Problems Found

### HIGH: Seed credentials in application source

**Problem:** The data initializer creates default accounts using fixed credentials.  
**Evidence:** `DataInitializer.java` contains fixed account setup.  
**Recommended fix:** Disable this initializer in production or replace it with a controlled, environment-specific bootstrap process. Rotate exposed credentials.

### MEDIUM: Debug authentication output

**Problem:** JWT processing writes usernames and authorities to standard output.  
**Evidence:** Debug `System.out` statements are present in `JwtAuthenticationFilter.java`.  
**Recommended fix:** Remove them before deployment.

### MEDIUM: SQL logging enabled

**Problem:** SQL logging may expose query and data details in production logs.  
**Evidence:** `spring.jpa.show-sql=true`.  
**Recommended fix:** Set it to `false` in production.

### MEDIUM: Local runtime unavailable

**Problem:** Live API, database, Swagger, health, and browser flows could not run.  
**Evidence:** MySQL rejected the local root credentials, causing Hibernate metadata initialization to fail.  
**Recommended fix:** Supply valid local test-database credentials or run the application against an isolated test MySQL instance, then repeat the live matrix.

### LOW: Limited automated coverage

**Problem:** Only one service unit test is present.  
**Evidence:** `VoucherServiceTest` covers duplicate voucher code rejection only.  
**Recommended fix:** Add tests for authentication, role authorization, voucher redemption rules, gift-card balance/history, and controller responses.

### LOW: No exported PNG diagrams

**Problem:** Diagram source files exist but PNG submission artifacts are absent.  
**Evidence:** `docs/diagrams/` contains DBML and `.drawio` sources only.  
**Recommended fix:** Export the required PNG files manually before final submission if the college requires them.

## 9. Changes Made During Testing

Created only:

- `PRE_DEPLOYMENT_TEST_REPORT.md`

No application source, test source, database, workflow, or configuration files were modified during this audit.

## 10. Deployment Blockers

1. Configure valid Railway MySQL environment variables in Render: `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.
2. Configure a production-only `JWT_SECRET` in Render.
3. Remove or disable fixed seed credentials before production deployment.
4. Remove JWT debug output.
5. Disable SQL logging for production.
6. Execute live API and browser tests against an isolated database before public deployment.

## 11. Remaining Manual Steps

- Create the Railway MySQL service.
- Copy Railway connection details into Render backend environment variables without committing them.
- Set `FRONTEND_URL` to the deployed frontend origin.
- Set frontend `VITE_API_URL` to the deployed backend `/api` URL.
- Create the Render backend service using the documented Maven build/start commands.
- Create the Render frontend static site with publish directory `dist`.
- Repeat `/health`, authentication, authorization, voucher, gift-card, history, and Swagger tests after startup succeeds.
- Export diagram PNGs if required by the college.
- Record the final capstone demonstration.

## 12. Final Readiness

# READY WITH BLOCKERS

The code compiles, the existing automated test passes, the production JAR builds, and the React production build passes. Deployment is not yet ready for live use because production seed credentials and debug/SQL logging findings remain, and live database/API/browser validation was blocked by unavailable valid local MySQL credentials.
