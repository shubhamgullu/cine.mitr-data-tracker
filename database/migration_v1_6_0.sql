-- =============================================================================
-- Cine Mitr Database Migration Script v1.6.0
-- =============================================================================
-- Migration for Content Catalog Enhancement
-- Adds three new columns: Local Status, Location Path, and enhanced Metadata
-- =============================================================================

-- Add new columns to content_catalog table
ALTER TABLE content_catalog ADD COLUMN local_status VARCHAR(50);
ALTER TABLE content_catalog ADD COLUMN location_path TEXT;

-- Update existing metadata column to ensure it's TEXT type
ALTER TABLE content_catalog MODIFY COLUMN metadata TEXT;

-- Add indexes for better performance on new fields
CREATE INDEX IF NOT EXISTS idx_content_catalog_local_status ON content_catalog(local_status);
CREATE INDEX IF NOT EXISTS idx_content_catalog_location_path ON content_catalog(location_path(255));

-- =============================================================================
-- Data Migration and Cleanup
-- =============================================================================

-- Set default values for existing records
UPDATE content_catalog 
SET local_status = 'NOT_AVAILABLE' 
WHERE local_status IS NULL;

-- Clean up any empty location_path entries
UPDATE content_catalog 
SET location_path = NULL 
WHERE location_path = '';

-- =============================================================================
-- Enhanced Content Catalog Analytics with New Fields
-- =============================================================================

-- Update existing analytics view to include new columns
DROP VIEW IF EXISTS content_catalog_analytics;

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
    COUNT(CASE WHEN local_status = 'NOT_AVAILABLE' THEN 1 END) as not_locally_available,
    COUNT(CASE WHEN local_status = 'DOWNLOADING' THEN 1 END) as downloading,
    COUNT(CASE WHEN local_status = 'PROCESSING' THEN 1 END) as processing,
    COUNT(CASE WHEN location_path IS NOT NULL AND location_path != '' THEN 1 END) as with_location_path,
    COUNT(CASE WHEN metadata IS NOT NULL AND metadata != '' THEN 1 END) as with_metadata
FROM content_catalog
GROUP BY media_catalog_type
ORDER BY total_items DESC;

-- Create new analytics view specifically for local storage status
CREATE VIEW content_local_storage_analytics AS
SELECT 
    local_status,
    media_catalog_type,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (), 2) as percentage,
    COUNT(CASE WHEN location_path IS NOT NULL AND location_path != '' THEN 1 END) as with_path,
    AVG(LENGTH(metadata)) as avg_metadata_length
FROM content_catalog
WHERE local_status IS NOT NULL
GROUP BY local_status, media_catalog_type
ORDER BY count DESC;

-- =============================================================================
-- Sample Data and Testing
-- =============================================================================

-- Insert sample data to test new columns
INSERT INTO content_catalog (
    link, 
    media_catalog_type, 
    media_catalog_name, 
    status, 
    priority, 
    local_status, 
    location_path, 
    metadata,
    upload_content_status,
    created_on,
    updated_on,
    created_by,
    updated_by
) VALUES (
    'https://example.com/test-content-enhanced',
    'MOVIE',
    'Test Movie Enhanced',
    'NEW',
    'MEDIUM',
    'AVAILABLE',
    '/storage/media/movies/test-movie-enhanced/',
    '{"quality": "1080p", "size": "2.5GB", "codec": "H.264", "audio": "AAC", "subtitles": ["en", "es"], "genre": "Action", "year": "2024"}',
    'PENDING_UPLOAD',
    NOW(),
    NOW(),
    'migration-test',
    'migration-test'
);

-- Verify the sample data was inserted correctly
SELECT 
    id,
    media_catalog_name,
    local_status,
    location_path IS NOT NULL as has_location_path,
    LENGTH(metadata) as metadata_length
FROM content_catalog 
WHERE created_by = 'migration-test';

-- Clean up test data
DELETE FROM content_catalog WHERE created_by = 'migration-test';

-- =============================================================================
-- Data Validation Queries
-- =============================================================================

-- Check distribution of local status values
SELECT 
    local_status,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM content_catalog), 2) as percentage
FROM content_catalog
GROUP BY local_status
ORDER BY count DESC;

-- Check records with location paths
SELECT 
    COUNT(*) as total_records,
    COUNT(CASE WHEN location_path IS NOT NULL AND location_path != '' THEN 1 END) as with_location_path,
    COUNT(CASE WHEN metadata IS NOT NULL AND metadata != '' THEN 1 END) as with_metadata,
    COUNT(CASE WHEN local_status IS NOT NULL THEN 1 END) as with_local_status
FROM content_catalog;

-- Find records that might need attention (e.g., AVAILABLE status but no location path)
SELECT 
    id,
    media_catalog_name,
    local_status,
    location_path,
    'Inconsistent: Available but no path' as issue
FROM content_catalog
WHERE local_status = 'AVAILABLE' 
  AND (location_path IS NULL OR location_path = '')
LIMIT 10;

-- =============================================================================
-- Performance and Storage Analysis
-- =============================================================================

-- Check table size after adding new columns
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS "DB Size in MB"
FROM information_schema.tables
WHERE table_schema = DATABASE()
  AND table_name = 'content_catalog';

-- Verify indexes were created
SHOW INDEX FROM content_catalog WHERE Key_name LIKE 'idx_content_catalog_%';

-- =============================================================================
-- Migration Verification
-- =============================================================================

-- Verify new columns were added
DESCRIBE content_catalog;

-- Check column constraints and types
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_KEY
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'content_catalog'
  AND TABLE_SCHEMA = DATABASE()
  AND COLUMN_NAME IN ('local_status', 'location_path', 'metadata')
ORDER BY ORDINAL_POSITION;

-- Final summary statistics
SELECT 
    'Migration v1.6.0 Complete' as status,
    COUNT(*) as total_records,
    COUNT(CASE WHEN local_status IS NOT NULL THEN 1 END) as records_with_local_status,
    COUNT(CASE WHEN location_path IS NOT NULL THEN 1 END) as records_with_location_path,
    COUNT(CASE WHEN metadata IS NOT NULL AND LENGTH(metadata) > 0 THEN 1 END) as records_with_metadata
FROM content_catalog;

-- =============================================================================
-- End of Migration Script v1.6.0
-- =============================================================================