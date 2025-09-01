-- =============================================================================
-- Cine Mitr - Consolidated Database Migration Script
-- =============================================================================
-- This file consolidates all migration scripts from v1.0.0 to v1.6.0
-- Execute this script on a fresh database to set up the complete schema
-- For existing databases, use the individual migration scripts in order
-- =============================================================================

-- =============================================================================
-- INITIAL SCHEMA SETUP (v1.0.0 - v1.1.0)
-- =============================================================================

-- Create initial movie_instagram_links table
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Performance indexes
    INDEX idx_movie_name (movie_name),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_view_count (view_count DESC),
    INDEX idx_click_count (click_count DESC)
);

-- =============================================================================
-- CATALOG TABLES SETUP (v1.3.0 Base + v1.4.0 + v1.6.0 Enhancements)
-- =============================================================================

-- Create Media Catalog Table with language and genre support
CREATE TABLE IF NOT EXISTS media_catalog (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    platform VARCHAR(100),
    download_status VARCHAR(50) DEFAULT 'NOT_DOWNLOADED',
    location VARCHAR(500),
    description TEXT,
    fun_facts TEXT,
    language VARCHAR(100) DEFAULT NULL,
    main_genre VARCHAR(100) DEFAULT NULL,
    sub_genres TEXT DEFAULT NULL,
    created_by VARCHAR(100) DEFAULT 'system',
    updated_by VARCHAR(100) DEFAULT 'system',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Performance indexes
    INDEX idx_media_name (name),
    INDEX idx_media_type (type),
    INDEX idx_platform (platform),
    INDEX idx_download_status (download_status),
    INDEX idx_media_created_on (created_on),
    INDEX idx_media_language (language),
    INDEX idx_media_main_genre (main_genre),
    INDEX idx_media_sub_genres (sub_genres(100)),
    
    -- Unique constraint for name + language combination
    CONSTRAINT uk_media_name_language UNIQUE (name, language)
);

-- Create Content Catalog Table with all enhancements
CREATE TABLE IF NOT EXISTS content_catalog (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    link VARCHAR(500) NOT NULL,
    media_catalog_type VARCHAR(50) NOT NULL,
    media_catalog_name TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    location VARCHAR(500),
    metadata TEXT,
    like_states VARCHAR(20),
    comment_states TEXT,
    upload_content_status VARCHAR(50),
    local_status VARCHAR(50),
    location_path TEXT,
    linked_upload_catalog_id BIGINT,
    created_by VARCHAR(100) DEFAULT 'system',
    updated_by VARCHAR(100) DEFAULT 'system',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Performance indexes
    INDEX idx_content_status (status),
    INDEX idx_content_type (media_catalog_type),
    INDEX idx_content_name (media_catalog_name(100)),
    INDEX idx_content_priority (priority),
    INDEX idx_content_created_on (created_on),
    INDEX idx_content_linked_upload (linked_upload_catalog_id),
    INDEX idx_content_catalog_local_status (local_status),
    INDEX idx_content_catalog_location_path (location_path(255)),
    INDEX idx_content_catalog_link (link),
    
    -- Unique constraint on link field
    CONSTRAINT uk_content_catalog_link UNIQUE (link)
);

-- Create Upload Catalog Table with linking support
CREATE TABLE IF NOT EXISTS upload_catalog (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_catalog_link VARCHAR(500) NOT NULL,
    media_catalog_type VARCHAR(50) NOT NULL,
    media_catalog_name VARCHAR(255) NOT NULL,
    content_catalog_location VARCHAR(500),
    upload_catalog_location VARCHAR(500),
    upload_status VARCHAR(50) NOT NULL,
    upload_catalog_caption TEXT,
    content_block VARCHAR(255),
    linked_content_catalog_id BIGINT,
    created_by VARCHAR(100) DEFAULT 'system',
    updated_by VARCHAR(100) DEFAULT 'system',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Performance indexes
    INDEX idx_upload_status (upload_status),
    INDEX idx_upload_type (media_catalog_type),
    INDEX idx_upload_name (media_catalog_name),
    INDEX idx_upload_created_on (created_on),
    INDEX idx_upload_linked_content (linked_content_catalog_id),
    INDEX idx_upload_catalog_content_link (content_catalog_link),
    INDEX idx_upload_catalog_linked_content (linked_content_catalog_id)
);

