-- =============================================================================
-- QUICK MIGRATION: Make Link Primary Key in Content Catalog
-- =============================================================================
-- This script modifies the content_catalog table to use 'link' as primary key
-- WARNING: This will lose existing data relationships. Run on development first!
-- =============================================================================

-- Step 1: Backup existing data
CREATE TABLE content_catalog_backup AS SELECT * FROM content_catalog;
CREATE TABLE upload_catalog_backup AS SELECT * FROM upload_catalog;

-- Step 2: Check for duplicate links (must resolve these first)
SELECT 'DUPLICATE_LINKS_CHECK' as check_type, link, COUNT(*) as count 
FROM content_catalog 
GROUP BY link 
HAVING COUNT(*) > 1;

-- Step 3: Remove duplicates (keeps the latest entry for each link)
DELETE c1 FROM content_catalog c1
INNER JOIN content_catalog c2 
WHERE c1.link = c2.link 
  AND c1.created_on < c2.created_on;

-- Step 4: Drop existing foreign key constraints
ALTER TABLE upload_catalog DROP FOREIGN KEY IF EXISTS fk_upload_content_catalog;

-- Step 5: Recreate content_catalog table with link as primary key
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
    
    -- Constraints
    CONSTRAINT chk_content_type CHECK (media_catalog_type IN ('MOVIE', 'ALBUM', 'WEB_SERIES', 'DOCUMENTARY')),
    CONSTRAINT chk_content_status CHECK (status IN ('NEW', 'DOWNLOADED', 'ERROR', 'IN_PROGRESS')),
    CONSTRAINT chk_content_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    CONSTRAINT chk_content_local_status CHECK (local_status IN ('AVAILABLE', 'NOT_AVAILABLE', 'PARTIALLY_AVAILABLE', 'DOWNLOADING', 'PROCESSING', 'CORRUPTED')),
    CONSTRAINT chk_content_like_states CHECK (like_states IN ('LIKED', 'DISLIKED', 'NEUTRAL')),
    CONSTRAINT chk_content_upload_status CHECK (upload_content_status IN ('PENDING_UPLOAD', 'UPLOADING', 'UPLOADED', 'UPLOAD_FAILED')),
    
    -- Performance indexes
    INDEX idx_content_media_type (media_catalog_type),
    INDEX idx_content_status (status),
    INDEX idx_content_priority (priority),
    INDEX idx_content_created_on (created_on),
    INDEX idx_content_upload_status (upload_content_status),
    INDEX idx_content_local_status (local_status)
);

-- Step 6: Restore data from backup (excluding id and linking columns for now)
INSERT INTO content_catalog (
    link, media_catalog_type, media_catalog_name, status, priority, 
    location, metadata, local_status, location_path, like_states, 
    comment_states, upload_content_status, created_by, updated_by, 
    created_on, updated_on
)
SELECT 
    link, media_catalog_type, media_catalog_name, status, priority,
    location, metadata, local_status, location_path, like_states,
    comment_states, upload_content_status, created_by, updated_by,
    created_on, updated_on
FROM content_catalog_backup;

-- Step 7: Update upload_catalog to use link references
ALTER TABLE upload_catalog 
DROP COLUMN linked_content_catalog_id,
ADD COLUMN linked_content_catalog_link VARCHAR(500);

-- Step 8: Re-establish relationships where possible
UPDATE upload_catalog u 
INNER JOIN content_catalog_backup cb ON u.content_catalog_link = cb.link
SET u.linked_content_catalog_link = cb.link
WHERE u.content_catalog_link IS NOT NULL;

-- Step 9: Update content_catalog with upload links
UPDATE content_catalog c
INNER JOIN upload_catalog u ON c.link = u.content_catalog_link
SET c.linked_upload_catalog_link = u.content_catalog_link
WHERE u.linked_content_catalog_link = c.link;

-- Step 10: Add foreign key constraint
ALTER TABLE upload_catalog 
ADD CONSTRAINT fk_upload_content_catalog 
    FOREIGN KEY (linked_content_catalog_link) 
    REFERENCES content_catalog(link) 
    ON DELETE SET NULL 
    ON UPDATE CASCADE;

-- Step 11: Verification
SELECT 'MIGRATION_VERIFICATION' as check_type,
    COUNT(*) as content_records,
    COUNT(DISTINCT link) as unique_links,
    COUNT(linked_upload_catalog_link) as linked_uploads
FROM content_catalog;

SELECT 'UPLOAD_RELATIONSHIPS' as check_type,
    COUNT(*) as total_uploads,
    COUNT(linked_content_catalog_link) as linked_to_content
FROM upload_catalog;

-- Step 12: Drop backup tables (uncomment after verification)
-- DROP TABLE content_catalog_backup;
-- DROP TABLE upload_catalog_backup;

SELECT 'MIGRATION_COMPLETED' as status, NOW() as completed_at;