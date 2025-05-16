# Deep Detect - Graduation Project

## Project Overview
Deep Detect is a sophisticated backend application built with Spring Boot that provides advanced detection and analysis capabilities. The project implements a secure, scalable, and maintainable architecture following modern software development practices.

## Academic Information
- **University**: Tanta University
- **Faculty**: Faculty of Computer and Information Technology
- **Supervisor**: Dr. Aida Nasr

## Technical Stack
- **Backend Framework**: Spring Boot 3.4.0
- **Language**: Java 17
- **Database**: 
  - PostgreSQL (Production)
  - H2 Database (Development/Testing)
- **Security**: Spring Security with JWT Authentication
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Cloud Storage**: Digital Ocean Spaces (S3-compatible)
- **Build Tool**: Maven
- **Containerization**: Docker
- **Deployment**: Digital Ocean Droplet

## Key Features
- RESTful API Architecture
- Secure Authentication and Authorization
- JWT-based Security
- Database Integration with JPA
- API Documentation with Swagger
- Cloud Storage Integration with Digital Ocean Spaces
- Comprehensive Error Handling
- Actuator for Application Monitoring
- Production Deployment on Digital Ocean

## Project Structure
```
src/main/java/com/validata/deepdetect/
├── controller/    # REST API Controllers
├── service/       # Business Logic Layer
├── repository/    # Data Access Layer
├── model/         # Entity Classes
├── dto/           # Data Transfer Objects
├── config/        # Configuration Classes
├── security/      # Security Configuration
├── exception/     # Custom Exception Handling
└── mapper/        # Object Mapping
```

## Prerequisites
- Java 17 or higher
- Maven
- Docker and Docker Compose
- PostgreSQL (for production)

## Getting Started

### Local Development Setup
1. Clone the repository
2. Configure your database settings in `application.properties`
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Docker Setup
1. Build and run using Docker Compose:
   ```bash
   docker-compose up --build
   ```

### Production Deployment
The application is deployed on Digital Ocean Droplet with the following specifications:
- Ubuntu Server
- Docker and Docker Compose for containerization
- Nginx as reverse proxy
- SSL/TLS encryption
- Digital Ocean Spaces for object storage

## API Documentation
The API documentation is available at:
```
http://159.89.10.1:8080/swagger-ui/index.html#/
```

## Security
The application implements JWT-based authentication and authorization. Make sure to:
1. Configure your JWT secret in the application properties
2. Use proper security headers in your requests
3. Follow the security best practices for token management
4. Keep your Digital Ocean API keys and credentials secure

## Acknowledgments
- Dr. Aida Nasr for her guidance and supervision
- Tanta University Faculty of Computer and Information Technology
- All contributors and supporters of this project