-- Create States Catalog Table with date support
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
    report_date DATE DEFAULT NULL,
    created_by VARCHAR(100) DEFAULT 'system',
    updated_by VARCHAR(100) DEFAULT 'system',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Performance indexes
    INDEX idx_states_views (views DESC),
    INDEX idx_states_subscribers (subscribers DESC),
    INDEX idx_states_engagement (avg_engagement_rate DESC),
    INDEX idx_states_created_on (created_on),
    INDEX idx_states_report_date (report_date),
    INDEX idx_states_date_metrics (report_date, avg_engagement_rate DESC)
);

-- =============================================================================
-- ANALYTICS VIEWS
-- =============================================================================

-- States Catalog Summary View
CREATE VIEW states_catalog_summary AS
SELECT 
    id,
    views,
    subscribers,
    interactions,
    reach,
    avg_engagement_rate,
    followers_gained,
    followers_lost,
    total_content,
    reels_count,
    stories_count,
    (followers_gained - followers_lost) as net_follower_growth,
    (interactions * 100.0 / NULLIF(reach, 0)) as interaction_rate,
    created_on,
    updated_on
FROM states_catalog
ORDER BY created_on DESC;

-- Recent States Performance View
CREATE VIEW states_recent_performance AS
SELECT 
    DATE(created_on) as date,
    SUM(views) as daily_views,
    SUM(interactions) as daily_interactions,
    SUM(followers_gained) as daily_followers_gained,
    SUM(followers_lost) as daily_followers_lost,
    AVG(avg_engagement_rate) as avg_daily_engagement
FROM states_catalog
WHERE created_on >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 30 DAY)
GROUP BY DATE(created_on)
ORDER BY date DESC;

-- States Catalog by Date View
CREATE VIEW states_catalog_by_date AS
SELECT 
    s.id,
    s.report_date,
    s.views,
    s.subscribers,
    s.interactions,
    s.reach,
    s.avg_engagement_rate,
    s.followers_gained,
    s.followers_lost,
    s.total_content,
    s.reels_count,
    s.stories_count,
    (s.followers_gained - s.followers_lost) as net_follower_growth,
    (s.interactions * 100.0 / NULLIF(s.reach, 0)) as interaction_rate,
    s.created_on,
    s.updated_on
FROM states_catalog s
ORDER BY 
    CASE WHEN s.report_date IS NOT NULL THEN s.report_date ELSE DATE(s.created_on) END DESC,
    s.created_on DESC;

-- Content-Upload Catalog Relationships View
CREATE VIEW content_upload_relationships AS
SELECT 
    c.id as content_id,
    c.link as content_link,
    c.media_catalog_name,
    CASE 
        WHEN c.media_catalog_name LIKE '%,%' 
        THEN CONCAT('Multiple (', 
                   (LENGTH(c.media_catalog_name) - LENGTH(REPLACE(c.media_catalog_name, ',', '')) + 1), 
                   ' items)')
        ELSE c.media_catalog_name
    END as media_display_name,
    c.status as content_status,
    c.priority,
    u.id as upload_id,
    u.upload_status,
    u.upload_catalog_location,
    u.upload_catalog_caption,
    c.created_on as content_created,
    u.created_on as upload_created
FROM content_catalog c
LEFT JOIN upload_catalog u ON c.linked_upload_catalog_id = u.id
WHERE c.linked_upload_catalog_id IS NOT NULL
ORDER BY c.created_on DESC;

-- Media Catalog Analytics View
CREATE VIEW media_catalog_analytics AS
SELECT 
    type,
    COUNT(*) as total_items,
    COUNT(CASE WHEN download_status = 'DOWNLOADED' THEN 1 END) as downloaded_count,
    COUNT(CASE WHEN download_status = 'NOT_DOWNLOADED' THEN 1 END) as not_downloaded_count,
    COUNT(CASE WHEN download_status = 'PARTIALLY_DOWNLOADED' THEN 1 END) as partial_count,
    platform,
    COUNT(*) as platform_count,
    language,
    COUNT(CASE WHEN language IS NOT NULL THEN 1 END) as language_count,
    main_genre,
    COUNT(CASE WHEN main_genre IS NOT NULL THEN 1 END) as genre_count
FROM media_catalog
GROUP BY type, platform, language, main_genre
ORDER BY total_items DESC;

