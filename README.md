# RideShare Backend

## Commands

### Configure Application Properties
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

### Build the Project
```bash
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

## Project Structure

```
rideshare/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/rideshare/
│   │   │       ├── RideShareApplication.java          # Main Spring Boot Application
│   │   │       ├── model/                             # Domain Entities
│   │   │       │   ├── User.java                      # User entity (ROLE_USER, ROLE_DRIVER)
│   │   │       │   └── Ride.java                      # Ride entity (REQUESTED, ACCEPTED, COMPLETED)
│   │   │       ├── repository/                        # Data Access Layer
│   │   │       │   ├── UserRepository.java            # MongoDB repository for User
│   │   │       │   └── RideRepository.java            # MongoDB repository for Ride
│   │   │       ├── service/                           # Business Logic Layer
│   │   │       │   ├── AuthService.java               # Authentication & Registration
│   │   │       │   ├── RideService.java               # Ride management logic
│   │   │       │   └── UserService.java               # User operations
│   │   │       ├── controller/                        # REST API Layer
│   │   │       │   ├── AuthController.java            # /api/auth/** endpoints
│   │   │       │   ├── RideController.java            # /api/v1/rides/** endpoints
│   │   │       │   ├── UserController.java            # /api/v1/user/** endpoints
│   │   │       │   └── DriverController.java          # /api/v1/driver/** endpoints
│   │   │       ├── config/                            # Configuration Layer
│   │   │       │   ├── SecurityConfig.java            # Spring Security configuration
│   │   │       │   └── JwtAuthenticationFilter.java   # JWT token validation filter
│   │   │       ├── dto/                               # Data Transfer Objects
│   │   │       │   ├── RegisterRequest.java           # Registration request DTO
│   │   │       │   ├── LoginRequest.java              # Login request DTO
│   │   │       │   ├── AuthResponse.java              # Authentication response DTO
│   │   │       │   ├── CreateRideRequest.java         # Create ride request DTO
│   │   │       │   ├── RideResponse.java              # Ride response DTO
│   │   │       │   └── ErrorResponse.java             # Error response DTO
│   │   │       ├── exception/                         # Exception Handling
│   │   │       │   ├── GlobalExceptionHandler.java    # Global exception handler
│   │   │       │   ├── NotFoundException.java         # 404 exception
│   │   │       │   ├── BadRequestException.java       # 400 exception
│   │   │       │   └── UnauthorizedException.java     # 401 exception
│   │   │       └── util/                              # Utility Classes
│   │   │           └── JwtUtil.java                   # JWT token generation & validation
│   │   └── resources/
│   │       └── application.properties                 # Application configuration
│   └── test/
├── pom.xml                                            # Maven dependencies
├── .gitignore                                         # Git ignore rules
└── README.md                                          # This file
```

## System Architecture

### 1. Layered Architecture

The application follows a clean, layered architecture pattern:

```
┌─────────────────────────────────────────────────┐
│           REST API Layer (Controllers)          │
│  - AuthController                               │
│  - RideController                               │
│  - UserController                               │
│  - DriverController                             │
└─────────────────────┬───────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│         Business Logic Layer (Services)         │
│  - AuthService (registration, login)            │
│  - RideService (CRUD operations)                │
│  - UserService (user management)                │
└─────────────────────┬───────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│       Data Access Layer (Repositories)          │
│  - UserRepository (MongoDB)                     │
│  - RideRepository (MongoDB)                     │
└─────────────────────┬───────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────┐
│              MongoDB Database                   │
│  - users collection                             │
│  - rides collection                             │
└─────────────────────────────────────────────────┘
```

### 2. Security Architecture

#### JWT-Based Authentication Flow

```
┌──────────┐                                    ┌──────────┐
│  Client  │                                    │  Server  │
└────┬─────┘                                    └────┬─────┘
     │                                               │
     │  1. POST /api/auth/register                   │
     │  { username, password, role }                 │
     ├──────────────────────────────────────────────►│
     │                                               │
     │                                          2. Validate Input
     │                                          3. BCrypt Password
     │                                          4. Save to MongoDB
     │                                          5. Generate JWT
     │                                               │
     │  6. Response: { token, username, role }       │
     │◄──────────────────────────────────────────────┤
     │                                               │
     │  7. Store token in client                     │
     │                                               │
     │  8. POST /api/v1/rides                        │
     │  Headers: Authorization: Bearer <token>       │
     ├──────────────────────────────────────────────►│
     │                                               │
     │                                     9. JwtAuthenticationFilter
     │                                          - Extract token
     │                                          - Validate token
     │                                          - Set Authentication
     │                                                │
     │                                     10. SecurityConfig
     │                                          - Check authorities
     │                                          - Authorize request
     │                                                │
     │                                     11. Process request
     │                                                │
     │  12. Response: { ride details }                │
     │◄───────────────────────────────────────────────┤
     │                                                │
