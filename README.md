<h1 align="center"> Moonlight</h1> <br>
<p align="center">
    Back end for the Moonlight Booking System.
</p>
<p align="center">
    <strong>bootcamp-java-24-july</strong>
</p>

## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [Testing](#testing)
- [API](#API)


## Introduction
Moonlight is a comprehensive client-based system 
designed for managing various types of reservations. 
It supports restaurant, bar, car rental, and hotel bookings, 
catering to a wide range of customer needs. 
The system also includes admin endpoints for managing assets 
such as hotel rooms, bar tables, restaurant tables, and rental cars. 
Built with Spring Boot and MySQL, Moonlight ensures seamless 
integration for both customer and administrative operations, 
making it an efficient solution for hospitality and service industries.

## Features

#### Spring Boot Starter Web:
This dependency simplifies the setup and development of web applications in Java. 
It includes embedded servers like Tomcat, allowing for quick deployment and testing. 
With features like RESTful APIs and integrated security, it streamlines the creation of web services, 
making it an essential component for the Moonlight Hotel system.

#### Spring Boot Starter Data JPA: 
This dependency simplifies data access by integrating the Java Persistence API (JPA) with Spring Boot. 
It allows for the easy implementation of database operations, enabling developers to focus on 
writing business logic instead of boilerplate code. With support for various databases, including MySQL, 
it facilitates efficient management of the data required for hotel, restaurant, and car rental reservations, 
ensuring a seamless experience for users.


### Docker Setup
There are three Docker Compose files provided for flexible configuration:
1. **Database Only**: Standalone setup for MySQL database.
2. **Application Only**: Runs the Spring Boot application without the database.
3. **Combined (DB + App)**: Both the MySQL database and the application run together in one setup.

You can choose the appropriate Docker Compose file depending on your requirements during development or deployment.

### Security
The API includes robust security features:

- JWT Authentication: Users are authenticated using JWT tokens to ensure secure access.
- Password Encryption: User passwords are securely encrypted to protect sensitive information.

### Payment Systems
The application fully implements two payment systems:

- Stripe: For credit/debit card transactions.
- PayPal: For online payments.

### Docker for Database
Steps to set up:
1. Download and install [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- Ensure the version you install is made for your OS
- Ensure Virtualization is Enabled in the BIOS(Most commonly under CPU/Advanced)
2. Once the Docker Desktop is up and running:
- Open your Terminal and locate projectname\src\main\resources
- Run the command "docker-compose up -d"
- (To turn it off, run the command "docker-compose down")
3. To use the docker, before running the app change the default profile from 'dev' to 'docker'
4. The docker is set to not interfere with a running MySQLService, so it runs on port 3307,
if that is not a free port, make sure you select a free available one in docker-compose.yml
5. You can test using the same endpoints as you do normally.

### Docker for Application
1. Run this command in the path where the docker-compose file for the App. is:

docker-compose -f app-docker-compose.yml up --build
- IF having any issues:

docker network create bootcamp-app-network

docker system prune --volumes
then run the first command again.
2. After the app has been started successfully:
- To stop it:

docker-compose stop(if none other docker compose files are present in the same file path)

or

docker-compose -f app-docker-compose.yml stop
- To start it again:

docker-compose start(if none other docker compose files are present in the same file path)

or

docker-compose -f app-docker-compose.yml start

## Testing
The project includes comprehensive tests for all repository, service, and controller classes, ensuring the integrity of the application's core functionalities. These tests are written using the following libraries:
- **JUnit (Jupiter)**: For writing unit tests.
- **Mockito**: For mocking dependencies and verifying interactions within the tests.
- **RestAssured**: For testing the REST API endpoints with ease.

These tests are written to validate the behavior of:
- **Repositories**: Ensure proper data access and manipulation.
- **Services**: Verify the business logic and service layer.
- **Controllers**: Test the API endpoints for correct request handling and response.

## API
**Swagger Documentation**: Detailed API documentation and interactive testing are available through Swagger. 
You can access it at:

http://localhost:8080/swagger-ui/index.html


In the searchbar of the swagger you must type --> /api-docs

### Endpoints

#### Bar Endpoints
- **POST** `/api/v1/bars/events/add` - Add a new event to the bar.
- **GET** `/api/v1/bars/events/search/` - Search for bar events.
- **GET** `/api/v1/bars/screen/get-info/` - Get screen information in the bar.
- **GET** `/api/v1/bars/search/screen` - Search for available screens.
- **GET** `/api/v1/bars/search/screen/seats` - Get seat availability for a screen in the bar.

#### Restaurant Reservations
- **GET** `/api/v1/reservations/restaurant/available-tables` - Fetch available tables in the restaurant.
- **POST** `/api/v1/reservations/restaurant/create-reservation/` - Create a restaurant reservation.
- **GET** `/api/v1/restaurants/search` - Search for restaurant assets.

#### Bar Reservations
- **GET** `/api/v1/reservations/bar/available-seats/` - Fetch available seats in the bar.
- **POST** `/api/v1/reservations/bar/create-reservation/` - Create a bar reservation.

#### Car Rental
- **GET** `/api/v1/reservations/car/available/` - Fetch available rental cars.
- **POST** `/api/v1/reservations/car/create-reservation/` - Create a car rental reservation.
- **GET** `/api/v1/cars/images` - Fetch images of available cars.
- **GET** `/api/v1/cars/search/` - Search for available cars.

#### Hotel Reservations
- **GET** `/api/v1/reservations/hotel/available-rooms/` - Fetch available hotel rooms.
- **POST** `/api/v1/reservations/hotel/create-reservation/` - Create a hotel room reservation.
- **GET** `/api/v1/hotel/search` - Search for available hotel rooms.

#### Payments
- **GET** `/api/v1/payments/paypal/success` - Handle successful PayPal payments.
- **POST** `/api/v1/payments/paypal/pay-reservation` - Pay for a reservation with PayPal.
- **GET** `/api/v1/payments/stripe/checkout/cancel` - Handle canceled Stripe payments.
- **GET** `/api/v1/payments/stripe/checkout/success` - Handle successful Stripe payments.
- **POST** `/api/v1/payments/stripe/create-checkout-session` - Create a Stripe checkout session.
- **POST** `/api/v1/webhooks/stripe` - Handle Stripe webhooks.

#### User Management
- **POST** `/api/v1/users/register` - Register a new user.
- **POST** `/api/v1/users/login` - Login for users.
- **GET** `/api/v1/users/get-by-email` - Get user by email.
- **GET** `/api/v1/users/list` - Get a list of all users.
- **GET** `/api/v1/users/list-reservations/` - Get a list of user reservations.
- **GET** `/api/v1/users/reservation` - Fetch a user's reservation.
- **GET** `/api/v1/users/{id}` - Fetch user by ID.
- **PUT** `/api/v1/users/change-password` - Change the password of a user.
- **PUT** `/api/v1/users/reset-password` - Reset the password for a user.
- **DELETE** `/api/v1/users/account-deletion` - Delete a user account.
