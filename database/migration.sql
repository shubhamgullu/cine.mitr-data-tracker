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
-- Migration Version 1.3.0 - Add Catalog Tables
-- =============================================================================

-- Create Media Catalog Table
CREATE TABLE IF NOT EXISTS media_catalog (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    platform VARCHAR(100),
    download_status VARCHAR(50) DEFAULT 'NOT_DOWNLOADED',
    location VARCHAR(500),
    description TEXT,
    fun_facts TEXT,
    created_by VARCHAR(100) DEFAULT 'system',
    updated_by VARCHAR(100) DEFAULT 'system',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create Content Catalog Table
CREATE TABLE IF NOT EXISTS content_catalog (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    link VARCHAR(500) NOT NULL,
    media_catalog_type VARCHAR(50) NOT NULL,
    media_catalog_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    location VARCHAR(500),
    metadata TEXT,
    like_states VARCHAR(20),
    comment_states TEXT,
    upload_content_status VARCHAR(50),
    created_by VARCHAR(100) DEFAULT 'system',
    updated_by VARCHAR(100) DEFAULT 'system',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create Upload Catalog Table
CREATE TABLE IF NOT EXISTS upload_catalog (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_catalog_link VARCHAR(500) NOT NULL,
    media_catalog_type VARCHAR(50) NOT NULL,
    media_catalog_name VARCHAR(255) NOT NULL,
    content_catalog_location VARCHAR(500),
    upload_catalog_location VARCHAR(500),
    upload_status VARCHAR(50) NOT NULL,
    upload_catalog_caption TEXT,
    created_by VARCHAR(100) DEFAULT 'system',
    updated_by VARCHAR(100) DEFAULT 'system',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create States Catalog Table
CREATE TABLE IF NOT EXISTS states_catalog (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    views INT DEFAULT 0,
    subscribers INT DEFAULT 0,
    interactions INT DEFAULT 0,
    total_content INT DEFAULT 0,
    reach INT DEFAULT 0,
    impressions INT DEFAULT 0,
    profile_visits INT DEFAULT 0,
    website_clicks INT DEFAULT 0,
    email_clicks INT DEFAULT 0,
    call_clicks INT DEFAULT 0,
    followers_gained INT DEFAULT 0,
    followers_lost INT DEFAULT 0,
    reels_count INT DEFAULT 0,
    stories_count INT DEFAULT 0,
    avg_engagement_rate DECIMAL(5,2) DEFAULT 0.00,
    created_by VARCHAR(100) DEFAULT 'system',
    updated_by VARCHAR(100) DEFAULT 'system',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Add indexes for new catalog tables
CREATE INDEX IF NOT EXISTS idx_media_name ON media_catalog (name);
CREATE INDEX IF NOT EXISTS idx_media_type ON media_catalog (type);
CREATE INDEX IF NOT EXISTS idx_platform ON media_catalog (platform);
CREATE INDEX IF NOT EXISTS idx_download_status ON media_catalog (download_status);
CREATE INDEX IF NOT EXISTS idx_media_created_on ON media_catalog (created_on);

CREATE INDEX IF NOT EXISTS idx_content_status ON content_catalog (status);
CREATE INDEX IF NOT EXISTS idx_content_type ON content_catalog (media_catalog_type);
CREATE INDEX IF NOT EXISTS idx_content_name ON content_catalog (media_catalog_name);
CREATE INDEX IF NOT EXISTS idx_content_priority ON content_catalog (priority);
CREATE INDEX IF NOT EXISTS idx_content_created_on ON content_catalog (created_on);

CREATE INDEX IF NOT EXISTS idx_upload_status ON upload_catalog (upload_status);
CREATE INDEX IF NOT EXISTS idx_upload_type ON upload_catalog (media_catalog_type);
CREATE INDEX IF NOT EXISTS idx_upload_name ON upload_catalog (media_catalog_name);
CREATE INDEX IF NOT EXISTS idx_upload_created_on ON upload_catalog (created_on);

CREATE INDEX IF NOT EXISTS idx_states_views ON states_catalog (views DESC);
CREATE INDEX IF NOT EXISTS idx_states_subscribers ON states_catalog (subscribers DESC);
CREATE INDEX IF NOT EXISTS idx_states_engagement ON states_catalog (avg_engagement_rate DESC);
CREATE INDEX IF NOT EXISTS idx_states_created_on ON states_catalog (created_on);

-- =============================================================================
-- Migration Version 1.4.0 - Add User Interaction Tracking (Future)
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