```

#### Security Components

1. **JwtUtil**
   - Generates JWT tokens with username and role claims
   - Validates token signature and expiration
   - Extracts user information from tokens

2. **JwtAuthenticationFilter**
   - Intercepts every HTTP request
   - Extracts JWT from Authorization header
   - Validates token and sets Spring Security context

3. **SecurityConfig**
   - Configures URL-based security rules
   - Defines public endpoints (/api/auth/**)
   - Enforces role-based access control
   - Disables session management (stateless)

### 3. Data Model Architecture

#### Entity Relationship Diagram

```
┌──────────────────────────┐
│         USER             │
├──────────────────────────┤
│ id: String (PK)          │
│ username: String (UK)    │
│ password: String (Hash)  │
│ role: String             │
│   - ROLE_USER            │
│   - ROLE_DRIVER          │
└──────────┬───────────────┘
           │
           │ 1:N (as passenger)
           │
           ▼
┌──────────────────────────┐
│         RIDE             │
├──────────────────────────┤
│ id: String (PK)          │
│ userId: String (FK)      │ ──► References USER (passenger)
│ driverId: String (FK)    │ ──► References USER (driver)
│ pickupLocation: String   │
│ dropLocation: String     │
│ status: String           │
│   - REQUESTED            │
│   - ACCEPTED             │
│   - COMPLETED            │
│ createdAt: Date          │
└──────────────────────────┘
           ▲
           │ 1:N (as driver)
           │
     ┌─────┴────────┐
     │   USER       │
     │ (ROLE_DRIVER)│
     └──────────────┘
```

### 4. API Request Flow

#### Example: User Creates a Ride

```
1. CLIENT REQUEST
   ↓
   POST /api/v1/rides
   Headers: Authorization: Bearer eyJhbGc...
   Body: { "pickupLocation": "A", "dropLocation": "B" }

2. SECURITY LAYER
   ↓
   JwtAuthenticationFilter
   ├─ Extract token from header
   ├─ Validate token signature
   ├─ Extract username and role
   └─ Set SecurityContext

3. AUTHORIZATION CHECK
   ↓
   SecurityConfig
   ├─ Check if user has ROLE_USER
   ├─ Verify endpoint access
   └─ Allow or deny request

4. CONTROLLER LAYER
   ↓
   RideController.createRide()
   ├─ Validate request body (@Valid)
   ├─ Extract username from Authentication
   └─ Call service layer

5. SERVICE LAYER
   ↓
   RideService.createRide()
   ├─ Get user ID from username
   ├─ Create new Ride entity
   ├─ Set status = REQUESTED
   ├─ Set createdAt = current date
   └─ Call repository layer

6. REPOSITORY LAYER
   ↓
   RideRepository.save()
   ├─ Generate unique ID
   ├─ Save to MongoDB
   └─ Return saved entity

7. RESPONSE MAPPING
   ↓
   RideService.mapToResponse()
   └─ Convert entity to RideResponse DTO

8. CONTROLLER RESPONSE
   ↓
   Return ResponseEntity<RideResponse>
   └─ HTTP 200 OK with ride details

9. CLIENT RECEIVES
   ↓
   { "id": "...", "userId": "...", "status": "REQUESTED", ... }
