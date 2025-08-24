# Cine Mitr - Movie Instagram Dashboard

A modern movie Instagram dashboard built with Spring Boot, Thymeleaf, and Bootstrap. Cine Mitr allows you to manage and track your movie-related Instagram links in one centralized dashboard.

## 🎬 Features

- **Instagram Link Management**: Add, edit, and delete movie Instagram links with ease
- **Dark Theme Dashboard**: Modern dark blue theme with CSS custom properties
- **Category Organization**: Organize links by movie genres (Action, Comedy, Drama, Horror, Romance, Thriller, Science Fiction, Fantasy, Documentary, Animation)
- **Analytics Tracking**: Track views and clicks on Instagram links automatically  
- **Responsive Design**: Mobile-first responsive interface using Bootstrap 5
- **Search & Filter**: Find links by movie name or category
- **Modern UI**: Beautiful dark theme design with smooth animations and transitions
- **Edit Modal**: In-place editing with Bootstrap modal dialogs
- **Click Tracking**: AJAX-powered click tracking for Instagram links
- **Database Integration**: JPA/Hibernate with H2 database and comprehensive schema

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
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: *(leave empty)*

## 🎯 Usage Guide

### Dashboard Features

1. **Homepage**: View total Instagram links count and recent links
2. **Add Entry Tab**: Add new movie Instagram links with form validation
3. **Dashboard Tab**: View, edit, and delete existing Instagram links
4. **Analytics**: Track view counts and click counts for each link
5. **Categories**: Organize links by predefined movie genres
6. **Click Tracking**: Automatic click tracking when users access Instagram links
7. **Edit Modal**: Edit link details using Bootstrap modal dialogs
8. **Responsive Design**: Works perfectly on desktop, tablet, and mobile devices

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
spring.datasource.url=jdbc:h2:mem:testdb
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
│   │   │   ├── config/          # Security configuration
│   │   │   ├── controller/      # Web controllers
│   │   │   ├── model/           # Entity classes
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── service/         # Business logic
│   │   │   └── CineMitrApplication.java
│   │   └── resources/
│   │       ├── static/          # CSS, JS, Images
│   │       ├── templates/       # Thymeleaf templates
│   │       └── application.properties
│   └── test/                    # Test classes
├── target/                      # Build output
├── pom.xml                     # Maven configuration
├── .gitignore                  # Git ignore rules
└── README.md                   # This file
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
- `GET /dashboard` - Main dashboard with tabs (Add Entry/Dashboard)
- `POST /dashboard/add-link` - Create new Instagram link
- `POST /dashboard/link/{id}/edit` - Edit existing Instagram link
- `POST /dashboard/link/{id}/delete` - Delete Instagram link
- `POST /dashboard/link/{id}/click` - Track click analytics (AJAX)
- `GET /dashboard/links` - View all links with search/filter
- `GET /dashboard/link/{id}` - View specific link details

### API Response Formats
- **Success**: Returns redirect with flash message
- **Error**: Returns error message with form validation
- **AJAX**: Returns simple text response for click tracking

## 🔒 Security Features

- **Form Validation**: Server-side validation for all form inputs
- **URL Validation**: Instagram URL format validation
- **SQL Injection Prevention**: JPA/Hibernate parameter binding
- **XSS Protection**: Thymeleaf automatic HTML escaping
- **Input Sanitization**: Proper data validation and sanitization

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
# Verify JDBC URL: jdbc:h2:mem:testdb
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