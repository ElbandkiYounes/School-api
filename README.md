# School API

A comprehensive Java-based REST API for school management, handling room scheduling, reservations, and academic resources.

## 🚀 Features

- **Room Management**
  - CRUD operations for classrooms
  - Room availability checking
  - Access control for room management
  
- **Reservation System**
  - Create and manage room reservations
  - Check current day and week schedules
  - Professor-specific reservations
  
- **Schedule Management (Emploi Du Temps)**
  - Manage academic schedules
  - Track room occupancy
  - Schedule conflicts prevention
  
- **Liberation System**
  - Manage room liberations
  - Week-based liberation tracking
  - Professor-specific liberations

## 🛠️ Tech Stack

- Java
- Jakarta EE
- JAX-RS for RESTful API
- JPA for database operations
- JSON for data exchange

## 📋 Prerequisites

- Java 11 or higher
- Jakarta EE compatible application server
- MySQL/PostgreSQL database (configured in application)
- Maven for dependency management

## 🔧 Installation

1. Clone the repository:
```bash
git clone https://github.com/ElbandkiYounes/School-api.git
```

2. Navigate to the project directory:
```bash
cd School-api
```

3. Build the project:
```bash
mvn clean install
```

4. Deploy the generated WAR file to your application server.

## 🔒 Security

The API implements role-based access control with the following roles:
- RESPONSABLE_SALLES (Room Manager)
- PROFESSEUR (Professor)
- Other roles as defined in the system

Authentication is required for all endpoints using the `@Secured` annotation.

## 💻 Usage

Ensure to include appropriate headers in your requests:
- `Content-Type: application/json`
- `email`: User's email for authentication
```

Now, let me provide the API documentation:

```markdown
# API Documentation

Base URL: `/api`

## 🏫 Rooms (Salles)

### Get All Rooms
```http
GET /salles
Authorization: Required
Response: List of Salle objects
```

### Get Room by ID
```http
GET /salles/{id}
Authorization: Required
Response: Salle object
```

### Get Free Rooms
```http
POST /salles/free-day-week-seance
Authorization: Required
Body: {
    "day": string,
    "week": string,
    "seance": string
}
Response: List of available Salle objects
```

### Create Room
```http
POST /salles
Authorization: Required (RESPONSABLE_SALLES only)
Body: {
    "name": string,
    // Other room properties
}
Response: Created Salle object
```

### Update Room
```http
PUT /salles/{id}
Authorization: Required (RESPONSABLE_SALLES only)
Body: {
    "name": string,
    // Updated properties
}
Response: Updated Salle object
```

### Delete Room
```http
DELETE /salles/{id}
Authorization: Required (RESPONSABLE_SALLES only)
Response: 204 No Content
```

## 📅 Reservations

### Get All Reservations
```http
GET /reservations
Authorization: Required
Response: List of Reservation objects
```

### Get Professor's Reservations
```http
GET /reservations/professeur/{professeurId}
Authorization: Required
Response: List of professor's Reservation objects
```

### Get Current Day
```http
GET /reservations/day
Authorization: Required
Response: Current day information
```

### Get Current Week
```http
GET /reservations/week
Authorization: Required
Response: Current week information
```

### Create Reservation
```http
POST /reservations
Authorization: Required (PROFESSEUR only)
Body: {
    // Reservation details
}
Response: Created Reservation object
```

### Delete Reservation
```http
DELETE /reservations/{id}
Authorization: Required
Response: 204 No Content
```

## 🔓 Liberations

### Get All Liberations
```http
GET /liberations
Authorization: Required
Response: List of Liberation objects
```

### Get Professor's Liberations
```http
GET /liberations/professeur/{professeurId}
Authorization: Required
Response: List of professor's Liberation objects
```

### Get Liberations by Week
```http
GET /liberations/week/{week}
Authorization: Required
Response: List of Liberation objects for specified week
```

### Create Liberation
```http
POST /liberations/emploi-du-temps/{emploiDuTempsId}
Authorization: Required
Body: {
    // Liberation details
}
Response: Created Liberation object
```

### Delete Liberation
```http
DELETE /liberations/{id}
Authorization: Required
Response: 204 No Content
```

## 📝 Error Responses

The API may return the following error responses:

- `400 Bad Request`: Invalid input data
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource conflict (e.g., duplicate names)
- `500 Internal Server Error`: Server-side error

Each error response includes a descriptive message explaining the error.

## 📦 Models

### Salle (Room)
```json
{
    "id": long,
    "name": string
}
```

### Reservation
```json
{
    "id": long,
    "status": string,
    // Other reservation properties
}
```

### Liberation
```json
{
    "id": long,
    // Liberation properties
}
```
