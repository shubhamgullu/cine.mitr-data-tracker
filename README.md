# CineMitr Data Tracker

A Spring Boot application with H2 database backend and modern web UI for managing media content, tracking uploads, and monitoring statistics.

## Features

- **Media Management**: Track movies, web series, and documentaries with metadata
- **Content Catalog**: Manage download links, priorities, and status tracking
- **Upload Management**: Monitor upload processes and status
- **Statistics Dashboard**: View analytics for views, subscribers, and interactions
- **Real-time Dashboard**: Live counts and overview of all data

## Technology Stack

### Backend
- **Spring Boot 2.7.18** - Main framework (Java 8 compatible)
- **Spring Data JPA** - Database abstraction
- **H2 Database** - In-memory database for development
- **Spring Web** - REST API endpoints
- **Maven** - Dependency management

### Frontend
- **HTML5 + CSS3** - Structure and styling
- **Tailwind CSS** - Modern UI framework
- **Vanilla JavaScript** - Frontend logic and API communication
- **Fetch API** - HTTP requests to backend

## Project Structure

```
cine.mitr-data-tracker/
├── src/main/java/com/cinemitr/datatracker/
│   ├── DataTrackerApplication.java          # Main Spring Boot application
│   ├── config/
│   │   └── CorsConfig.java                  # CORS configuration
│   ├── controller/                          # REST API controllers
│   │   ├── MediaCatalogController.java
│   │   ├── ContentCatalogController.java
│   │   ├── UploadCatalogController.java
│   │   └── StatsCatalogController.java
│   ├── dto/                                 # Data Transfer Objects
│   │   ├── MediaCatalogDTO.java
│   │   ├── ContentCatalogDTO.java
│   │   ├── UploadCatalogDTO.java
│   │   └── StatsCatalogDTO.java
│   ├── entity/                              # JPA Entities
│   │   ├── MediaCatalog.java
│   │   ├── ContentCatalog.java
│   │   ├── UploadCatalog.java
│   │   ├── StatsCatalog.java
│   │   └── MetadataStatus.java
│   ├── repository/                          # Data repositories
│   │   ├── MediaCatalogRepository.java
│   │   ├── ContentCatalogRepository.java
│   │   ├── UploadCatalogRepository.java
│   │   ├── StatsCatalogRepository.java
│   │   └── MetadataStatusRepository.java
│   └── service/                             # Business logic layer
│       ├── MediaCatalogService.java
│       ├── ContentCatalogService.java
│       ├── UploadCatalogService.java
│       └── StatsCatalogService.java
├── src/main/resources/
│   ├── application.yml                      # Spring Boot configuration
│   ├── schema.sql                           # Database schema (H2)
│   ├── data.sql                            # Sample data
│   └── static/                             # Frontend files
│       ├── index.html                      # Main UI
│       └── app.js                          # JavaScript logic
├── Design/V1/                              # Original design files
│   ├── UI Design-V1.html                   # Original UI design
│   └── Table-schema.sql                    # Original MySQL schema
└── pom.xml                                 # Maven dependencies
```

## Database Schema

The application uses 5 main tables:

1. **metadata_status** - File and path metadata
2. **media_catalog** - Media information (movies, series, docs)
3. **content_catalog** - Download links and content tracking
4. **upload_catalog** - Upload process management
5. **stats_catalog** - Analytics and statistics data

## API Endpoints

### Media Catalog
- `GET /api/media` - Get all media
- `POST /api/media` - Create new media
- `PUT /api/media/{id}` - Update media
- `DELETE /api/media/{id}` - Delete media
- `GET /api/media/count` - Get media count

### Content Catalog
- `GET /api/content` - Get all content
- `POST /api/content` - Create new content
- `PUT /api/content/{id}` - Update content
- `DELETE /api/content/{id}` - Delete content
- `GET /api/content/count` - Get content count

### Upload Catalog
- `GET /api/upload` - Get all uploads
- `POST /api/upload` - Create new upload
- `PUT /api/upload/{id}` - Update upload
- `DELETE /api/upload/{id}` - Delete upload
- `GET /api/upload/count` - Get upload count

