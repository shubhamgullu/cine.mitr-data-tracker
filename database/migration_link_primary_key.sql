-- =============================================================================
-- Migration: Make Link Primary Key in Content Catalog
-- =============================================================================
-- This migration makes the 'link' field the primary key in content_catalog table
-- and updates all related constraints and relationships
-- Execute this after backing up your data
-- =============================================================================

-- Set SQL mode for consistent behavior
SET sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';

-- Enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- BACKUP AND PREPARATION
-- =============================================================================

-- Create backup table before making changes
CREATE TABLE content_catalog_backup AS 
SELECT * FROM content_catalog;

SELECT 
    'Backup created' as status,
    COUNT(*) as records_backed_up
FROM content_catalog_backup;

-- Check for duplicate links before proceeding
SELECT 
    link,
    COUNT(*) as duplicate_count
FROM content_catalog
GROUP BY link
HAVING COUNT(*) > 1
ORDER BY duplicate_count DESC;

-- If duplicates exist, you'll need to handle them first
-- Option 1: Keep the latest record for each duplicate link
DELETE c1 FROM content_catalog c1
INNER JOIN content_catalog c2 
WHERE c1.link = c2.link 
  AND c1.created_on < c2.created_on;

-- Option 2: Or merge duplicate records (manual process recommended)
-- This should be done manually to ensure data integrity

-- =============================================================================
-- STRUCTURE MODIFICATION
-- =============================================================================

-- Step 1: Drop foreign key constraints from related tables
ALTER TABLE upload_catalog 
DROP FOREIGN KEY IF EXISTS fk_upload_content_catalog;

-- Step 2: Add new varchar primary key column temporarily
ALTER TABLE content_catalog 
ADD COLUMN link_pk VARCHAR(500) NOT NULL DEFAULT '';

-- Step 3: Update the new column with link values
UPDATE content_catalog 
SET link_pk = link;

-- Step 4: Drop the old primary key
ALTER TABLE content_catalog 
DROP PRIMARY KEY;

-- Step 5: Drop the old id column
ALTER TABLE content_catalog 
DROP COLUMN id;

-- Step 6: Make link the primary key
ALTER TABLE content_catalog 
DROP COLUMN link_pk,
ADD PRIMARY KEY (link);

-- Step 7: Update upload_catalog to reference link instead of id
-- First add the new foreign key column
ALTER TABLE upload_catalog 
ADD COLUMN linked_content_catalog_link VARCHAR(500);

-- Update the new column with link values from content_catalog
UPDATE upload_catalog u
INNER JOIN content_catalog c ON u.linked_content_catalog_id = c.id
SET u.linked_content_catalog_link = c.link
WHERE u.linked_content_catalog_id IS NOT NULL;

-- Drop the old foreign key column
ALTER TABLE upload_catalog 
DROP COLUMN linked_content_catalog_id;

-- Rename the new column
ALTER TABLE upload_catalog 
CHANGE COLUMN linked_content_catalog_link linked_content_catalog_link VARCHAR(500);

-- Add foreign key constraint
ALTER TABLE upload_catalog 
ADD CONSTRAINT fk_upload_content_catalog 
    FOREIGN KEY (linked_content_catalog_link) 
    REFERENCES content_catalog(link) 
    ON DELETE SET NULL 
    ON UPDATE CASCADE;

-- Step 8: Update content_catalog to reference upload_catalog by link
-- Add new column for upload catalog link reference
ALTER TABLE content_catalog 
ADD COLUMN linked_upload_catalog_link VARCHAR(500);

-- Update with existing upload catalog links
UPDATE content_catalog c
INNER JOIN upload_catalog u ON c.linked_upload_catalog_id = u.id
SET c.linked_upload_catalog_link = u.content_catalog_link
WHERE c.linked_upload_catalog_id IS NOT NULL;

-- Drop old column
ALTER TABLE content_catalog 
DROP COLUMN linked_upload_catalog_id;

-- =============================================================================
-- INDEX OPTIMIZATION
-- =============================================================================

-- Add indexes for performance (link is already primary key, so indexed)
CREATE INDEX idx_content_media_type ON content_catalog (media_catalog_type);
CREATE INDEX idx_content_status ON content_catalog (status);
CREATE INDEX idx_content_priority ON content_catalog (priority);
CREATE INDEX idx_content_created_on ON content_catalog (created_on);
CREATE INDEX idx_content_upload_status ON content_catalog (upload_content_status);
CREATE INDEX idx_content_local_status ON content_catalog (local_status);

-- Update upload_catalog indexes
CREATE INDEX idx_upload_content_link ON upload_catalog (linked_content_catalog_link);
CREATE INDEX idx_upload_catalog_link ON upload_catalog (content_catalog_link);

-- =============================================================================
-- UPDATE APPLICATION CONSTRAINTS
-- =============================================================================

-- Add check constraints for data validation
ALTER TABLE content_catalog 
ADD CONSTRAINT chk_content_link_not_empty 
    CHECK (LENGTH(TRIM(link)) > 0);

ALTER TABLE content_catalog 
ADD CONSTRAINT chk_content_link_format 
    CHECK (link LIKE 'http%' OR link LIKE 'https%');

-- =============================================================================
-- DATA VALIDATION AND CLEANUP
-- =============================================================================