-- Media Genre Analytics View
CREATE VIEW media_genre_analytics AS
SELECT 
    main_genre,
    COUNT(*) as total_count,
    COUNT(CASE WHEN type = 'MOVIE' THEN 1 END) as movie_count,
    COUNT(CASE WHEN type = 'WEB_SERIES' THEN 1 END) as series_count,
    COUNT(CASE WHEN type = 'DOCUMENTARY' THEN 1 END) as documentary_count,
    COUNT(CASE WHEN type = 'ALBUM' THEN 1 END) as album_count,
    COUNT(CASE WHEN download_status = 'DOWNLOADED' THEN 1 END) as downloaded_count
FROM media_catalog
WHERE main_genre IS NOT NULL AND main_genre != ''
GROUP BY main_genre
ORDER BY total_count DESC;

-- Media Language Analytics View
CREATE VIEW media_language_analytics AS
SELECT 
    language,
    COUNT(*) as total_count,
    COUNT(CASE WHEN type = 'MOVIE' THEN 1 END) as movie_count,
    COUNT(CASE WHEN type = 'WEB_SERIES' THEN 1 END) as series_count,
    COUNT(CASE WHEN type = 'DOCUMENTARY' THEN 1 END) as documentary_count,
    COUNT(CASE WHEN type = 'ALBUM' THEN 1 END) as album_count,
    COUNT(CASE WHEN download_status = 'DOWNLOADED' THEN 1 END) as downloaded_count
FROM media_catalog
WHERE language IS NOT NULL AND language != ''
GROUP BY language
ORDER BY total_count DESC;

-- Content Catalog Analytics View
CREATE VIEW content_catalog_analytics AS
SELECT 
    media_catalog_type,
    COUNT(*) as total_items,
    COUNT(CASE WHEN status = 'NEW' THEN 1 END) as new_count,
    COUNT(CASE WHEN status = 'DOWNLOADED' THEN 1 END) as downloaded_count,
    COUNT(CASE WHEN status = 'ERROR' THEN 1 END) as error_count,
    COUNT(CASE WHEN status = 'IN_PROGRESS' THEN 1 END) as in_progress_count,
    COUNT(CASE WHEN linked_upload_catalog_id IS NOT NULL THEN 1 END) as linked_uploads,
    COUNT(CASE WHEN upload_content_status = 'UPLOADED' THEN 1 END) as uploaded_count,
    COUNT(CASE WHEN local_status = 'AVAILABLE' THEN 1 END) as locally_available,
    COUNT(CASE WHEN local_status = 'NOT_AVAILABLE' THEN 1 END) as not_available,
    COUNT(CASE WHEN local_status = 'DOWNLOADING' THEN 1 END) as downloading,
    COUNT(CASE WHEN location_path IS NOT NULL AND location_path != '' THEN 1 END) as with_location_path
FROM content_catalog
GROUP BY media_catalog_type
ORDER BY total_items DESC;

-- Upload Content Integration Analytics View
CREATE VIEW upload_content_integration_analytics AS
SELECT 
    uc.media_catalog_type,
    COUNT(*) as total_uploads,
    COUNT(CASE WHEN uc.linked_content_catalog_id IS NOT NULL THEN 1 END) as linked_to_content,
    COUNT(CASE WHEN cc.id IS NOT NULL THEN 1 END) as content_exists,
    COUNT(CASE WHEN cc.id IS NULL THEN 1 END) as missing_content,
    COUNT(CASE WHEN uc.upload_status = 'UPLOADED' THEN 1 END) as upload_completed
FROM upload_catalog uc
LEFT JOIN content_catalog cc ON uc.linked_content_catalog_id = cc.id
GROUP BY uc.media_catalog_type
ORDER BY total_uploads DESC;

-- Local Storage Tracking View
CREATE VIEW content_catalog_local_storage AS
SELECT 
    local_status,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM content_catalog), 2) as percentage,
    media_catalog_type,
    COUNT(CASE WHEN location_path IS NOT NULL AND location_path != '' THEN 1 END) as with_path,
    AVG(CASE WHEN location_path IS NOT NULL AND location_path != '' THEN 1 ELSE 0 END) * 100 as path_percentage
FROM content_catalog
GROUP BY local_status, media_catalog_type
ORDER BY local_status, count DESC;

-- =============================================================================
-- UTILITY FUNCTIONS
-- =============================================================================

-- Function to count media catalog names in a content record
DELIMITER $$
CREATE FUNCTION CountMediaCatalogs(media_names TEXT) 
RETURNS INT
READS SQL DATA
DETERMINISTIC
BEGIN
    IF media_names IS NULL OR TRIM(media_names) = '' THEN
        RETURN 0;
    END IF;
    
    RETURN LENGTH(media_names) - LENGTH(REPLACE(media_names, ',', '')) + 1;
