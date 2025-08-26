# Cine Mitr - Comprehensive Media Catalog Management System

A modern, comprehensive media catalog management system built with Spring Boot, Thymeleaf, and Bootstrap. Cine Mitr provides a centralized dashboard to manage media catalogs, content tracking, upload operations, user analytics, and Instagram links with advanced search, CRUD functionality, and real-time table management.

## 🎬 Features

### 📊 Multi-Catalog Management
- **Media Catalog**: Manage movies, albums, web series, documentaries with platform details, download status, and metadata
- **Content Catalog**: Track content links with status management, priority levels, and upload integration
- **Upload Catalog**: Handle upload operations with content linking and status tracking
- **States Catalog**: Comprehensive user analytics with interactive table interface, bulk operations, and engagement metrics tracking
- **Instagram Link Management**: Traditional Instagram link management with category organization

### 🎨 Enhanced User Experience
- **Unified Interface**: Consistent Add/Update/Search functionality across all catalog tabs
- **Hidden Forms**: Clean interface with forms appearing only when needed
- **Recent Records Display**: Shows last 10 records for each catalog with edit/delete actions
- **Direct Tab Access**: URL endpoints to navigate directly to specific catalog tabs
- **Dark Theme Dashboard**: Modern dark blue theme with CSS custom properties
- **Responsive Design**: Mobile-first responsive interface using Bootstrap 5

### ⚡ Advanced Functionality
- **CRUD Operations**: Full Create, Read, Update, Delete operations for all catalogs
- **Interactive Tables**: Real-time table management with sort, search, and bulk operations
- **Bulk Operations**: Multi-select delete functionality across all catalog types
- **Search & Filter**: Advanced search functionality with multiple filter criteria
- **Real-time Updates**: AJAX-powered interactions for seamless user experience
- **Analytics Tracking**: Comprehensive tracking for views, clicks, and engagement metrics
- **Database Integration**: JPA/Hibernate with H2 database and complete audit trail
- **Form Validation**: Client and server-side validation with proper error handling
- **Java 8 Compatibility**: Backward compatible implementation with proper error handling

## 🛠️ Technology Stack

- **Backend**: Spring Boot 2.7.18, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap 5, jQuery, Font Awesome
- **Database**: H2 (Development), MySQL (Production ready)  
- **Build Tool**: Maven
- **Java Version**: 8+

## 📋 Prerequisites

Before running this project, make sure you have:

- **Java 8** or higher installed
- **Maven 3.6+** installed
- **Git** (for cloning the repository)
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Verify Prerequisites

```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check Git version
git --version
```

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd cine.mitr-data-tracker
```

### 2. Build the Project

```bash
# Clean and compile the project
mvn clean compile

# Run tests
mvn test

# Package the application
mvn package
```

### 3. Run the Application

#### Option A: Using Maven
```bash
mvn spring-boot:run
```

#### Option B: Using Java JAR
```bash
# First, build the JAR file
mvn clean package

