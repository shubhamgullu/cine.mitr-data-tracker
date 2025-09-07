@echo off
echo ========================================
echo CineMitr Data Tracker Database Reset
echo ========================================
echo.

:: Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java and ensure it's in your system PATH
    pause
    exit /b 1
)

:: Check if Maven is available
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven and ensure it's in your system PATH
    pause
    exit /b 1
)

echo Stopping any running Spring Boot application...
taskkill /f /im java.exe /fi "WINDOWTITLE eq CineMitr*" >nul 2>&1

echo.
echo Cleaning Maven project...
mvn clean

echo.
echo Deleting existing H2 database files...
if exist "data\" (
    rmdir /s /q "data"
    echo - Deleted data directory
)
if exist "cinemitr-db.mv.db" (
    del "cinemitr-db.mv.db"
    echo - Deleted cinemitr-db.mv.db
)
if exist "cinemitr-db.trace.db" (
    del "cinemitr-db.trace.db"
    echo - Deleted cinemitr-db.trace.db
)

echo.
echo Compiling the application...
mvn compile

echo.
echo Starting Spring Boot application with fresh database...
echo - Database will be recreated with updated schema
echo - Sample data will be loaded automatically
echo - Application will be available at: http://localhost:8081
echo - H2 Console will be available at: http://localhost:8081/h2-console
echo.
echo JDBC URL: jdbc:h2:file:./data/cinemitr-db
echo Username: sa
echo Password: password
echo.

:: Start the application
mvn spring-boot:run

pause