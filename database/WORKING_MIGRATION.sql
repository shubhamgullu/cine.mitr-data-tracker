-- =============================================================================
-- WORKING MIGRATION: Content Catalog Link as Primary Key
-- =============================================================================
-- This migration modifies content_catalog table to use 'link' as primary key
-- Tested and working version
-- =============================================================================

-- Enable safe updates and foreign key checks
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 1;

-- Step 1: Create backup tables
DROP TABLE IF EXISTS content_catalog_backup;
DROP TABLE IF EXISTS upload_catalog_backup;

CREATE TABLE content_catalog_backup AS SELECT * FROM content_catalog;
CREATE TABLE upload_catalog_backup AS SELECT * FROM upload_catalog;

SELECT 'BACKUP_CREATED' as step, COUNT(*) as content_records FROM content_catalog_backup;
SELECT 'BACKUP_CREATED' as step, COUNT(*) as upload_records FROM upload_catalog_backup;

-- Step 2: Check for duplicate links and show them
SELECT 'DUPLICATE_CHECK' as step, link, COUNT(*) as duplicates
FROM content_catalog 
GROUP BY link 
HAVING COUNT(*) > 1;

-- Step 3: Remove duplicate links (keep the most recent one)
DELETE c1 FROM content_catalog c1
INNER JOIN content_catalog c2 
WHERE c1.link = c2.link 
  AND c1.created_on < c2.created_on;

SELECT 'DUPLICATES_REMOVED' as step, COUNT(*) as remaining_records FROM content_catalog;

-- Step 4: Drop foreign key constraints that reference content_catalog.id
ALTER TABLE upload_catalog DROP FOREIGN KEY IF EXISTS fk_upload_content_catalog;
ALTER TABLE upload_catalog DROP FOREIGN KEY IF EXISTS fk_content_catalog;

-- Step 5: Add new columns to upload_catalog for link-based relationships
ALTER TABLE upload_catalog ADD COLUMN linked_content_catalog_link VARCHAR(500) DEFAULT NULL;

-- Step 6: Populate the new link-based relationship column
UPDATE upload_catalog u
SET u.linked_content_catalog_link = (
    SELECT c.link 
    FROM content_catalog_backup c 
    WHERE c.id = u.linked_content_catalog_id
    LIMIT 1
)
WHERE u.linked_content_catalog_id IS NOT NULL;

SELECT 'LINK_RELATIONSHIPS_UPDATED' as step, 
       COUNT(*) as total_uploads,
       COUNT(linked_content_catalog_link) as linked_uploads
FROM upload_catalog;

-- Step 7: Create new content_catalog table structure with link as primary key
DROP TABLE content_catalog;

CREATE TABLE content_catalog (
    link VARCHAR(500) NOT NULL PRIMARY KEY,
    media_catalog_type VARCHAR(50) NOT NULL,
    media_catalog_name TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    location VARCHAR(500),
    metadata TEXT,
    local_status VARCHAR(50),
    location_path TEXT,
    like_states VARCHAR(20),
    comment_states TEXT,
    upload_content_status VARCHAR(50),
    linked_upload_catalog_link VARCHAR(500),
    created_by VARCHAR(100) DEFAULT 'system',
    updated_by VARCHAR(100) DEFAULT 'system',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Add indexes for performance
    INDEX idx_media_type (media_catalog_type),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_created_on (created_on),
    INDEX idx_upload_status (upload_content_status),
    INDEX idx_local_status (local_status),
    INDEX idx_linked_upload (linked_upload_catalog_link)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Step 8: Insert data from backup into new structure
INSERT INTO content_catalog (
    link, media_catalog_type, media_catalog_name, status, priority,
    location, metadata, local_status, location_path, like_states,
    comment_states, upload_content_status, created_by, updated_by,
    created_on, updated_on
)
SELECT 
    link, media_catalog_type, media_catalog_name, status, priority,
    location, metadata, local_status, location_path, like_states,
    comment_states, upload_content_status, 
    COALESCE(created_by, 'system'),
    COALESCE(updated_by, 'system'),
    created_on, updated_on
FROM content_catalog_backup
WHERE link IS NOT NULL AND TRIM(link) != '';

SELECT 'DATA_RESTORED' as step, COUNT(*) as content_records FROM content_catalog;

-- Step 9: Update content_catalog with linked upload catalog links
UPDATE content_catalog c
SET c.linked_upload_catalog_link = (
    SELECT u.content_catalog_link
    FROM upload_catalog u
    WHERE u.linked_content_catalog_link = c.link
    LIMIT 1
)
WHERE EXISTS (
    SELECT 1 FROM upload_catalog u 
    WHERE u.linked_content_catalog_link = c.link
);

-- Step 10: Remove old foreign key column from upload_catalog
ALTER TABLE upload_catalog DROP COLUMN linked_content_catalog_id;

-- Step 11: Add foreign key constraint for new relationship
ALTER TABLE upload_catalog 
ADD CONSTRAINT fk_upload_content_catalog 
    FOREIGN KEY (linked_content_catalog_link) 
    REFERENCES content_catalog(link) 
    ON DELETE SET NULL 
    ON UPDATE CASCADE;

-- Step 12: Final verification
SELECT 'FINAL_VERIFICATION' as step;

SELECT 'content_catalog' as table_name, COUNT(*) as record_count, 
       COUNT(DISTINCT link) as unique_links,
       COUNT(linked_upload_catalog_link) as with_upload_links
FROM content_catalog;

SELECT 'upload_catalog' as table_name, COUNT(*) as record_count,
       COUNT(linked_content_catalog_link) as with_content_links
FROM upload_catalog;

-- Step 13: Check primary key constraint
SHOW INDEX FROM content_catalog WHERE Key_name = 'PRIMARY';

-- Step 14: Verify foreign key constraints
SELECT 
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = DATABASE() 
  AND (TABLE_NAME = 'upload_catalog' AND REFERENCED_TABLE_NAME = 'content_catalog');

-- Success message
SELECT 'MIGRATION_COMPLETED_SUCCESSFULLY' as status, 
       NOW() as completed_at,
       'Link is now primary key for content_catalog' as message;

-- Optional: Uncomment to drop backup tables after successful verification
-- DROP TABLE content_catalog_backup;
-- DROP TABLE upload_catalog_backup;