# Run the JAR file
java -jar target/cine-mitr-1.0.0.jar
```

#### Option C: Using IDE
- Import the project as a Maven project
- Run the main class: `com.cinemitr.CineMitrApplication`

### 4. Access the Application

Once the application starts successfully:

- **Main Application**: http://localhost:8080
- **H2 Database Console**: http://localhost:8080/h2-console

#### H2 Database Connection Details:
- **JDBC URL**: `jdbc:h2:file:./data/cinemitr`
- **Username**: `sa`
- **Password**: *(leave empty)*

## 🎯 Usage Guide

### Dashboard Features

#### 📋 Catalog Management Tabs

1. **Media Catalog Tab** (`/dashboard/media-catalog`)
   - Manage movies, albums, web series, documentaries
   - Platform tracking (Netflix, Amazon, Zee5, YouTube, etc.)
   - Download status management (Downloaded, Not Downloaded, Partially Downloaded)
   - Location and metadata storage
   - Fun facts and descriptions
   - Add/Update/Search functionality with recent records display

2. **Content Catalog Tab** (`/dashboard/content-catalog-tab`)
   - Content link management with media catalog integration
   - Status tracking (New, Downloaded, Error, In Progress)
   - Priority levels (High, Medium, Low)
   - Metadata and location management
   - Like states and comment tracking
   - Upload content status integration
   - Advanced search with multiple filters

3. **Upload Catalog Tab** (`/dashboard/upload-catalog-tab`)
   - Upload operation management
   - Content catalog link integration
   - Storage location tracking
   - Upload status monitoring
   - Caption and metadata management
   - Bulk upload operations support

4. **States Catalog Tab** (`/dashboard/states-catalog`)
   - **Interactive Table Interface**: Real-time table with sortable columns and bulk selection
   - **Analytics Management**: Complete CRUD operations for analytics entries
   - **Bulk Operations**: Multi-select delete functionality with confirmation
   - **Views, subscribers, and interactions tracking**
   - **Reach and impressions metrics**
   - **Profile visits and contact clicks monitoring**
   - **Follower growth analysis** (gained/lost tracking)
   - **Content performance metrics** (reels, stories count)
   - **Engagement rate calculations** with percentage display
   - **Add/Edit Forms**: Collapsible forms with all analytics fields
   - **Real-time Updates**: AJAX-powered table refresh and data management

5. **Dashboard Tab** (`/dashboard`)
   - Instagram link management
   - Category organization by genres
   - Click and view tracking
   - Link validation and management

#### 🎯 Enhanced User Interface Features

6. **Consistent UX Pattern**: All tabs feature the same interface pattern:
   - Action bar with Add New/Update/Search buttons
   - Hidden forms that appear only when "Add New" is clicked
   - Recent records table (last 10 entries) sorted by latest updates
   - Inline edit/delete functionality
   - Real-time search and filtering

7. **Direct Tab Navigation**: 
   - URL-based tab access for bookmarking and sharing
   - Auto-activation of specific tabs via URL parameters
   - Seamless navigation between catalog types

8. **Advanced Search & Filtering**:
   - Multi-criteria search across all catalog types
   - Real-time filtering with AJAX updates
   - Search by name, type, status, priority, and custom fields

9. **Responsive Design**: Optimized for desktop, tablet, and mobile devices with consistent dark theme

### Dark Theme UI

1. **Modern Design**: Dark blue color scheme with CSS custom properties
2. **Smooth Animations**: CSS transitions and fade-in effects
3. **Professional Layout**: Clean table layout with hover effects
4. **Mobile Responsive**: Adapts beautifully to all screen sizes
5. **Accessibility**: Focus indicators and proper color contrast

## 🔧 Configuration

### Database Configuration

#### Development (H2 - Default)
```properties
spring.datasource.url=jdbc:h2:file:./data/cinemitr
spring.datasource.username=sa
spring.datasource.password=
```

#### Production (MySQL)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cinemitr
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

### Application Properties

Key configuration properties in `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Thymeleaf Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# Static Resources Configuration
spring.web.resources.static-locations=classpath:/static/
spring.web.resources.cache.period=3600
```

## 📁 Project Structure

```
cine-mitr/
├── src/
│   ├── main/
│   │   ├── java/com/cinemitr/
│   │   │   ├── config/          # JPA and security configuration
│   │   │   ├── controller/      # Web and API controllers
│   │   │   │   ├── InstagramLinkController.java    # Main dashboard controller
│   │   │   │   ├── MediaCatalogController.java     # Media catalog API
│   │   │   │   ├── ContentCatalogController.java   # Content catalog API
│   │   │   │   ├── UploadCatalogController.java    # Upload catalog API
│   │   │   │   └── StatesCatalogController.java    # States catalog API
│   │   │   ├── model/           # Entity classes with audit trail
│   │   │   │   ├── BaseEntity.java                 # Base entity with audit fields
│   │   │   │   ├── MediaCatalog.java               # Media catalog entity
│   │   │   │   ├── ContentCatalog.java             # Content catalog entity
│   │   │   │   ├── UploadCatalog.java              # Upload catalog entity
│   │   │   │   ├── StatesCatalog.java              # States catalog entity
│   │   │   │   └── MovieInstagramLink.java         # Instagram link entity
│   │   │   ├── repository/      # JPA repositories with custom queries
│   │   │   ├── service/         # Business logic services
│   │   │   └── CineMitrApplication.java
│   │   └── resources/
│   │       ├── static/          # CSS, JS, Images
│   │       │   ├── css/
│   │       │   │   ├── dark-theme.css              # Dark theme styles
│   │       │   │   └── style.css                   # Main styles
│   │       │   └── js/
│   │       │       └── dashboard.js                # Dashboard functionality
│   │       ├── templates/       # Thymeleaf templates
│   │       │   └── dashboard/
│   │       │       └── index.html                  # Main dashboard template
│   │       └── application.properties
│   └── test/                    # Test classes
├── database/                    # Database schema and migration files
│   ├── schema.sql              # Complete database schema
│   └── migration.sql           # Database migration scripts
├── data/                       # H2 database files (created at runtime)
├── target/                     # Build output
├── pom.xml                     # Maven configuration
├── .gitignore                  # Git ignore rules
└── README.md                   # This documentation
```

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=MovieServiceTest
```

