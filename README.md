# CineMitr Data Tracker

A comprehensive Spring Boot application with persistent H2 database backend and modern web UI for managing media content, tracking uploads, and monitoring statistics. Features advanced search functionality, multiple media support, automatic content-upload synchronization, and modern responsive design.

## ✨ Key Features

- **Advanced Media Management**: Track movies, web series, and documentaries with comprehensive metadata
- **Smart Content Catalog**: Manage download links, priorities, and status tracking with auto-sync
- **Intelligent Upload Management**: Monitor upload processes with automatic content mapping
- **Real-time Statistics Dashboard**: View analytics for views, subscribers, and interactions
- **🔍 Advanced Search & Filtering**: Powerful search across all management tables with multi-filter support
- **🔗 Multi-Media Support**: Handle multiple comma-separated media names in single entries
- **🔄 Bi-directional Sync**: Automatic content-to-upload synchronization with "New Content" status
- **📱 Modern Responsive UI**: Mobile-first design with Tailwind CSS and intuitive user experience
- **💾 Persistent Data Storage**: File-based H2 database that retains data across server restarts
- **🛡️ Data Integrity**: Unique media constraints and comprehensive validation system

## Technology Stack

### Backend
- **Spring Boot 2.7.18** - Main framework
- **Spring Data JPA** - Database abstraction layer
- **H2 Database** - File-based database for persistent storage
- **Spring Web** - REST API endpoints
- **Maven** - Dependency management and build tool

### Frontend
- **HTML5 + CSS3** - Structure and styling
- **Tailwind CSS** - Modern utility-first UI framework
- **Vanilla JavaScript** - Frontend logic and API communication
- **Fetch API** - HTTP requests to backend

## Project Structure

```
cine.mitr-data-tracker/
├── src/main/java/com/cinemitr/datatracker/
│   ├── DataTrackerApplication.java          # Main Spring Boot application
│   ├── controller/                          # REST API controllers
│   │   ├── MediaCatalogController.java
│   │   ├── ContentCatalogController.java
│   │   ├── UploadCatalogController.java
│   │   └── StatsCatalogController.java
│   ├── dto/                                 # Data Transfer Objects
│   │   ├── MediaCatalogDTO.java
│   │   ├── ContentCatalogDTO.java
│   │   ├── UploadCatalogDTO.java
│   │   ├── StatsCatalogDTO.java
│   │   └── MetadataStatusDTO.java           # NEW
│   ├── entity/                              # JPA Entities
│   │   ├── MediaCatalog.java               # Updated with unique constraints
│   │   ├── ContentCatalog.java
│   │   ├── UploadCatalog.java
│   │   ├── StatsCatalog.java
│   │   └── MetadataStatus.java             # Updated with PathCategory enum
│   ├── enums/                              # Enumerations
│   │   └── PathCategory.java               # NEW: MEDIA_FILE, CONTENT_FILE, UPLOADED_FILE
│   ├── repository/                         # Data repositories
│   │   ├── MediaCatalogRepository.java     # Enhanced with composite queries
│   │   ├── ContentCatalogRepository.java
│   │   ├── UploadCatalogRepository.java
│   │   ├── StatsCatalogRepository.java
│   │   └── MetadataStatusRepository.java
│   └── service/                            # Business logic layer
│       ├── MediaCatalogService.java        # Enhanced with metadata handling
│       ├── ContentCatalogService.java
│       ├── UploadCatalogService.java
│       └── StatsCatalogService.java
├── src/main/resources/
│   ├── application.properties              # Updated for persistent storage
│   ├── schema.sql                          # Updated database schema
│   ├── data.sql                           # Updated sample data
│   └── static/                            # Frontend files
│       ├── index.html                     # Main UI
│       └── app.js                         # JavaScript logic
├── data/                                  # NEW: H2 database files directory
│   ├── cinemitr-db.mv.db                 # Persistent database file
│   └── *.trace.db                        # Database trace files
├── Design/                               # Design files and documentation
│   ├── UI-Design-V3.html
│   ├── UI-Design-V4.html
│   └── sample_media.csv
└── pom.xml                               # Maven dependencies
```

## Database Schema

The application uses 5 main tables with enhanced relationships:

