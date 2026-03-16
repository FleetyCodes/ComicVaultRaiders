# Comic Vault Raiders

## Live Demo
🌐 **Online:** https://comicvaultraiders.eu/

## Description
A hobby web application to track and manage comic book collections.  
Users can register, manage their comics, and organize them by series, publishers, and formats.  
This is a study project with practical usage.

## Tech Stack
- **Backend:** Java 17, Spring Boot 3.4.5, Spring Data JPA, Spring Security, OpenAPI (Swagger UI), JUnit, log4j, Maven, PostgreSQL
- **Frontend:** Angular
- **Authentication and Authorization:** JWT
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
- Logout on inactivity
- Dynamic design on user interaction

## Planned
- Refact
- openapi with yaml structures and generated classes
- cross entity logic implementation
- REST API config
- statistics with aggregations
- unit tests, more integration tests
- sonarqube integration
- spring ai
- caching
- aws integration
- oauth2
- email function (with GDPR)
- deploy pipeline
- webflux (?)
- functions using datetime
- validations
- mapstruct
- global exception handling
- more http status
- docker, kubernetes
- statistics at the end of the year -> recap / monthly too
- UTC zone usage everywhere
- http 401 -> 5xx
- add original release date in grid
- finish wip pages
- modify existing comics
- create and config a demo user
- favourite comics -> notify anniversaries
- my comics & wishlist -> add more btn which shows more and more comics in the list (desktop view)
- finish this list from the local documentation :)


## Architecture Notes
- Layered architecture (Controller → Service → Repository)
- REST API
- DTO usage to separate API and persistence models
- JWT-based security and role management

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