### Run with Coverage
```bash
mvn clean test jacoco:report
```

## 📝 API Endpoints

### Dashboard Endpoints
- `GET /` - Homepage with Instagram links overview
- `GET /dashboard` - Main dashboard with tabs (Media Catalog/Content Catalog/Upload Catalog/States Catalog/Dashboard)
- `POST /dashboard/add-link` - Create new Instagram link
- `POST /dashboard/link/{id}/edit` - Edit existing Instagram link
- `POST /dashboard/link/{id}/delete` - Delete Instagram link
- `POST /dashboard/link/{id}/click` - Track click analytics (AJAX)
- `GET /dashboard/links` - View all links with search/filter
- `GET /dashboard/link/{id}` - View specific link details

### Catalog Management Endpoints

#### Media Catalog API
- `GET /api/media-catalog` - List all media catalog entries with pagination and sorting
- `GET /api/media-catalog/{id}` - Get specific media catalog entry
- `POST /api/media-catalog` - Create new media catalog entry
- `PUT /api/media-catalog/{id}` - Update existing media catalog entry
- `DELETE /api/media-catalog/{id}` - Delete media catalog entry
- `GET /api/media-catalog/search` - Search media catalog with filters

#### Content Catalog API
- `GET /api/content-catalog` - List all content catalog entries
- `GET /api/content-catalog/{id}` - Get specific content catalog entry
- `POST /dashboard/content-catalog` - Create new content catalog entry
- `POST /dashboard/content-catalog/{id}/edit` - Update content catalog entry
- `DELETE /api/content-catalog/{id}` - Delete content catalog entry
- `GET /api/content-catalog/search` - Advanced search with multiple filters

#### Upload Catalog API
- `GET /api/upload-catalog` - List all upload catalog entries
- `GET /api/upload-catalog/{id}` - Get specific upload catalog entry
- `POST /dashboard/upload-catalog` - Create new upload catalog entry
- `POST /dashboard/upload-catalog/{id}/edit` - Update upload catalog entry
- `DELETE /api/upload-catalog/{id}` - Delete upload catalog entry

#### States Catalog API
- `GET /api/states-catalog` - List all analytics entries with sorting
- `POST /dashboard/states-catalog` - Create new analytics entry
- `POST /dashboard/states-catalog/{id}/edit` - Update existing analytics entry
- `DELETE /api/states-catalog/bulk-delete` - Bulk delete analytics entries
- `GET /dashboard/states-catalog` - Direct tab access with table interface

