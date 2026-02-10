# Comic Vault Raiders

## Live Demo
🌐 **Online:** https://comicvaultraiders.eu/
Try logging in or register to explore the comic collection features!

## Description
A hobby web application to track and manage comic book collections.  
Users can register, manage their comics, and organize them by series, publishers, and formats.  
This is a study project with practical usage.

## Tech Stack
- **Backend:** Java 17, Spring Boot 3.4.5, Spring Data JPA, Spring Security, OpenAPI (Swagger UI), JUnit, log4j, Maven, PostgreSQL
- **Frontend:** Angular
- **Authentication:** JWT
- **Database for testing:** H2 (test profile)
- **Deployment:** Hosted at [comicvaultraiders.eu](https://comicvaultraiders.eu/)

## Features
- User registration and login
- Comic CRUD operations
- Series and publisher management
- Search and filtering
- Scheduled jobs
- Pagination
- Role-based access
- Local API documentation via Swagger UI

## Architecture Notes
- Layered architecture (Controller → Service → Repository)
- REST API
- DTO usage to separate API and persistence models
- JWT-based security and role management
- Unit tests with JUnit

## Environment Variables
The following environment variables should be set before running the application:

| Variable | Description |
|----------|-------------|
| `DB_URL` | JDBC URL for PostgreSQL (default: `jdbc:postgresql://localhost:5432/postgres`) |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `SECRET_KEY` | Application secret key |
| `JWT_SECRET` | JWT signing key |
| `GOOGLE_API_KEY` | API key for Google Books API |
| `HTTPS_KEY` | Optional: password for SSL keystore if HTTPS is enabled |

## How to Run Locally
1. Clone the repository  
2. Set up PostgreSQL database and configure environment variables or `.env` file  
3. Run the backend via Spring Boot (`mvn spring-boot:run`)  
4. Run the frontend via Angular (`ng serve`)  
5. Access the application at `http://localhost:4200`

## API Documentation
Accessible at `/swagger-ui.htm
