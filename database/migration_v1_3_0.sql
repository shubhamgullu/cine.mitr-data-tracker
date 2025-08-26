-- =============================================================================
-- Cine Mitr Database Migration Script v1.3.0
-- =============================================================================
-- Migration for Multi-Media Catalog Names Support and States Catalog Date Feature
-- Enables content catalog entries to link multiple media catalog names
-- Adds report_date support for backdated states catalog entries
-- =============================================================================

-- Update Content Catalog to support multiple media catalog names
ALTER TABLE content_catalog MODIFY COLUMN media_catalog_name TEXT NOT NULL;

-- Add report_date column to states_catalog for backdate entries
ALTER TABLE states_catalog ADD COLUMN IF NOT EXISTS report_date DATE DEFAULT NULL;

-- Add index for report_date for better query performance
CREATE INDEX IF NOT EXISTS idx_states_report_date ON states_catalog(report_date);

-- =============================================================================
-- Data Migration for Existing Multi-Media Content Support
-- =============================================================================

-- No data migration needed for media_catalog_name as existing single names remain valid
-- The new TEXT type supports both single names and comma-separated multiple names

-- Update existing states_catalog records to set report_date to created_on date if null
UPDATE states_catalog 
SET report_date = DATE(created_on)
WHERE report_date IS NULL;

-- =============================================================================
-- Enhanced Views for Multi-Media Support
-- =============================================================================

-- Update content_upload_relationships view to show media catalog names properly
DROP VIEW IF EXISTS content_upload_relationships;
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

-- Create view for states catalog with date support
DROP VIEW IF EXISTS states_catalog_by_date;
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

-- =============================================================================
-- Multi-Media Content Analysis Functions
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

-- =============================================================================
-- Enhanced Analytics Queries for Multi-Media Support
-- =============================================================================

-- Query: Content records with multiple media catalogs
SELECT 
    id,
    link,
    media_catalog_name,
    CountMediaCatalogs(media_catalog_name) as media_count,
    status,
    created_on
FROM content_catalog
WHERE media_catalog_name LIKE '%,%'
ORDER BY CountMediaCatalogs(media_catalog_name) DESC, created_on DESC;

-- Query: Most popular media catalog names (including from multi-media entries)
CREATE TEMPORARY TABLE IF NOT EXISTS temp_media_names AS
SELECT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(media_catalog_name, ',', numbers.n), ',', -1)) as media_name,
       COUNT(*) as usage_count
FROM content_catalog
JOIN (SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 
      UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10) numbers
ON CHAR_LENGTH(media_catalog_name) - CHAR_LENGTH(REPLACE(media_catalog_name, ',', '')) >= numbers.n - 1
WHERE TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(media_catalog_name, ',', numbers.n), ',', -1)) != ''
GROUP BY TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(media_catalog_name, ',', numbers.n), ',', -1))
ORDER BY usage_count DESC;

-- Query: States catalog performance by report date
SELECT 
    report_date,
    views,
    subscribers,
    interactions,
    avg_engagement_rate,
    followers_gained,
    followers_lost,
    (followers_gained - followers_lost) as net_growth,
    created_on
FROM states_catalog
WHERE report_date IS NOT NULL
ORDER BY report_date DESC;

-- =============================================================================
-- Data Validation and Integrity Checks
-- =============================================================================

-- Check for content records with malformed media_catalog_name (leading/trailing commas)
SELECT id, media_catalog_name, 'Malformed media names (leading/trailing commas)' as issue
FROM content_catalog
WHERE media_catalog_name LIKE ',%' 
   OR media_catalog_name LIKE '%,' 
   OR media_catalog_name LIKE '%,,%';

-- Check states catalog entries with future report dates
SELECT id, report_date, created_on, 'Future report date' as issue
FROM states_catalog
WHERE report_date > CURRENT_DATE();

-- =============================================================================
-- Performance Optimization
-- =============================================================================

-- Additional index for multi-media content analysis
CREATE INDEX IF NOT EXISTS idx_content_media_multi ON content_catalog(media_catalog_name(100));

-- Index for states catalog report date queries
CREATE INDEX IF NOT EXISTS idx_states_date_metrics ON states_catalog(report_date, avg_engagement_rate DESC);

-- =============================================================================
-- Migration Verification
-- =============================================================================

-- Verify column changes
SHOW COLUMNS FROM content_catalog WHERE Field = 'media_catalog_name';
SHOW COLUMNS FROM states_catalog WHERE Field = 'report_date';

-- Verify indexes
SHOW INDEX FROM content_catalog WHERE Key_name LIKE 'idx_%';
SHOW INDEX FROM states_catalog WHERE Key_name LIKE 'idx_%';

-- Test multi-media functions
SELECT 
    'Multi-Media Function Test' as test_type,
    CountMediaCatalogs('Movie A,Movie B,Movie C') as count_result,
    GetNthMediaCatalog('Movie A,Movie B,Movie C', 2) as nth_result;

-- Summary statistics
SELECT 
    'Total Content Records' as metric,
    COUNT(*) as value
FROM content_catalog
UNION ALL
SELECT 
    'Multi-Media Content Records' as metric,
    COUNT(*) as value
FROM content_catalog
WHERE media_catalog_name LIKE '%,%'
UNION ALL
SELECT 
    'States Records with Report Date' as metric,
    COUNT(*) as value
FROM states_catalog
WHERE report_date IS NOT NULL;

-- =============================================================================
-- End of Migration Script v1.3.0
-- =============================================================================