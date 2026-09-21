Update ONLY the README.md file of my existing project.

Project name:
Gift Card and Voucher Management System

Important:

* Do NOT modify Java backend code.
* Do NOT modify React frontend code.
* Do NOT modify database code.
* Do NOT modify pom.xml.
* Do NOT modify package files.
* Do NOT change project functionality.
* Only update README.md documentation.

Use the ACTUAL current project structure and implementation.

Fix these outdated details:

1. Frontend technology must say:

* React
* Vite
* Axios
* JavaScript
  Do NOT say HTML5/CSS3/Fetch API as the main frontend stack.

2. Update the project structure to accurately show:

* Spring Boot backend under src/main/java
* controller
* dto
* entity
* repository
* service
* security
* resources
* frontend/
* frontend/src
* frontend package.json
* pom.xml

3. Keep the project title exactly:
   Gift Card and Voucher Management System

4. Document the current authentication:

* Customer registration
* Customer login
* Admin login
* BCrypt password hashing
* JWT authentication
* ADMIN/CUSTOMER role-based authorization
* Protected APIs

5. Document Voucher Management:

* Admin create voucher
* View vouchers
* Update voucher
* Deactivate voucher
* Expiry validation
* Maximum usage
* Duplicate redemption prevention
* Customer redemption
* Customer history
* Admin redemption history

6. Document Gift Card Management:

* Admin create gift card
* View gift cards
* Deactivate gift card
* Customer view available gift cards
* Customer redeem gift card
* Gift card balance reduction
* Multiple redemptions
* Insufficient balance validation
* Expiry validation
* Duplicate code validation
* Gift card redemption history if it is already implemented in the current code

7. IMPORTANT:
   Do NOT list gift card transaction history or customer gift card history under "Future Enhancements" if those features already exist in the current code.

8. Update the API documentation based on the actual current controllers/endpoints. Do not invent endpoints.

9. Update the database section based on the actual current entities/tables.

10. Update the local running instructions with the actual commands:
    Backend:
    mvn spring-boot:run

Frontend:
cd frontend
npm install
npm run dev

Mention:
Backend: http://localhost:8080
Frontend: http://localhost:5173

11. Keep environment-variable instructions accurate:
    DB_URL
    DB_USERNAME
    DB_PASSWORD
    JWT_SECRET
    FRONTEND_URL
    VITE_API_URL

Do not expose any real passwords, JWT secrets, or database credentials.

12. Update the deployment section only using information already present in the project. Do not invent deployment details.

13. Remove outdated references to "Secure Voucher System" where they refer to the project title. Use:
    Gift Card and Voucher Management System

14. Keep the README professional and suitable for a college capstone project.

15. Do not add fake features.

After editing README.md:

* Show me the complete list of changes made.
* Confirm that ONLY README.md was modified.
* Do not run large upgrades or change Java versions.
* Do not create a new branch.
* Do not modify any source code.