END$$
DELIMITER ;

-- Function to extract nth media catalog name
DELIMITER $$
CREATE FUNCTION GetNthMediaCatalog(media_names TEXT, n INT) 
RETURNS VARCHAR(255)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE result VARCHAR(255);
    
    IF media_names IS NULL OR n <= 0 THEN
        RETURN NULL;
    END IF;
    
    SET result = TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(media_names, ',', n), ',', -1));
    
    IF result = '' THEN
        RETURN NULL;
    END IF;
    
    RETURN result;
END$$
DELIMITER ;

-- Function to validate sub-genres format
DELIMITER $$
CREATE FUNCTION ValidateSubGenres(sub_genres_text TEXT) 
RETURNS BOOLEAN
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE is_valid BOOLEAN DEFAULT TRUE;
    
    -- Check if sub_genres is null or empty (valid)
    IF sub_genres_text IS NULL OR TRIM(sub_genres_text) = '' THEN
        RETURN TRUE;
    END IF;
    
    -- Check for malformed format (leading/trailing commas, double commas)
    IF sub_genres_text LIKE ',%' 
       OR sub_genres_text LIKE '%,' 
       OR sub_genres_text LIKE '%,,%' THEN
        RETURN FALSE;
    END IF;
    
    RETURN is_valid;
END$$
DELIMITER ;

-- Function for Auto-Creating Content Catalog Entry from Upload Catalog
DELIMITER $$
CREATE FUNCTION AutoCreateContentCatalogEntry(
    p_link VARCHAR(2048),
    p_media_catalog_type VARCHAR(50),
    p_media_catalog_name TEXT,
    p_upload_catalog_id BIGINT
) 
RETURNS BIGINT
MODIFIES SQL DATA
DETERMINISTIC
BEGIN
    DECLARE content_catalog_id BIGINT DEFAULT NULL;
    DECLARE content_type VARCHAR(50);
    DECLARE content_status VARCHAR(50) DEFAULT 'NEW';
    
    -- Convert upload catalog media type to content catalog media type
    CASE p_media_catalog_type
        WHEN 'MOVIE' THEN SET content_type = 'MOVIE';
        WHEN 'ALBUM' THEN SET content_type = 'ALBUM';
        WHEN 'WEB_SERIES' THEN SET content_type = 'WEB_SERIES';
        WHEN 'DOCUMENTARY' THEN SET content_type = 'DOCUMENTARY';
        ELSE SET content_type = 'MOVIE';
    END CASE;
    
    -- Check if content catalog entry already exists with this link
    SELECT id INTO content_catalog_id
    FROM content_catalog
    WHERE link = p_link
    LIMIT 1;
    
    -- If no entry exists, create a new one
    IF content_catalog_id IS NULL THEN
        INSERT INTO content_catalog (
            link, 
            media_catalog_type, 
            media_catalog_name, 
            status, 
            priority, 
            upload_content_status,
            linked_upload_catalog_id,
            local_status,
            created_on,
            updated_on,
            created_by,
            updated_by
        ) VALUES (
            p_link,
            content_type,
            p_media_catalog_name,
            content_status,
            'MEDIUM',
            'PENDING_UPLOAD',
            p_upload_catalog_id,
            'NOT_AVAILABLE',
            NOW(),
            NOW(),
            'auto-upload-integration',
            'auto-upload-integration'
        );
        
        SET content_catalog_id = LAST_INSERT_ID();
    ELSE
        -- Update existing entry to link with upload catalog
        UPDATE content_catalog 
        SET 
            linked_upload_catalog_id = p_upload_catalog_id,
            upload_content_status = 'PENDING_UPLOAD',
            updated_on = NOW(),
            updated_by = 'auto-upload-integration'
        WHERE id = content_catalog_id;
    END IF;
    
    RETURN content_catalog_id;
END$$
DELIMITER ;

-- Stored Procedure to Calculate Engagement Metrics
DELIMITER $$
CREATE PROCEDURE CalculateEngagementMetrics(IN states_id BIGINT)
BEGIN
    DECLARE calculated_rate DECIMAL(5,2);
    
    SELECT 
        (interactions * 100.0 / NULLIF(reach, 0))
    INTO calculated_rate
    FROM states_catalog 
    WHERE id = states_id;
    
    UPDATE states_catalog 
    SET avg_engagement_rate = COALESCE(calculated_rate, 0.00),
        updated_on = CURRENT_TIMESTAMP
    WHERE id = states_id;
