# StayEase

## Problem Statement
Develop and deploy a RESTful API service using Spring Boot to streamline the room booking process for a hotel management aggregator application. The service must use MySQL for data persistence. 

The application is simplified, focusing on essential features of a hotel booking system.

---
## RentRead API Documentation
https://documenter.getpostman.com/view/32910654/2sAYX3qiHr

## Key Features
* **Authentication and Authorization**:
  - The service uses JWT tokens for stateless authentication.
  - There are three roles: `CUSTOMER`, `HOTEL MANAGER`, and `ADMIN`.
  - Public and private API endpoints are defined based on user roles.

* **User Registration and Login**:
  - Users can register by providing their email, password, first name, last name, and optionally, a role.
  - Passwords are hashed using BCrypt for security.
  - A JWT token is generated upon successful registration or login.

* **Hotel Management**:
  - Store and manage hotel details such as hotel name, location, description, and the number of available rooms.
  - Public API: Anyone can browse available hotels.
  - Admin: Can create and delete hotels.
  - Hotel Manager: Can update hotel details, but not delete or create hotels.

* **Booking Management**:
  - Customers can book a room, with one room being booked per request.
  - Only hotel managers are authorized to cancel bookings.

---

## Entities

### User
The User entity includes the following fields:
- **Email**: Unique identifier
- **Password**: The user's password (hashed)
- **First Name**: User’s first name
- **Last Name**: User’s last name
- **Role**: Defaulted to `CUSTOMER`, can also be `HOTEL MANAGER` or `ADMIN`

### Hotel
The Hotel entity includes the following fields:
- **Hotel Name**: Name of the hotel
- **Location**: The location of the hotel
- **Description**: Brief description of the hotel
- **Available Rooms**: The number of available rooms for booking

### Booking
The Booking entity includes:
- **User**: The customer who made the booking
- **Hotel**: The hotel for which the booking was made
- **Booking Date**: Date when the room was booked

---

## API Endpoints

### Public Endpoints
1. **User Registration**:
   - `POST /register`
   - Allows new users to register by providing their email, password, first name, last name, and optional role.
   - Returns a JWT token on successful registration.
   
2. **User Login**:
   - `POST /login`
   - Allows users to log in using their credentials (email and password).
   - Returns a JWT token on successful login.
   
3. **Get All Hotels**:
   - `GET /hotels`
   - Retrieves a list of all available hotels. No authentication is required.

### Private Endpoints
1. **Book a Room**:
   - `POST /book/{hotelId}`
   - Allows a `CUSTOMER` to book a room at the specified hotel.
   - Returns the booking confirmation.

2. **Cancel a Booking**:
   - `DELETE /booking/{bookingId}`
   - Allows a `HOTEL MANAGER` to cancel a booking.
   - Returns a success message upon cancellation.

3. **Create a Hotel**:
   - `POST /hotels`
   - Allows an `ADMIN` to create a new hotel. The admin provides hotel name, location, description, and available rooms.

4. **Update a Hotel**:
   - `PUT /hotels/{hotelId}`
   - Allows a `HOTEL MANAGER` to update the hotel details.

5. **Delete a Hotel**:
   - `DELETE /hotels/{hotelId}`
   - Allows an `ADMIN` to delete a hotel.

---

## Exception Handling

The application includes several exceptions to handle various scenarios, ensuring smooth operations:

- **HotelNotFoundException**: Thrown when a hotel with a given ID is not found. This exception returns a `404 Not Found` HTTP status code.
- **BookingNotFoundException**: Thrown when a booking with the specified ID does not exist. This exception returns a `404 Not Found` HTTP status code.
- **UnauthorizedException**: Thrown when a user tries to access a resource without proper authorization. This exception is handled with a `403 Forbidden` HTTP status code.
- **MaximumRoomsExceededException**: Thrown when a customer attempts to book a room while all available rooms are booked. This is responded with a `409 Conflict` HTTP status code.

---

## Security

The service uses **JWT tokens** for stateless authentication. Each protected API endpoint requires the inclusion of a valid JWT token in the request header for authorization.

Roles:
- **CUSTOMER**: Can browse hotels, book a room, and view their bookings.
- **HOTEL MANAGER**: Can update hotel details and cancel bookings.
- **ADMIN**: Has full control, including creating and deleting hotels.

---

## Unit Tests

Unit tests are included for:
- **Authentication**: Ensures users can successfully register and log in.
- **Hotel Management**: Verifies that only admins can create and delete hotels, and hotel managers can update hotel details.
- **Booking Management**: Ensures customers can book rooms and hotel managers can cancel bookings.

---
## Deployment

The project is deployed on **Render** and can be accessed using Postman using the below link:

🔗 **https://stayease-rzyn.onrender.com**


## Getting Started

### Prerequisites
- Java 11+
- Spring Boot
- MySQL Database

### Running the Project

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/StayEase.git
   cd StayEase

2. **Configure MySQL**:
    Set up a MySQL database and update the application.properties file with your database connection details.

3. **Build and run the application**:
   ```bash
   ./gradlew build
    ./gradlew bootRun
   
4.  **Access the API at http://localhost:8080.**
