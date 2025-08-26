-- =============================================================================
-- Cine Mitr Database Migration Script v1.2.0
-- =============================================================================
-- Migration for States Catalog Enhancements and Linking Features
-- Adds table management functionality and bidirectional linking
-- =============================================================================

-- Add linking columns to existing tables (if they don't exist)
ALTER TABLE content_catalog ADD COLUMN IF NOT EXISTS linked_upload_catalog_id BIGINT;
ALTER TABLE upload_catalog ADD COLUMN IF NOT EXISTS content_block VARCHAR(255);
ALTER TABLE upload_catalog ADD COLUMN IF NOT EXISTS linked_content_catalog_id BIGINT;

-- Add indexes for the new linking columns
CREATE INDEX IF NOT EXISTS idx_content_linked_upload ON content_catalog(linked_upload_catalog_id);
CREATE INDEX IF NOT EXISTS idx_upload_linked_content ON upload_catalog(linked_content_catalog_id);

-- =============================================================================
-- States Catalog Enhanced Analytics (Already exists but ensuring structure)
-- =============================================================================

-- Verify States Catalog table structure is complete
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
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes for better query performance
    INDEX idx_states_views (views DESC),
    INDEX idx_states_subscribers (subscribers DESC),
    INDEX idx_states_engagement (avg_engagement_rate DESC),
    INDEX idx_states_created_on (created_on)
);

-- =============================================================================
-- Enhanced Analytics Views for States Catalog
-- =============================================================================

-- Drop and recreate analytics views with latest structure
DROP VIEW IF EXISTS states_catalog_summary;
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

-- Recent states performance view
DROP VIEW IF EXISTS states_recent_performance;
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

-- =============================================================================
-- Enhanced Catalog Linking Views
-- =============================================================================

-- View for content-upload catalog relationships
DROP VIEW IF EXISTS content_upload_relationships;
CREATE VIEW content_upload_relationships AS
SELECT 
    c.id as content_id,
    c.link as content_link,
    c.media_catalog_name,
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

-- =============================================================================
-- Data Migration for Existing Records
-- =============================================================================

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
-- Sample Data for States Catalog Testing (if table is empty)
-- =============================================================================

INSERT IGNORE INTO states_catalog (views, subscribers, interactions, total_content, reach, impressions, profile_visits, website_clicks, email_clicks, call_clicks, followers_gained, followers_lost, reels_count, stories_count, avg_engagement_rate) VALUES
(25000, 4200, 2100, 78, 22000, 35000, 1500, 280, 45, 18, 320, 25, 42, 150, 8.75),
(18500, 3800, 1650, 65, 16500, 24000, 1200, 195, 32, 12, 280, 15, 35, 120, 7.92),
(31000, 5100, 2800, 95, 28000, 42000, 2100, 380, 65, 28, 450, 35, 58, 200, 9.35);

-- =============================================================================
-- Performance Optimization Queries for v1.2.0
-- =============================================================================

-- Query: States Catalog Performance Metrics
SELECT 
    s.id,
    s.views,
    s.subscribers,
    s.avg_engagement_rate,
    (s.followers_gained - s.followers_lost) as net_growth,
    s.created_on,
    RANK() OVER (ORDER BY s.avg_engagement_rate DESC) as engagement_rank,
    RANK() OVER (ORDER BY (s.followers_gained - s.followers_lost) DESC) as growth_rank
FROM states_catalog s
WHERE s.created_on >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 90 DAY)
ORDER BY s.avg_engagement_rate DESC;

-- Query: Linked Records Health Check
SELECT 
    'Content without Upload Link' as issue_type,
    COUNT(*) as count
FROM content_catalog 
WHERE linked_upload_catalog_id IS NULL
UNION ALL
SELECT 
    'Upload without Content Link' as issue_type,
    COUNT(*) as count
FROM upload_catalog 
WHERE linked_content_catalog_id IS NULL;

-- =============================================================================
-- Stored Procedures for States Catalog Management
-- =============================================================================

-- Procedure: Calculate engagement metrics
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
-- Migration Verification Queries
-- =============================================================================

-- Verify table structures
SHOW COLUMNS FROM content_catalog;
SHOW COLUMNS FROM upload_catalog;
SHOW COLUMNS FROM states_catalog;

-- Verify indexes
SHOW INDEX FROM content_catalog WHERE Key_name LIKE 'idx_%';
SHOW INDEX FROM upload_catalog WHERE Key_name LIKE 'idx_%';
SHOW INDEX FROM states_catalog WHERE Key_name LIKE 'idx_%';

-- Verify data integrity
SELECT 
    'Total Content Records' as metric,
    COUNT(*) as value
FROM content_catalog
UNION ALL
SELECT 
    'Total Upload Records' as metric,
    COUNT(*) as value
FROM upload_catalog
UNION ALL
SELECT 
    'Total States Records' as metric,
    COUNT(*) as value
FROM states_catalog
UNION ALL
SELECT 
    'Linked Content-Upload Pairs' as metric,
    COUNT(*) as value
FROM content_catalog c
JOIN upload_catalog u ON c.linked_upload_catalog_id = u.id;

-- =============================================================================
-- End of Migration Script v1.2.0
-- =============================================================================