### 1. **metadata_status**
- Stores file and path metadata
- **NEW**: Uses PathCategory enum (MEDIA_FILE, CONTENT_FILE, UPLOADED_FILE)
- Supports automatic timestamp updates

### 2. **media_catalog** 
- Media information (movies, series, docs)
- **NEW**: Unique constraint on (media_name, media_type) combination
- **UPDATED**: Language field is now nullable
- Links to metadata_status for download paths

### 3. **content_catalog**
- Download links and content tracking
- References media_catalog and metadata_status

### 4. **upload_catalog**
- Upload process management
- Enhanced relationships with content and media catalogs

### 5. **states_catalog**
- Analytics and statistics data
- Daily metrics with page-wise tracking

## 🚀 Latest Features & Updates

### 🔍 **Advanced Search & Filtering System**
- **Multi-Table Search**: Comprehensive search functionality across Media, Content, and Upload management tables
- **Real-time Filtering**: Live search with 300ms debounce for optimal performance
- **Smart Column Mapping**: Intelligent search across all relevant table columns
- **Combined Filters**: Multiple dropdown filters work together with text search
- **Results Counter**: Dynamic display showing "X of Y results" with filter status
- **No Results State**: Beautiful empty state with clear filter options

#### **Media Management Search**:
- **Text Search**: Media name, type, language, genre, availability
- **Type Filter**: Movie, Web-Series, Documentary
- **Language Filter**: English, Hindi, Spanish, French, German, Japanese, Korean
- **Advanced Features**: Searches across all visible columns simultaneously

#### **Content Management Search**:
- **Text Search**: Link, media type, media name, status, priority, local status
- **Type Filter**: Movie, Web-Series, Documentary  
- **Status Filter**: Pending, Downloading, Downloaded, Completed, Error
- **Priority Filter**: Low, Medium, High, Urgent
- **Local Status Filter**: Available, Not Available, Local, Processing
- **Complete Coverage**: All 6 table columns fully searchable and filterable

#### **Upload Management Search**:
- **Text Search**: Source link, media name, source data, status, media format
- **Status Filter**: Pending, New Content, Completed, Downloaded, In Progress, Blocked, Ready to Upload, Uploaded
- **Media Type Filter**: Movie, Web-Series, Documentary (smart detection across multiple columns)
- **Format Filter**: HD Video, 4K Video, Full HD, Web-DL, BluRay, TV-Rip, Web-Rip
- **Intelligent Matching**: Media type detection across both media name and source data columns

### 🔗 **Multi-Media Support System**
- **Comma-Separated Entries**: Handle multiple media names in single form entry (e.g., "Movie A, Movie B, Movie C")
- **Automatic Media Creation**: Creates new media entries with default values for non-existing names
- **Duplicate Prevention**: Smart deduplication using synchronized blocks and unique constraints
- **Many-to-Many Relationships**: Enhanced entity relationships supporting multiple media per content/upload
- **Order Preservation**: Maintains input order using LinkedHashSet

### 🔄 **Bi-directional Synchronization**
- **Content-to-Upload Auto-Sync**: Adding content automatically creates corresponding upload entry
- **"New Content" Status**: Auto-generated uploads receive special "New Content" status
- **Intelligent Linking**: Automatic mapping between content links and upload source links
- **Cross-Service Communication**: Seamless data flow between ContentCatalogService and UploadCatalogService
- **Metadata Propagation**: Automatic transfer of media information and metadata

### 📝 **Enhanced Form Handling**
- **Optional Fields**: All form fields support empty values without forced defaults
- **Character Limits**: 9000-character metadata field with real-time character counter
- **Smart Validation**: Form validation that doesn't block empty optional fields
- **Dropdown Enhancements**: Pre-selected default values for better user experience
- **Error Resilience**: Graceful handling of empty inputs across all forms

### 🔄 **Data Persistence & Integrity**
- **File-based H2 Database**: Data persists across server restarts
- **Database Location**: `./data/cinemitr-db.mv.db`
- **Schema Management**: Uses `update` strategy to preserve existing data
- **Automatic Timestamps**: All entities support automatic created_at/updated_at
- **Enhanced Relationships**: Proper Many-to-Many mappings with junction tables

