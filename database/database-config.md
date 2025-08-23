# Database Configuration Guide

## Overview
This guide contains database configuration instructions for different environments and database systems supported by Cine Mitr.

## Supported Databases
- **H2 Database** (Development)
- **MySQL 8.0+** (Production)
- **PostgreSQL 12+** (Production)
- **MariaDB 10.5+** (Production)

## Environment Configurations

### Development Environment (H2)
```properties
# application-dev.properties
spring.datasource.url=jdbc:h2:mem:cinemitr
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

### Production Environment (MySQL)
```properties
# application-prod.properties
spring.datasource.url=jdbc:mysql://localhost:3306/cinemitr?useSSL=false&serverTimezone=UTC
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=${DB_USERNAME:cinemitr_user}
spring.datasource.password=${DB_PASSWORD:secure_password}
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Connection pool settings
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=20000
```

### Production Environment (PostgreSQL)
```properties
# application-postgres.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cinemitr
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=${DB_USERNAME:cinemitr_user}
spring.datasource.password=${DB_PASSWORD:secure_password}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# PostgreSQL specific settings
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
```

## Database Setup Instructions

### 1. H2 Database (Development)
No setup required. Database is created automatically in memory.

### 2. MySQL Setup
```sql
-- Create database
CREATE DATABASE cinemitr CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER 'cinemitr_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON cinemitr.* TO 'cinemitr_user'@'localhost';
FLUSH PRIVILEGES;

-- Run schema creation
mysql -u cinemitr_user -p cinemitr < database/schema.sql
```

### 3. PostgreSQL Setup
```bash
# Create database and user
sudo -u postgres psql
CREATE DATABASE cinemitr;
CREATE USER cinemitr_user WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE cinemitr TO cinemitr_user;
\q

# Run schema creation
psql -U cinemitr_user -d cinemitr -f database/schema.sql
```

### 4. MariaDB Setup
```sql
-- Same as MySQL
CREATE DATABASE cinemitr CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'cinemitr_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON cinemitr.* TO 'cinemitr_user'@'localhost';
FLUSH PRIVILEGES;

-- Run schema creation
mysql -u cinemitr_user -p cinemitr < database/schema.sql
```

## Migration Commands

### Initial Setup
```bash
# Run schema creation
java -jar cine-mitr.jar --spring.jpa.hibernate.ddl-auto=none
```

### Apply Migrations
```bash
# Run migration scripts
mysql -u cinemitr_user -p cinemitr < database/migration.sql
```

### Backup and Restore
```bash
# Backup
mysqldump -u cinemitr_user -p cinemitr > backup_$(date +%Y%m%d).sql

# Restore
mysql -u cinemitr_user -p cinemitr < backup_20240825.sql
```

## Performance Tuning

### MySQL Configuration (my.cnf)
```ini
[mysqld]
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
max_connections = 200
query_cache_size = 64M
tmp_table_size = 64M
max_heap_table_size = 64M
```

### PostgreSQL Configuration (postgresql.conf)
```ini
shared_buffers = 256MB
effective_cache_size = 1GB
work_mem = 4MB
maintenance_work_mem = 64MB
max_connections = 200
```

## Monitoring Queries

### Check Database Status
```sql
-- MySQL
SHOW ENGINE INNODB STATUS;
SHOW PROCESSLIST;
SHOW TABLE STATUS FROM cinemitr;

-- PostgreSQL
SELECT * FROM pg_stat_activity;
SELECT * FROM pg_stat_user_tables;
```

### Performance Analysis
```sql
-- Find slow queries
SELECT 
    movie_name,
    category,
    view_count,
    click_count,
    DATEDIFF(NOW(), created_at) as days_old
FROM movie_instagram_links
WHERE view_count > 1000
ORDER BY view_count DESC
LIMIT 10;

-- Analyze table growth
SELECT 
    COUNT(*) as total_records,
    AVG(LENGTH(description)) as avg_description_length,
    MIN(created_at) as oldest_record,
    MAX(created_at) as newest_record
FROM movie_instagram_links;
```

## Security Considerations

### 1. Database User Permissions
- Create dedicated database users with minimal required permissions
- Use strong passwords
- Restrict network access to database servers

### 2. Connection Security
```properties
# Enable SSL for production
spring.datasource.url=jdbc:mysql://localhost:3306/cinemitr?useSSL=true&requireSSL=true
```

### 3. Data Encryption
```sql
-- Example of encrypting sensitive data
CREATE TABLE movie_instagram_links_encrypted (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    movie_name VARCHAR(255) NOT NULL,
    instagram_link VARBINARY(500) NOT NULL, -- Encrypted
    -- other fields...
);
```

## Troubleshooting

### Common Issues

#### 1. Connection Timeout
```properties
# Increase timeout values
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.validation-timeout=5000
```

#### 2. Character Encoding Issues
```properties
# Ensure UTF-8 encoding
spring.datasource.url=jdbc:mysql://localhost:3306/cinemitr?characterEncoding=UTF-8&useUnicode=true
```

#### 3. Time Zone Issues
```properties
# Set server timezone
spring.datasource.url=jdbc:mysql://localhost:3306/cinemitr?serverTimezone=UTC
```

### Diagnostic Queries
```sql
-- Check table sizes
SELECT 
    table_name,
    table_rows,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS "Size (MB)"
FROM information_schema.tables 
WHERE table_schema = 'cinemitr';

-- Check index usage
SELECT 
    table_name,
    index_name,
    cardinality,
    sub_part,
    packed,
    nullable,
    index_type
FROM information_schema.statistics 
WHERE table_schema = 'cinemitr';
```

## Backup Strategy

### Automated Backup Script
```bash
#!/bin/bash
# backup.sh
DB_NAME="cinemitr"
DB_USER="cinemitr_user"
DB_PASS="secure_password"
BACKUP_DIR="/var/backups/cinemitr"
DATE=$(date +%Y%m%d_%H%M%S)

# Create backup
mysqldump -u $DB_USER -p$DB_PASS $DB_NAME > $BACKUP_DIR/cinemitr_backup_$DATE.sql

# Compress backup
gzip $BACKUP_DIR/cinemitr_backup_$DATE.sql

# Clean old backups (keep last 30 days)
find $BACKUP_DIR -name "cinemitr_backup_*.sql.gz" -mtime +30 -delete
```

### Cron Job Setup
```bash
# Add to crontab (crontab -e)
0 2 * * * /path/to/backup.sh
```