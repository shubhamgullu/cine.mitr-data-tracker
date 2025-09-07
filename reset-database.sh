#!/bin/bash

echo "========================================"
echo "CineMitr Data Tracker Database Reset"
echo "========================================"
echo

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java and ensure it's in your system PATH"
    exit 1
fi

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven and ensure it's in your system PATH"
    exit 1
fi

echo "Stopping any running Spring Boot application..."
pkill -f "spring-boot" 2>/dev/null || true
sleep 2

echo
echo "Cleaning Maven project..."
mvn clean

echo
echo "Deleting existing H2 database files..."
if [ -d "data" ]; then
    rm -rf data
    echo "- Deleted data directory"
fi
if [ -f "cinemitr-db.mv.db" ]; then
    rm -f cinemitr-db.mv.db
    echo "- Deleted cinemitr-db.mv.db"
fi
if [ -f "cinemitr-db.trace.db" ]; then
    rm -f cinemitr-db.trace.db
    echo "- Deleted cinemitr-db.trace.db"
fi

echo
echo "Compiling the application..."
mvn compile

echo
echo "Starting Spring Boot application with fresh database..."
echo "- Database will be recreated with updated schema"
echo "- Sample data will be loaded automatically"
echo "- Application will be available at: http://localhost:8081"
echo "- H2 Console will be available at: http://localhost:8081/h2-console"
echo
echo "JDBC URL: jdbc:h2:file:./data/cinemitr-db"
echo "Username: sa"
echo "Password: password"
echo

# Start the application
mvn spring-boot:run