### 🔒 **Advanced Data Integrity**
- **Unique Media Constraint**: Prevents duplicate media with same name and type combination
- **PathCategory Enum**: Enforces valid path categories in MetadataStatus
- **Enhanced Validation**: Service-layer validation with descriptive error messages
- **Thread-Safe Operations**: Synchronized media creation prevents race conditions
- **Constraint Violation Handling**: Graceful error recovery with user-friendly messages

### 🎨 **Modern UI/UX Enhancements**
- **Responsive Design**: Mobile-first approach with Tailwind CSS
- **Search Containers**: Modern gray-themed search panels with proper spacing
- **Interactive Elements**: Hover states, focus rings, and smooth transitions
- **Loading States**: Professional loading indicators and empty states
- **Character Counters**: Real-time feedback with color-coded warnings
- **Grid Layouts**: Responsive grid systems adapting to different screen sizes

## API Endpoints

### Media Catalog
- `GET /api/media` - Get all media entries
- `POST /api/media` - Create new media (validates unique name+type)
- `PUT /api/media/{id}` - Update media entry
- `DELETE /api/media/{id}` - Delete media entry
- `GET /api/media/count` - Get total media count

### Content Catalog
- `GET /api/content` - Get all content entries
- `POST /api/content` - Create new content entry
- `PUT /api/content/{id}` - Update content entry
- `DELETE /api/content/{id}` - Delete content entry
- `GET /api/content/count` - Get total content count

### Upload Catalog
- `GET /api/upload` - Get all upload entries
- `POST /api/upload` - Create new upload entry
- `PUT /api/upload/{id}` - Update upload entry
- `DELETE /api/upload/{id}` - Delete upload entry
- `GET /api/upload/count` - Get total upload count

### Statistics
- `GET /api/states` - Get all statistics entries
- `POST /api/states` - Create new statistics entry
- `PUT /api/states/{id}` - Update statistics entry
- `DELETE /api/states/{id}` - Delete statistics entry
- `GET /api/states/count` - Get total statistics count

## Prerequisites

- **Java 8 or higher** (tested with Java 8, 11, 17, and 23)
- **Maven 3.6+ or use the included Maven wrapper**
- **Web browser** (Chrome, Firefox, Safari, Edge)

## How to Run

### Option 1: Using Maven Wrapper (Recommended)
```bash
# Navigate to project directory
cd cine.mitr-data-tracker

# For Windows
mvnw.cmd spring-boot:run

# For Linux/macOS
./mvnw spring-boot:run
```

### Option 2: Using Maven (if installed)
```bash
# Navigate to project directory
cd cine.mitr-data-tracker

# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run
```

### Option 3: Using Pre-built JAR
```bash
# Build the JAR
./mvnw clean install

# Run the JAR directly
java -jar target/data-tracker-1.0.0.jar
```

### Option 4: Using IDE
1. Open the project in your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Run `DataTrackerApplication.java` as a Java application

## Accessing the Application

Once the application starts:

1. **Frontend UI**: http://localhost:8081
2. **H2 Database Console**: http://localhost:8081/h2-console
   - JDBC URL: `jdbc:h2:file:./data/cinemitr-db`
   - Username: `sa`
   - Password: `password`
3. **API Base URL**: http://localhost:8081/api

## Configuration

### Application Properties (application.properties)
```properties
# Server Configuration
server.port=8081

# Database Configuration - File-based H2 for data persistence
spring.datasource.url=jdbc:h2:file:./data/cinemitr-db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password

# H2 Console Configuration
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# SQL Initialization - Only run if database is empty
spring.sql.init.mode=embedded
spring.sql.init.data-locations=classpath:data.sql
spring.jpa.defer-datasource-initialization=true
```

## Sample Data

The application comes with sample data including:
- 2 metadata status entries with MEDIA_FILE category
- 2 media entries (The Dark Knight, Sample Movie)
- 2 content links with different statuses and priorities
- 2 upload records with processing status
- 2 statistics entries with sample analytics data

## Features Overview

### 1. **Media Management**
- Add, edit, and delete media entries
- **Unique validation** prevents duplicate media with same name and type
- Track download status and file paths via MetadataStatus integration
- Organize by type, language, and genre
- Monitor availability across platforms

