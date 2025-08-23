-- =============================================================================
-- Cine Mitr - Database Migration Scripts
-- =============================================================================
-- This file contains migration scripts for database schema changes
-- Execute these scripts in order when upgrading the database
-- =============================================================================

-- =============================================================================
-- Migration Version 1.0.0 - Initial Schema
-- =============================================================================

-- Create initial table structure
CREATE TABLE IF NOT EXISTS movie_instagram_links (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    movie_name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    instagram_link VARCHAR(500) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    view_count BIGINT DEFAULT 0,
    click_count BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =============================================================================
-- Migration Version 1.1.0 - Add Indexes for Performance
-- =============================================================================

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_movie_name ON movie_instagram_links (movie_name);
CREATE INDEX IF NOT EXISTS idx_category ON movie_instagram_links (category);
CREATE INDEX IF NOT EXISTS idx_status ON movie_instagram_links (status);
CREATE INDEX IF NOT EXISTS idx_created_at ON movie_instagram_links (created_at);
CREATE INDEX IF NOT EXISTS idx_view_count ON movie_instagram_links (view_count DESC);
CREATE INDEX IF NOT EXISTS idx_click_count ON movie_instagram_links (click_count DESC);

-- =============================================================================
-- Migration Version 1.2.0 - Add Analytics Features (Future)
-- =============================================================================

-- Add engagement rate tracking column (for future use)
-- ALTER TABLE movie_instagram_links ADD COLUMN engagement_rate DECIMAL(5,2) DEFAULT 0.0;

-- Add tags column for better categorization (for future use)
-- ALTER TABLE movie_instagram_links ADD COLUMN tags JSON;

-- Add source tracking (for future use)
-- ALTER TABLE movie_instagram_links ADD COLUMN source VARCHAR(100) DEFAULT 'manual';

-- =============================================================================
-- Migration Version 1.3.0 - Add User Interaction Tracking (Future)
-- =============================================================================

-- Create table for tracking individual user interactions (for future use)
/*
CREATE TABLE IF NOT EXISTS link_interactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    link_id BIGINT NOT NULL,
    interaction_type VARCHAR(20) NOT NULL, -- 'VIEW', 'CLICK', 'SHARE'
    user_ip VARCHAR(45),
    user_agent TEXT,
    interaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (link_id) REFERENCES movie_instagram_links(id) ON DELETE CASCADE
);
*/

-- =============================================================================
-- Data Migration Scripts
-- =============================================================================

-- Migrate from old schema (if exists)
-- This section would contain scripts to migrate data from previous versions

-- Example: Update status values if they were different in previous version
-- UPDATE movie_instagram_links SET status = 'ACTIVE' WHERE status = '1';
-- UPDATE movie_instagram_links SET status = 'INACTIVE' WHERE status = '0';

-- Example: Populate missing view_count and click_count with 0
UPDATE movie_instagram_links 
SET view_count = 0 
WHERE view_count IS NULL;

UPDATE movie_instagram_links 
SET click_count = 0 
WHERE click_count IS NULL;

-- =============================================================================
-- Database Cleanup and Maintenance
-- =============================================================================

-- Remove any test data (if needed)
-- DELETE FROM movie_instagram_links WHERE movie_name LIKE 'Test%';

-- Remove duplicate entries (if any)
-- This query removes duplicates keeping the one with the highest ID
/*
DELETE t1 FROM movie_instagram_links t1
INNER JOIN movie_instagram_links t2
WHERE t1.id < t2.id 
  AND t1.movie_name = t2.movie_name 
  AND t1.instagram_link = t2.instagram_link;
*/

-- =============================================================================
-- Post-Migration Verification Scripts
-- =============================================================================

-- Verify data integrity
SELECT 
    'Total Links' as metric, 
    COUNT(*) as value 
FROM movie_instagram_links
UNION ALL
SELECT 
    'Active Links' as metric, 
    COUNT(*) as value 
FROM movie_instagram_links 
WHERE status = 'ACTIVE'
UNION ALL
SELECT 
    'Categories' as metric, 
    COUNT(DISTINCT category) as value 
FROM movie_instagram_links
UNION ALL
SELECT 
    'Links with Views' as metric, 
    COUNT(*) as value 
FROM movie_instagram_links 
WHERE view_count > 0
UNION ALL
SELECT 
    'Links with Clicks' as metric, 
    COUNT(*) as value 
FROM movie_instagram_links 
WHERE click_count > 0;

-- Verify index creation
SHOW INDEX FROM movie_instagram_links;

-- =============================================================================
-- Rollback Scripts (for emergencies)
-- =============================================================================

-- Rollback to version 1.1.0 (remove analytics columns)
-- ALTER TABLE movie_instagram_links DROP COLUMN IF EXISTS engagement_rate;
-- ALTER TABLE movie_instagram_links DROP COLUMN IF EXISTS tags;
-- ALTER TABLE movie_instagram_links DROP COLUMN IF EXISTS source;

-- Rollback to version 1.0.0 (remove all indexes except primary key)
/*
DROP INDEX IF EXISTS idx_movie_name ON movie_instagram_links;
DROP INDEX IF EXISTS idx_category ON movie_instagram_links;
DROP INDEX IF EXISTS idx_status ON movie_instagram_links;
DROP INDEX IF EXISTS idx_created_at ON movie_instagram_links;
DROP INDEX IF EXISTS idx_view_count ON movie_instagram_links;
DROP INDEX IF EXISTS idx_click_count ON movie_instagram_links;
*/

-- =============================================================================
-- Environment-Specific Settings
-- =============================================================================

-- Development Environment
-- SET GLOBAL log_queries_not_using_indexes = ON;
-- SET GLOBAL slow_query_log = ON;

-- Production Environment  
-- SET GLOBAL innodb_buffer_pool_size = 1073741824; -- 1GB
-- SET GLOBAL max_connections = 200;

-- =============================================================================
-- End of Migration File
-- =============================================================================