-- Validate that all links are unique and not null
SELECT 
    'Link Validation' as check_type,
    COUNT(*) as total_records,
    COUNT(DISTINCT link) as unique_links,
    COUNT(CASE WHEN link IS NULL OR TRIM(link) = '' THEN 1 END) as empty_links
FROM content_catalog;

-- Validate foreign key relationships
SELECT 
    'Upload-Content Relationship Validation' as check_type,
    COUNT(*) as total_upload_records,
    COUNT(u.linked_content_catalog_link) as linked_records,
    COUNT(c.link) as valid_links
FROM upload_catalog u
LEFT JOIN content_catalog c ON u.linked_content_catalog_link = c.link;

-- =============================================================================
-- UPDATE VIEWS AND PROCEDURES
-- =============================================================================

-- Update views that referenced the old id column
DROP VIEW IF EXISTS content_upload_relationships;

CREATE VIEW content_upload_relationships AS
SELECT 
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
    c.local_status,
    u.id as upload_id,
    u.upload_status,
    u.upload_catalog_location,
    u.upload_catalog_caption,
    c.created_on as content_created,
    u.created_on as upload_created,
    CASE 
        WHEN c.link IS NOT NULL AND u.id IS NOT NULL THEN 'LINKED'
        WHEN c.link IS NOT NULL AND u.id IS NULL THEN 'CONTENT_ONLY'
        WHEN c.link IS NULL AND u.id IS NOT NULL THEN 'UPLOAD_ONLY'
        ELSE 'UNKNOWN'
    END as relationship_status
FROM content_catalog c
LEFT JOIN upload_catalog u ON c.linked_upload_catalog_link = u.content_catalog_link
ORDER BY c.created_on DESC;

-- Update content catalog analytics view
DROP VIEW IF EXISTS content_catalog_analytics;

CREATE VIEW content_catalog_analytics AS
SELECT 
    media_catalog_type,
    COUNT(*) as total_items,
    COUNT(CASE WHEN status = 'NEW' THEN 1 END) as new_count,
    COUNT(CASE WHEN status = 'DOWNLOADED' THEN 1 END) as downloaded_count,
    COUNT(CASE WHEN status = 'ERROR' THEN 1 END) as error_count,
    COUNT(CASE WHEN status = 'IN_PROGRESS' THEN 1 END) as in_progress_count,
    COUNT(CASE WHEN linked_upload_catalog_link IS NOT NULL THEN 1 END) as linked_uploads,
    COUNT(CASE WHEN upload_content_status = 'UPLOADED' THEN 1 END) as uploaded_count,
    COUNT(CASE WHEN local_status = 'AVAILABLE' THEN 1 END) as locally_available,
    COUNT(CASE WHEN local_status = 'NOT_AVAILABLE' THEN 1 END) as not_available,
    COUNT(CASE WHEN local_status = 'DOWNLOADING' THEN 1 END) as downloading,
    COUNT(CASE WHEN location_path IS NOT NULL AND location_path != '' THEN 1 END) as with_location_path
FROM content_catalog
GROUP BY media_catalog_type
ORDER BY total_items DESC;

-- =============================================================================
-- VERIFICATION QUERIES
-- =============================================================================

-- Final validation
SELECT 
    'Migration Completed Successfully' as status,
    'Content Catalog now uses link as primary key' as message;

SELECT 
    'Table Structure' as info,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_KEY
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'content_catalog'
ORDER BY ORDINAL_POSITION;

SELECT 
    'Primary Key Constraints' as info,
    CONSTRAINT_NAME,
    COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'content_catalog' 
  AND CONSTRAINT_NAME = 'PRIMARY';

SELECT 
    'Foreign Key Constraints' as info,
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = DATABASE() 
  AND (TABLE_NAME = 'content_catalog' OR REFERENCED_TABLE_NAME = 'content_catalog')
  AND CONSTRAINT_NAME != 'PRIMARY';

-- Sample data validation
SELECT 
    'Sample Content Catalog Records' as info,
    link,
    media_catalog_name,
    status,
    linked_upload_catalog_link
FROM content_catalog
ORDER BY created_on DESC
LIMIT 5;

SELECT 
    'Sample Upload Catalog Records' as info,
    id,
    content_catalog_link,
    linked_content_catalog_link,
    media_catalog_name
FROM upload_catalog
WHERE linked_content_catalog_link IS NOT NULL
ORDER BY created_on DESC
LIMIT 5;

-- =============================================================================
-- CLEANUP
-- =============================================================================

-- Drop backup table after successful verification (optional)
-- DROP TABLE content_catalog_backup;

-- =============================================================================
-- NOTES FOR APPLICATION UPDATES
-- =============================================================================

/*
IMPORTANT: After running this migration, you need to update your application code:

1. Update ContentCatalog entity:
   - Change @Id from Long id to String link
   - Remove @GeneratedValue annotation
   - Update all references to use link instead of id

2. Update UploadCatalog entity:
   - Change linkedContentCatalogId from Long to String
   - Rename to linkedContentCatalogLink
   - Update foreign key relationship

3. Update repositories:
   - Change primary key type from Long to String
   - Update custom queries that reference id field

4. Update service classes:
   - Update all method parameters and return types
   - Change id-based lookups to link-based lookups

5. Update controllers:
   - Update path variables and request parameters
   - Change id references to link references

6. Update UI templates:
   - Replace id references with link references
   - Update forms and AJAX calls

7. Update any integration tests or data fixtures
*/

-- =============================================================================
-- END OF MIGRATION
-- =============================================================================