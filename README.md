📘 Car Rental REST API — README
🚀 Overview
This project is a Car Rental REST API built using:

Spring Framework 7

Spring Boot 4.1.1

Java 25

H2 in‑memory database

It follows a layered architecture (Controller → Service → Repository → Model) and provides a clean, maintainable structure for building scalable REST applications.

The API supports:

Managing cars and car units

Creating and updating reservations

Checking availability

Querying reservations by car, unit, or type

All endpoints are fully tested using unit tests and integration tests.

🧱 Architecture
The project uses a classic layered approach:

1. API Layer (Controllers)
Handles HTTP requests and responses.
Uses Spring MVC annotations and integrates with validation and exception handling.

2. Service Layer
Contains business logic:

Reservation creation rules

Overlap detection

Availability checks

Car and unit selection logic

3. Repository Layer
Uses Spring Data JPA to interact with the H2 database.

4. Model Layer
Defines JPA entities for:

Car

CarUnit

Reservation

5. DTO Layer
Provides request/response objects with validation annotations.

6. Validation Layer
Custom validators such as:

@ValidReservationDates

@ValidCarSelector

7. Global Exception Handling
A centralized GlobalExceptionHandler ensures consistent error responses.

🧪 Testing
The project includes:

✔ Unit Tests
Controller tests using MockMvc

Service tests using Mockito

Validator tests

✔ Integration Tests

H2 in‑memory database

This ensures both correctness and stability across layers.

🗄️ Database
The application uses H2 in‑memory database, which means:

No external DB setup required

Data seeded using DataSeeder and resets on each application restart

📚 API Documentation (Swagger)
Swagger UI is enabled through Springdoc OpenAPI.

Once the application is running, explore the API at:

Code
http://localhost:8080/swagger-ui/index.html
This provides:

Interactive API documentation

Example requests

Schema definitions

Ability to test endpoints directly in the browser

▶️ Running the Application
Prerequisites
Java 25 installed

Maven or Gradle

Run using Maven
Code
mvn spring-boot:run

The application starts on:

Code
http://localhost:8080
📂 Project Structure
Code
src/main/java/com/crd/car_rental
│
├── api/                 # Controllers
├── dto/                 # Request/Response DTOs
├── error/               # Global exception handling
├── model/               # JPA entities
├── repository/          # Spring Data repositories
├── service/             # Business logic
└── validation/          # Custom validators
🧩 Technologies Used
Component	Version
Spring Framework	7.x
Spring Boot	4.1.1
Java	25
H2 Database	In‑memory
Springdoc OpenAPI	3.1.0
JUnit 5	✔
Mockito	✔