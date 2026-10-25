📘 Car Rental REST API
A modern, fully tested Car Rental REST API built with:

Spring Framework 7

Spring Boot 4.1.1

Java 25

H2 in‑memory database

The project follows a clean layered architecture and includes complete unit tests, integration tests, and Swagger documentation for easy exploration.

🚀 Features
Manage cars, car units, and reservations

Check availability by car unit, car entity, or car type

Custom validation (date validation, selector validation)

Global exception handling

In‑memory H2 database for fast development and testing

Swagger UI for interactive API exploration

🧱 Architecture
This project uses a classic layered approach:

API Layer
Spring MVC controllers handling HTTP requests and responses.

Service Layer
Business logic:

Reservation creation

Overlap detection

Availability checks

Car/unit access

Repository Layer
Spring Data JPA repositories interacting with H2.

Model Layer
JPA entities for Car, CarUnit, Reservation.

DTO Layer
Request/response objects with validation annotations.

Validation Layer
Custom validators:

@ValidReservationDates

Global Exception Handling
Consistent error responses via GlobalExceptionHandler.

🧪 Testing
The API is fully tested using:

✔ Unit Tests
Controllers (MockMvc)

Services (Mockito)

Validators

✔ Integration Tests
Full Spring Boot context

Real H2 database

Repository behavior

🗄️ Database
The application uses H2 in‑memory database, which requires no external setup.

📚 API Documentation (Swagger)
Swagger UI is available at:

http://localhost:8080/swagger-ui/index.html

You can explore all endpoints, send test requests, and view schema definitions.

▶️ Running the Application
Prerequisites
Java 25

Maven
Run with Maven
bash
mvn spring-boot:run

Application starts at:

http://localhost:8080
📂 Project Structure

<pre>
src/main/java/com/crd/car_rental
│
├── api/                 # Controllers
├── dto/                 # Request/Response DTOs
├── error/               # Global exception handling
├── model/               # JPA entities
├── repository/          # Spring Data repositories
├── service/             # Business logic
└── validation/          # Custom validators
</pre>

🧩 Technologies Used
| Component | Version |
| --- | --- |
| Spring Framework | 7.x |
| Spring Boot | 4.1.1 |
| Java | 25 |
| H2 Database | In‑memory |
| Springdoc OpenAPI | 3.1.0 |
| JUnit 5 | ✔ |
| Mockito | ✔ |
