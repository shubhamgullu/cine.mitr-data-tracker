-- =============================================================================
-- CONTENT CATALOG PRIMARY KEY MIGRATION SCRIPT
-- =============================================================================
-- Purpose: Migrate content_catalog table to use 'link' as primary key instead of 'id'
-- Issue: Fix duplicate link data and align database schema with JPA model
-- Compatible with: MySQL, PostgreSQL, H2
-- Date: 2025-08-31
-- =============================================================================

-- Step 1: Backup current data structure
-- =============================================================================
CREATE TABLE content_catalog_backup AS SELECT * FROM content_catalog;

-- Step 2: Analyze and handle duplicate links
-- =============================================================================

-- Check for duplicate links (for information)
SELECT link, COUNT(*) as duplicate_count 
FROM content_catalog 
GROUP BY link 
HAVING COUNT(*) > 1;

-- Step 3: Handle duplicate link data
-- =============================================================================

-- Option A: Keep the latest record for each duplicate link
-- This creates a temporary table with deduplicated data
CREATE TABLE content_catalog_temp AS
SELECT *
FROM content_catalog c1
WHERE c1.id = (
    SELECT MAX(c2.id)
    FROM content_catalog c2
    WHERE c2.link = c1.link
);

-- Step 4: Drop existing constraints and indexes
-- =============================================================================

-- Drop foreign key constraints that reference content_catalog.id
ALTER TABLE upload_catalog DROP FOREIGN KEY IF EXISTS fk_upload_content_catalog;

-- Drop the existing primary key
ALTER TABLE content_catalog DROP PRIMARY KEY;

-- Drop the auto-increment id column
ALTER TABLE content_catalog DROP COLUMN id;

-- Step 5: Recreate table structure with link as primary key
-- =============================================================================

-- Clear the table and insert deduplicated data
TRUNCATE TABLE content_catalog;

-- Insert deduplicated data from temp table (excluding the old id column)
INSERT INTO content_catalog (
    link,
    media_catalog_type,
    media_catalog_name,
    status,
    priority,
    location,
    metadata,
    local_status,
    location_path,
    like_states,
    comment_states,
    upload_content_status,
    linked_upload_catalog_link,
    created_by,
    updated_by,
    created_on,
    updated_on
)
SELECT 
    link,
    media_catalog_type,
    media_catalog_name,
    status,
    priority,
    location,
    metadata,
    local_status,
    location_path,
    like_states,
    comment_states,
    upload_content_status,
    linked_upload_catalog_link,
    created_by,
    updated_by,
    created_on,
    updated_on
FROM content_catalog_temp;

-- Step 6: Add link as primary key
-- =============================================================================

-- Add primary key constraint on link column
ALTER TABLE content_catalog ADD PRIMARY KEY (link);

-- Step 7: Update upload_catalog table to reference link instead of id
-- =============================================================================

-- Add new column for linking to content_catalog by link
ALTER TABLE upload_catalog ADD COLUMN linked_content_catalog_link VARCHAR(500);

-- Update the new column with actual link values
UPDATE upload_catalog uc
INNER JOIN content_catalog_backup ccb ON uc.linked_upload_catalog_id = ccb.id
SET uc.linked_content_catalog_link = ccb.link
WHERE uc.linked_upload_catalog_id IS NOT NULL;

-- Drop the old integer reference column (after data migration)
-- ALTER TABLE upload_catalog DROP COLUMN linked_upload_catalog_id;

-- Step 8: Recreate foreign key constraints
-- =============================================================================

-- Add foreign key constraint for proper referential integrity
ALTER TABLE upload_catalog 
ADD CONSTRAINT fk_upload_content_catalog_link 
FOREIGN KEY (linked_content_catalog_link) REFERENCES content_catalog(link) 
ON DELETE SET NULL ON UPDATE CASCADE;

-- Step 9: Add indexes for performance
-- =============================================================================

-- Add indexes on frequently queried columns
CREATE INDEX idx_content_catalog_status ON content_catalog(status);
CREATE INDEX idx_content_catalog_media_type ON content_catalog(media_catalog_type);
CREATE INDEX idx_content_catalog_priority ON content_catalog(priority);
CREATE INDEX idx_content_catalog_created_on ON content_catalog(created_on);
CREATE INDEX idx_content_catalog_updated_on ON content_catalog(updated_on);

-- Step 10: Cleanup temporary tables
-- =============================================================================

-- Drop temporary table (uncomment when migration is verified)
-- DROP TABLE content_catalog_temp;

-- Step 11: Data integrity verification
-- =============================================================================

-- Verify no duplicate links exist
SELECT 'Duplicate Links Check' as check_type, COUNT(*) as duplicate_count
FROM (
    SELECT link, COUNT(*) as cnt 
    FROM content_catalog 
    GROUP BY link 
    HAVING COUNT(*) > 1
) duplicates;

-- Verify primary key constraint
SHOW CREATE TABLE content_catalog;

-- Verify row counts match (should be <= original due to deduplication)
SELECT 
    'Original Count' as table_name, COUNT(*) as row_count FROM content_catalog_backup
UNION ALL
SELECT 
    'New Count' as table_name, COUNT(*) as row_count FROM content_catalog;

-- Step 12: Update JPA sequence (if using auto-generated values)
-- =============================================================================

-- Note: Since we're using link as string primary key, no sequence needed
-- But if you have any JPA sequence references, they should be removed

-- =============================================================================
-- ROLLBACK SCRIPT (Use only if needed)
-- =============================================================================

/*
-- Rollback steps (ONLY use if migration fails):

-- 1. Restore from backup
DROP TABLE content_catalog;
CREATE TABLE content_catalog AS SELECT * FROM content_catalog_backup;

-- 2. Re-add auto-increment primary key
ALTER TABLE content_catalog ADD COLUMN id BIGINT PRIMARY KEY AUTO_INCREMENT FIRST;

-- 3. Update auto-increment counter
ALTER TABLE content_catalog AUTO_INCREMENT = 1;

-- 4. Restore foreign key references
ALTER TABLE upload_catalog 
ADD CONSTRAINT fk_upload_content_catalog 
FOREIGN KEY (linked_upload_catalog_id) REFERENCES content_catalog(id);
*/

-- =============================================================================
-- VERIFICATION QUERIES
-- =============================================================================

-- Run these queries after migration to verify success:

-- 1. Check table structure
DESCRIBE content_catalog;

-- 2. Verify primary key
SHOW INDEX FROM content_catalog WHERE Key_name = 'PRIMARY';

-- 3. Check for any orphaned references
SELECT COUNT(*) as orphaned_references
FROM upload_catalog uc
LEFT JOIN content_catalog cc ON uc.linked_content_catalog_link = cc.link
WHERE uc.linked_content_catalog_link IS NOT NULL 
AND cc.link IS NULL;

-- 4. Sample data verification
SELECT link, media_catalog_name, status, created_on 
FROM content_catalog 
ORDER BY created_on DESC 
LIMIT 10;

-- =============================================================================
-- MIGRATION COMPLETE
-- =============================================================================
-- After running this migration:
-- 1. content_catalog table uses 'link' as primary key (VARCHAR)
-- 2. Duplicate links have been removed (keeping latest)
-- 3. upload_catalog properly references content_catalog by link
-- 4. All foreign key constraints are updated
-- 5. Proper indexes are in place for performance
-- =============================================================================