### 2. **Content Catalog**
- Manage download links and priorities
- Track download status and local file paths
- Set priority levels (High, Medium, Low)
- Status tracking: NEW, DOWNLOADED, ERROR
- Monitor content processing status

### 3. **Upload Management**
- Track upload processes and their status
- Link uploads to source content and media
- Status options: COMPLETED, DOWNLOADED, IN-PROGRESS, BLOCKED, READY-TO-UPLOAD, UPLOADED
- Monitor upload lifecycle from source to completion

### 4. **Statistics Dashboard**
- Daily analytics tracking with date-based entries
- View counts, subscribers, and interactions metrics
- Page-wise statistics (CINE.MITR, CINE.MITR.MUSIC)
- Historical data with unique date+page constraints

### 5. **Real-time Dashboard**
- Live count updates for all categories
- Quick overview of system status
- Responsive grid layout
- Auto-refreshing data display

## Data Persistence

### Database Storage
- **Location**: `./data/cinemitr-db.mv.db`
- **Type**: File-based H2 database
- **Persistence**: Data survives server restarts, stops, and crashes
- **Backup**: Database files can be copied for backup purposes

### Schema Evolution
- **Strategy**: `hibernate.ddl-auto=update`
- **Benefits**: Preserves existing data while applying schema changes
- **Migration**: Automatic handling of new columns and constraints

### Version Control
- Database files are excluded via `.gitignore`
- Only schema (`schema.sql`) and sample data (`data.sql`) are versioned
- Each developer maintains their own local database

## Error Handling

### Validation Errors
- **Unique Constraint Violations**: Clear error messages for duplicate media
- **Required Field Validation**: Descriptive messages for missing data
- **Enum Validation**: PathCategory values are strictly enforced

### Database Errors
- **Connection Issues**: Graceful handling with fallback to null values
- **Constraint Violations**: User-friendly error messages
- **Transaction Management**: Automatic rollback on errors

## Troubleshooting

### Common Issues

1. **Port 8081 already in use**
   ```properties
   # Change port in application.properties
   server.port=8082
   ```

2. **Database file access issues**
   - Ensure write permissions in project directory
   - Check if `./data/` directory exists
   - Verify H2 database files aren't locked by another process

3. **Unique constraint violations**
   - Media names must be unique per media type
   - Example: "Avatar" can exist as both "Movie" and "Series"

4. **API calls failing**
   - Check browser console for CORS or network errors
   - Verify backend is running on http://localhost:8081
   - Ensure database is accessible

### Logs and Debugging
```properties
# Enable debug logging
logging.level.com.cinemitr=DEBUG
logging.level.org.springframework.web=DEBUG
```

## Development Notes

### Database Development
- H2 Console available for direct database access
- SQL queries can be tested directly in H2 Console
- Database schema automatically created from entities
- Sample data loaded only on first run (embedded mode)

### Entity Relationships
- **MetadataStatus**: Referenced by MediaCatalog, ContentCatalog, UploadCatalog
- **MediaCatalog**: Referenced by ContentCatalog, UploadCatalog
- **ContentCatalog**: Referenced by UploadCatalog, StatsCatalog
- All relationships use proper foreign keys with cascade options

### Enum Usage
```java
// PathCategory enum values
MEDIA_FILE("Media_file")
CONTENT_FILE("Content_file") 
UPLOADED_FILE("Uploaded_file")
```

## Future Enhancements

- **Database Migration**: Move to PostgreSQL/MySQL for production
- **File Upload**: Direct file upload functionality
- **Batch Operations**: Bulk import/export capabilities
- **Advanced Search**: Full-text search and complex filtering
- **User Management**: Authentication and authorization
- **API Documentation**: Swagger/OpenAPI integration
- **Monitoring**: Application metrics and health checks
- **Caching**: Redis integration for performance
- **Testing**: Comprehensive unit and integration tests
- **Containerization**: Docker support for deployment

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is part of the CineMitr codebase and follows the project's licensing terms.

---

**Last Updated**: September 2025
**Version**: 1.0.0
**Spring Boot**: 2.7.18
**Java Compatibility**: 8+