END$$
DELIMITER ;

-- =============================================================================
-- DATA INITIALIZATION AND CLEANUP
-- =============================================================================

-- Initialize missing view_count and click_count
UPDATE movie_instagram_links 
SET view_count = 0 
WHERE view_count IS NULL;

UPDATE movie_instagram_links 
SET click_count = 0 
WHERE click_count IS NULL;

-- Set default local_status for content catalog
UPDATE content_catalog 
SET local_status = 'NOT_AVAILABLE' 
WHERE local_status IS NULL;

-- Set report_date for existing states_catalog records
UPDATE states_catalog 
SET report_date = DATE(created_on)
WHERE report_date IS NULL;

-- Clean up empty location_path entries
UPDATE content_catalog 
SET location_path = NULL 
WHERE location_path = '';

-- Update existing upload catalog records to link with content catalog
UPDATE upload_catalog u 
SET linked_content_catalog_id = (
    SELECT c.id 
    FROM content_catalog c 
    WHERE c.media_catalog_name = u.media_catalog_name 
    AND c.link = u.content_catalog_link
    LIMIT 1
)
WHERE linked_content_catalog_id IS NULL;

-- Update existing content catalog records to link with upload catalog
UPDATE content_catalog c 
SET linked_upload_catalog_id = (
    SELECT u.id 
    FROM upload_catalog u 
    WHERE u.media_catalog_name = c.media_catalog_name 
    AND u.content_catalog_link = c.link
    AND u.linked_content_catalog_id = c.id
    LIMIT 1
)
WHERE linked_upload_catalog_id IS NULL;

-- =============================================================================
-- SAMPLE DATA (Optional - Remove in Production)
-- =============================================================================

-- Sample States Catalog Data
INSERT IGNORE INTO states_catalog (views, subscribers, interactions, total_content, reach, impressions, profile_visits, website_clicks, email_clicks, call_clicks, followers_gained, followers_lost, reels_count, stories_count, avg_engagement_rate) VALUES
(25000, 4200, 2100, 78, 22000, 35000, 1500, 280, 45, 18, 320, 25, 42, 150, 8.75),
(18500, 3800, 1650, 65, 16500, 24000, 1200, 195, 32, 12, 280, 15, 35, 120, 7.92),
(31000, 5100, 2800, 95, 28000, 42000, 2100, 380, 65, 28, 450, 35, 58, 200, 9.35);

-- =============================================================================
-- VERIFICATION QUERIES
-- =============================================================================

-- Verify table creation and structure
SELECT 
    'Database Setup Complete' as status,
    'All tables created successfully' as message
UNION ALL
SELECT 
    'Total Tables' as status,
    COUNT(*) as message
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME IN ('movie_instagram_links', 'media_catalog', 'content_catalog', 'upload_catalog', 'states_catalog');

-- Verify views creation
SELECT 
    'Views Created' as status,
    COUNT(*) as message
FROM INFORMATION_SCHEMA.VIEWS 
WHERE TABLE_SCHEMA = DATABASE();

-- Verify functions creation
SELECT 
    'Functions Created' as status,
    COUNT(*) as message
FROM INFORMATION_SCHEMA.ROUTINES 
WHERE ROUTINE_SCHEMA = DATABASE()
  AND ROUTINE_TYPE = 'FUNCTION';

-- Summary statistics
SELECT 
    'Total Records in movie_instagram_links' as metric,
    COUNT(*) as value
FROM movie_instagram_links
UNION ALL
SELECT 
    'Total Records in media_catalog' as metric,
    COUNT(*) as value
FROM media_catalog
UNION ALL
SELECT 
    'Total Records in content_catalog' as metric,
    COUNT(*) as value
FROM content_catalog
UNION ALL
SELECT 
    'Total Records in upload_catalog' as metric,
    COUNT(*) as value
FROM upload_catalog
UNION ALL
SELECT 
    'Total Records in states_catalog' as metric,
    COUNT(*) as value
FROM states_catalog;

-- =============================================================================
-- End of Consolidated Migration Script
-- =============================================================================
-- Note: This script replaces the need for individual migration files v1.0.0 through v1.6.0
-- For existing databases, use the individual migration scripts in sequence
-- =============================================================================