-- =============================================================================
-- Cine Mitr Database Migration Script v1.5.0
-- =============================================================================
-- Migration for Content Catalog Link Uniqueness and Upload Catalog Integration
-- Adds unique constraint on link field in content_catalog table
-- Enhances upload catalog integration with content catalog auto-creation
-- =============================================================================

-- Add unique constraint on link field in content_catalog to prevent duplicates
-- This ensures one link can only exist once in the content catalog
ALTER TABLE content_catalog ADD CONSTRAINT uk_content_catalog_link UNIQUE (link);

-- =============================================================================
-- Enhanced Content Catalog Analytics
-- =============================================================================

-- Create view for content catalog analytics
CREATE VIEW content_catalog_analytics AS
SELECT 
    media_catalog_type,
    COUNT(*) as total_items,
    COUNT(CASE WHEN status = 'NEW' THEN 1 END) as new_count,
    COUNT(CASE WHEN status = 'DOWNLOADED' THEN 1 END) as downloaded_count,
    COUNT(CASE WHEN status = 'ERROR' THEN 1 END) as error_count,
    COUNT(CASE WHEN status = 'IN_PROGRESS' THEN 1 END) as in_progress_count,
    COUNT(CASE WHEN linked_upload_catalog_id IS NOT NULL THEN 1 END) as linked_uploads,
    COUNT(CASE WHEN upload_content_status = 'UPLOADED' THEN 1 END) as uploaded_count
FROM content_catalog
GROUP BY media_catalog_type
ORDER BY total_items DESC;

-- Create view for upload catalog integration analytics  
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

-- =============================================================================
-- Data Validation and Cleanup
-- =============================================================================

-- Check for duplicate links in content catalog before applying constraint
SELECT 
    link,
    COUNT(*) as duplicate_count,
    GROUP_CONCAT(id ORDER BY id) as duplicate_ids,
    GROUP_CONCAT(media_catalog_name ORDER BY id) as media_names
FROM content_catalog
GROUP BY link
HAVING COUNT(*) > 1;

-- Check for orphaned upload catalog entries without content catalog links
SELECT 
    uc.id,
    uc.content_catalog_link,
    uc.media_catalog_name,
    uc.linked_content_catalog_id,
    'Missing content catalog entry' as issue
FROM upload_catalog uc
LEFT JOIN content_catalog cc ON uc.content_catalog_link = cc.link
WHERE cc.id IS NULL;

-- Check for mismatched linked IDs
SELECT 
    uc.id as upload_id,
    uc.content_catalog_link,
    uc.linked_content_catalog_id,
    cc.id as actual_content_id,
    cc.link as actual_link,
    'Mismatched linked ID' as issue
FROM upload_catalog uc
LEFT JOIN content_catalog cc ON uc.linked_content_catalog_id = cc.id
WHERE uc.linked_content_catalog_id IS NOT NULL 
  AND (cc.id IS NULL OR cc.link != uc.content_catalog_link);

-- =============================================================================
-- Index Creation for Performance
-- =============================================================================

-- Add index on content catalog link for fast lookups
CREATE INDEX IF NOT EXISTS idx_content_catalog_link ON content_catalog(link);

-- Add index on upload catalog content link for integration queries
CREATE INDEX IF NOT EXISTS idx_upload_catalog_content_link ON upload_catalog(content_catalog_link);

-- Add index on linked content catalog ID
CREATE INDEX IF NOT EXISTS idx_upload_catalog_linked_content ON upload_catalog(linked_content_catalog_id);

-- Add index on content catalog linked upload ID
CREATE INDEX IF NOT EXISTS idx_content_catalog_linked_upload ON content_catalog(linked_upload_catalog_id);

-- =============================================================================
-- Function for Auto-Creating Content Catalog Entry from Upload Catalog
-- =============================================================================

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

-- =============================================================================
-- Sample Data and Testing
-- =============================================================================

-- Test the auto-create function (remove after testing)
SELECT AutoCreateContentCatalogEntry(
    'https://example.com/test-link',
    'MOVIE',
    'Test Movie for Integration',
    999999
) as created_content_id;

-- Clean up test data
DELETE FROM content_catalog WHERE created_by = 'auto-upload-integration' AND media_catalog_name = 'Test Movie for Integration';

-- =============================================================================
-- Migration Verification
-- =============================================================================

-- Verify unique constraint was added
SELECT 
    CONSTRAINT_NAME,
    CONSTRAINT_TYPE,
    TABLE_NAME
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
WHERE TABLE_NAME = 'content_catalog' 
  AND CONSTRAINT_NAME = 'uk_content_catalog_link';

-- Verify indexes were created
SHOW INDEX FROM content_catalog WHERE Key_name LIKE 'idx_content_catalog_%';
SHOW INDEX FROM upload_catalog WHERE Key_name LIKE 'idx_upload_catalog_%';

-- Test function exists
SELECT ROUTINE_NAME, ROUTINE_TYPE
FROM INFORMATION_SCHEMA.ROUTINES
WHERE ROUTINE_NAME = 'AutoCreateContentCatalogEntry';

-- Summary statistics
SELECT 
    'Total Content Catalog Records' as metric,
    COUNT(*) as value
FROM content_catalog
UNION ALL
SELECT 
    'Content with Upload Links' as metric,
    COUNT(*) as value
FROM content_catalog
WHERE linked_upload_catalog_id IS NOT NULL
UNION ALL
SELECT 
    'Total Upload Catalog Records' as metric,
    COUNT(*) as value
FROM upload_catalog
UNION ALL
SELECT 
    'Uploads with Content Links' as metric,
    COUNT(*) as value
FROM upload_catalog
WHERE linked_content_catalog_id IS NOT NULL;

-- =============================================================================
-- End of Migration Script v1.5.0
-- =============================================================================