#### Direct Tab Access Endpoints
- `GET /dashboard/media-catalog` - Open directly to Media Catalog tab
- `GET /dashboard/content-catalog-tab` - Open directly to Content Catalog tab  
- `GET /dashboard/upload-catalog-tab` - Open directly to Upload Catalog tab
- `GET /dashboard/states-catalog` - Open directly to States Catalog tab

### API Response Formats
- **Success**: Returns redirect with flash message or JSON response
- **Error**: Returns error message with form validation details
- **AJAX**: Returns JSON response for search and real-time updates
- **Direct Access**: Auto-activates specific tabs based on URL parameters

## 🔒 Security Features

- **Form Validation**: Server-side validation for all form inputs
- **URL Validation**: Instagram URL format validation
- **SQL Injection Prevention**: JPA/Hibernate parameter binding
- **XSS Protection**: Thymeleaf automatic HTML escaping
- **Input Sanitization**: Proper data validation and sanitization

## 📋 Recent Updates

### v1.2.0 - Enhanced States Catalog & Fixes (Latest)
- ✅ **States Catalog Table Interface**: Complete table functionality similar to Media Catalog
- ✅ **Interactive Analytics Management**: Add/Edit/Delete operations with real-time updates
- ✅ **Bulk Operations**: Multi-select delete functionality for analytics entries
- ✅ **Java 8 Compatibility**: Fixed `Map.of()` compilation errors with custom helper methods
- ✅ **Enhanced API Endpoints**: Added REST API endpoints for States Catalog operations
- ✅ **Improved UX**: Consistent interface pattern across all catalog types
- ✅ **Form Validation**: Enhanced client-side and server-side validation
- ✅ **Error Handling**: Improved error messaging and exception handling

### Key Technical Fixes
- Fixed Java 8 compatibility issues by replacing `Map.of()` with custom `createMap()` helper
- Added missing BigDecimal import for States Catalog controller
- Enhanced repository methods for States Catalog with proper sorting
- Implemented bulk delete API endpoints for all catalog types
- Added real-time table refresh and AJAX functionality

### Development Improvements
- Added comprehensive logging configuration ready
- Updated SQL scripts for States Catalog table operations
- Enhanced form handling with proper validation and error display
- Improved table responsiveness and mobile compatibility

## 🐛 Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Kill process using port 8080
netstat -ano | findstr :8080
taskkill /PID <PID_NUMBER> /F

# Or change port in application.properties
server.port=8081
```

#### Database Connection Issues
```bash
# Check H2 console at: http://localhost:8080/h2-console
# Verify JDBC URL: jdbc:h2:file:./data/cinemitr
# Database files will be created in ./data/ directory
```

#### Maven Build Issues
```bash
# Clean and reinstall dependencies
mvn clean install -U
```

#### Template Not Found Errors
```bash
# Ensure Thymeleaf templates are in src/main/resources/templates/
# Check template names match controller return values
```

## 🚀 Deployment

### Development
```bash
mvn spring-boot:run
```

### Production (JAR)
```bash
mvn clean package
java -jar target/cine-mitr-1.0.0.jar --spring.profiles.active=prod
```

### Docker (Optional)
```dockerfile
FROM openjdk:17-jre-slim
COPY target/cine-mitr-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Your Name** - *Initial work* - [YourGitHub](https://github.com/yourusername)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Bootstrap team for the responsive CSS framework
- Thymeleaf team for the templating engine
- Font Awesome for the beautiful icons

## 📞 Support

If you encounter any issues or have questions:

1. Check the [Issues](https://github.com/yourusername/cine-mitr/issues) page
2. Create a new issue with detailed description
3. Contact: your-email@example.com

---

**Happy Coding! 🎬🍿**