### Statistics
- `GET /api/states` - Get all statistics
- `POST /api/states` - Create new statistics
- `PUT /api/states/{id}` - Update statistics
- `DELETE /api/states/{id}` - Delete statistics
- `GET /api/states/count` - Get statistics count

## Prerequisites

- **Java 8 or higher** (tested with Java 8, 11, 17, and 23)
- **Maven 3.6+ or use the included Maven wrapper**  
- **Web browser** (Chrome, Firefox, Safari, Edge)

## How to Run

### Option 1: Using Maven (if installed)
```bash
# Navigate to project directory
cd cine.mitr-data-tracker

# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run
```

### Option 2: Using Maven Wrapper
```bash
# Navigate to project directory
cd cine.mitr-data-tracker

# For Windows
mvnw.cmd spring-boot:run

# For Linux/macOS  
./mvnw spring-boot:run
```

### Option 3: Using Pre-built JAR
```bash
# Navigate to project directory
cd cine.mitr-data-tracker

# Build the JAR (if not already built)
./mvnw clean install

# Run the JAR directly
java -jar target/data-tracker-1.0.0.jar
```

### Option 4: Using IDE
1. Open the project in your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Run `DataTrackerApplication.java` as a Java application

## Accessing the Application

Once the application starts:

1. **Frontend UI**: http://localhost:8080
2. **H2 Database Console**: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: `password`
3. **API Base URL**: http://localhost:8080/api

## Features Overview

### 1. Media Management
- Add, edit, and delete media entries
- Track download status and file paths
- Organize by type, language, and genre
- Monitor availability across platforms

### 2. Content Catalog
- Manage download links and priorities
- Track download status and local file paths
- Set priority levels (High, Medium, Low)
- Monitor content processing status

### 3. Upload Management
- Track upload processes and their status
- Link uploads to source content
- Monitor media data and metadata
- Status tracking through upload lifecycle

### 4. Statistics Dashboard
- Daily analytics tracking
- View counts, subscribers, and interactions
- Page-wise statistics (cine.mitr, cine.mitr.music)
- Historical data visualization

### 5. Real-time Dashboard
- Live count updates for all categories
- Quick overview of system status
- Responsive grid layout
- Auto-refreshing data

## Development Notes

### Database
- Uses H2 in-memory database for development
- Schema is created automatically on startup
- Sample data is loaded from `data.sql`
- Database persists only during application runtime

### Frontend
- Pure HTML/CSS/JavaScript (no frameworks)
- Responsive design with Tailwind CSS
- RESTful API communication
- Modal-based forms for data entry
- Real-time data updates

### Backend
- RESTful API design
- Proper error handling and validation
- CORS enabled for cross-origin requests
- Layered architecture (Controller → Service → Repository)
- JPA entities with relationships

## Configuration

### Application Properties (application.properties)
```properties
# Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=password

# H2 Console
spring.h2.console.enabled=true

# JPA Configuration
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create-drop

# Server Configuration
server.port=8080
```

## Sample Data

The application comes with sample data including:
- 2 media entries (The Dark Knight, Sample Movie)
- 2 content links with different statuses
- 2 upload records with processing status
- 2 statistics entries with sample analytics data

## Troubleshooting

### Common Issues

1. **Port 8080 already in use**
   - Change port in `application.yml`: `server.port: 8081`

2. **Java version issues**
   - Ensure Java 17+ is installed and JAVA_HOME is set

3. **API calls failing**
   - Check browser console for CORS or network errors
   - Verify backend is running on http://localhost:8080

4. **Database access issues**
   - H2 console: http://localhost:8080/h2-console
   - Use connection details from application.yml

### Logs
- Application logs are displayed in the console
- Enable debug logging by setting `logging.level.com.cinemitr: DEBUG`

## Future Enhancements

- Persistent database (PostgreSQL/MySQL)
- User authentication and authorization
- File upload functionality
- Advanced search and filtering
- Export functionality (CSV, PDF)
- Email notifications
- Scheduled tasks and automation
- API documentation with Swagger
- Unit and integration tests
- Docker containerization

## License

This project is part of the CineMitr codebase and follows the project's licensing terms.