```

### 5. Exception Handling Architecture

```
┌─────────────────────────┐
│   Any Controller        │
│   throws Exception      │
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────────────────────┐
│   GlobalExceptionHandler                │
│   (@RestControllerAdvice)               │
├─────────────────────────────────────────┤
│                                         │
│  @ExceptionHandler                      │
│  ├─ NotFoundException      → 404        │
│  ├─ BadRequestException    → 400        │
│  ├─ UnauthorizedException  → 401        │
│  ├─ ValidationException    → 400        │
│  └─ General Exception      → 500        │
│                                         │
└───────────┬─────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────┐
│   Standardized Error Response           │
├─────────────────────────────────────────┤
│ {                                       │
│   "error": "ERROR_TYPE",                │
│   "message": "Description",             │
│   "timestamp": "2025-01-20T12:00:00Z"   │
│ }                                       │
└─────────────────────────────────────────┘
```

### 6. Validation Architecture

Input validation is implemented at multiple levels:

```
1. DTO LEVEL (First Line of Defense)
   ↓
   @NotBlank, @Size, @Pattern annotations
   ├─ Validates before reaching controller
   └─ Throws MethodArgumentNotValidException

2. BUSINESS LOGIC LEVEL
   ↓
   Service layer validation
   ├─ Username already exists
   ├─ Ride status validation
   └─ Authorization checks

3. DATABASE LEVEL
   ↓
   MongoDB constraints
   ├─ Unique index on username
   └─ Required fields
```

### 7. Role-Based Access Control (RBAC)

```
┌─────────────────────────────────────────────────────┐
│                    Endpoints                        │
├─────────────────────────────────────────────────────┤
│                                                     │
│  PUBLIC (No Authentication Required)                │
│  ├─ POST /api/auth/register                         │
│  └─ POST /api/auth/login                            │
│                                                     │
│  ROLE_USER Only                                     │
│  ├─ POST /api/v1/rides          (Create ride)       │
│  └─ GET  /api/v1/user/rides     (View my rides)     │
│                                                     │
│  ROLE_DRIVER Only                                   │
│  ├─ GET  /api/v1/driver/rides/requests              │
│  │       (View pending rides)                       │
│  └─ POST /api/v1/driver/rides/{id}/accept           │
│         (Accept ride)                               │
│                                                     │
│  Both ROLE_USER and ROLE_DRIVER                     │
│  └─ POST /api/v1/rides/{id}/complete                │
│         (Complete ride)                             │
│                                                     │
└─────────────────────────────────────────────────────┘
```

### 8. Technology Stack

```
┌─────────────────────────────────────────┐
│        Presentation Layer               │
│        REST API (JSON)                  │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│      Spring Boot Framework              │
│  ├─ Spring Web (REST)                   │
│  ├─ Spring Security (Auth)              │
│  ├─ Spring Data MongoDB                 │
│  └─ Spring Validation                   │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│      Security & Authentication          │
│  ├─ JWT (JSON Web Tokens)               │
│  ├─ BCrypt (Password Hashing)           │
│  └─ JJWT Library                        │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│      Data Persistence                   │
│  └─ MongoDB (NoSQL Database)            │
└─────────────────────────────────────────┘
```

### 9. API Endpoints Summary

| Method | Endpoint                              | Role          | Description                |
|--------|---------------------------------------|---------------|----------------------------|
| POST   | /api/auth/register                    | PUBLIC        | Register new user          |
| POST   | /api/auth/login                       | PUBLIC        | Login and get JWT token    |
| POST   | /api/v1/rides                         | ROLE_USER     | Create a new ride request  |
| GET    | /api/v1/user/rides                    | ROLE_USER     | Get user's ride history    |
| GET    | /api/v1/driver/rides/requests         | ROLE_DRIVER   | View pending ride requests |
| POST   | /api/v1/driver/rides/{rideId}/accept  | ROLE_DRIVER   | Accept a ride request      |
| POST   | /api/v1/rides/{rideId}/complete       | AUTHENTICATED | Mark ride as completed     |

### 10. Ride Status State Machine

```
┌──────────────┐
│   REQUESTED  │  ← Initial state when user creates ride
└──────┬───────┘
       │
       │ Driver accepts ride
       │ POST /api/v1/driver/rides/{id}/accept
       │
       ▼
┌──────────────┐
│   ACCEPTED   │  ← Ride assigned to driver
└──────┬───────┘
       │
       │ User or Driver completes ride
       │ POST /api/v1/rides/{id}/complete
       │
       ▼
┌──────────────┐
│  COMPLETED   │  ← Final state
└──────────────┘
```

---

Created with ❤️ by techSas

