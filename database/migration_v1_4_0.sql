-- =============================================================================
-- Cine Mitr Database Migration Script v1.4.0
-- =============================================================================
-- Migration for Media Catalog Language, Main Genre, and Sub-Genres Support
-- Adds language, main_genre, and sub_genres columns to media_catalog table
-- Enhances search and categorization capabilities for media content
-- =============================================================================

-- Add language column to media_catalog
ALTER TABLE media_catalog ADD COLUMN IF NOT EXISTS language VARCHAR(100) DEFAULT NULL;

-- Add main genre column to media_catalog
ALTER TABLE media_catalog ADD COLUMN IF NOT EXISTS main_genre VARCHAR(100) DEFAULT NULL;

-- Add sub genres column to media_catalog (TEXT to support multiple genres)
ALTER TABLE media_catalog ADD COLUMN IF NOT EXISTS sub_genres TEXT DEFAULT NULL;

-- =============================================================================
-- Performance Optimization Indexes
-- =============================================================================

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_media_language ON media_catalog(language);
CREATE INDEX IF NOT EXISTS idx_media_main_genre ON media_catalog(main_genre);
CREATE INDEX IF NOT EXISTS idx_media_sub_genres ON media_catalog(sub_genres(100));

-- Add unique constraint for name + language combination to prevent duplicates
ALTER TABLE media_catalog ADD CONSTRAINT uk_media_name_language UNIQUE (name, language);

-- =============================================================================
-- Enhanced Views for Media Catalog Analytics
-- =============================================================================

-- Drop and recreate the media catalog analytics view to include new fields
DROP VIEW IF EXISTS media_catalog_analytics;
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

-- Create view for genre analytics
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

-- Create view for language analytics
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

-- =============================================================================
-- Sample Data Updates with New Fields
-- =============================================================================

-- Update existing sample data with language, genre information
UPDATE media_catalog SET 
    language = 'English', 
    main_genre = 'Action', 
    sub_genres = 'Superhero,Adventure,Sci-Fi'
WHERE name = 'Avengers: Endgame';

UPDATE media_catalog SET 
    language = 'English', 
    main_genre = 'Horror', 
    sub_genres = 'Supernatural,Mystery,Drama'
WHERE name = 'Stranger Things S4';

UPDATE media_catalog SET 
    language = 'English', 
    main_genre = 'Pop', 
    sub_genres = 'Alternative,Indie Pop,Synth Pop'
WHERE name = 'Taylor Swift - Midnights';

UPDATE media_catalog SET 
    language = 'English', 
    main_genre = 'Documentary', 
    sub_genres = 'Nature,Wildlife,Environmental'
WHERE name = 'Our Planet';

-- =============================================================================
-- Genre and Language Reference Data
-- =============================================================================

-- Create temporary table for common movie genres
CREATE TEMPORARY TABLE IF NOT EXISTS common_movie_genres (
    genre_name VARCHAR(100) PRIMARY KEY,
    description TEXT
);

INSERT IGNORE INTO common_movie_genres VALUES
('Action', 'High-energy films with physical stunts, chases, and battles'),
('Adventure', 'Stories involving exciting journeys or quests'),
('Comedy', 'Films designed to entertain and amuse audiences'),
('Drama', 'Character-driven stories with emotional themes'),
('Horror', 'Films intended to frighten, unsettle, or create suspense'),
('Romance', 'Stories centered around love relationships'),
('Sci-Fi', 'Science fiction stories with futuristic elements'),
('Thriller', 'Suspenseful films with tension and excitement'),
('Fantasy', 'Stories with magical or supernatural elements'),
('Crime', 'Films dealing with criminal activities'),
('Documentary', 'Non-fiction films presenting real events or subjects'),
('Biography', 'Life stories of real people'),
('Animation', 'Films created using animated techniques'),
('Musical', 'Films incorporating songs and dance numbers'),
('Western', 'Stories set in the American Old West');

-- Create temporary table for common languages
CREATE TEMPORARY TABLE IF NOT EXISTS common_languages (
    language_code VARCHAR(10) PRIMARY KEY,
    language_name VARCHAR(100),
    native_name VARCHAR(100)
);

INSERT IGNORE INTO common_languages VALUES
('en', 'English', 'English'),
('es', 'Spanish', 'Español'),
('fr', 'French', 'Français'),
('de', 'German', 'Deutsch'),
('it', 'Italian', 'Italiano'),
('pt', 'Portuguese', 'Português'),
('ru', 'Russian', 'Русский'),
('ja', 'Japanese', '日本語'),
('ko', 'Korean', '한국어'),
('zh', 'Chinese', '中文'),
('ar', 'Arabic', 'العربية'),
('hi', 'Hindi', 'हिन्दी'),
('ta', 'Tamil', 'தமிழ்'),
('te', 'Telugu', 'తెలుగు'),
('ml', 'Malayalam', 'മലയാളം'),
('kn', 'Kannada', 'ಕನ್ನಡ');

-- =============================================================================
-- Data Validation Functions
-- =============================================================================

-- Function to validate genre format in sub_genres
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

-- =============================================================================
-- Analytics Queries for New Fields
-- =============================================================================

-- Query: Media distribution by language
SELECT 
    COALESCE(language, 'Not Specified') as language,
    COUNT(*) as total_count,
    COUNT(CASE WHEN download_status = 'DOWNLOADED' THEN 1 END) as downloaded_count,
    ROUND(COUNT(CASE WHEN download_status = 'DOWNLOADED' THEN 1 END) * 100.0 / COUNT(*), 2) as download_percentage
FROM media_catalog
GROUP BY language
ORDER BY total_count DESC;

-- Query: Media distribution by main genre
SELECT 
    COALESCE(main_genre, 'Not Specified') as main_genre,
    COUNT(*) as total_count,
    COUNT(CASE WHEN type = 'MOVIE' THEN 1 END) as movie_count,
    COUNT(CASE WHEN type = 'WEB_SERIES' THEN 1 END) as series_count,
    COUNT(CASE WHEN type = 'DOCUMENTARY' THEN 1 END) as documentary_count
FROM media_catalog
GROUP BY main_genre
ORDER BY total_count DESC;

-- Query: Most common sub-genres (parsing comma-separated values)
SELECT 
    TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(sub_genres, ',', numbers.n), ',', -1)) as sub_genre,
    COUNT(*) as usage_count,
    GROUP_CONCAT(DISTINCT type ORDER BY type) as media_types
FROM media_catalog
JOIN (SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 
      UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10) numbers
ON CHAR_LENGTH(COALESCE(sub_genres, '')) - CHAR_LENGTH(REPLACE(COALESCE(sub_genres, ''), ',', '')) >= numbers.n - 1
WHERE sub_genres IS NOT NULL 
  AND TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(sub_genres, ',', numbers.n), ',', -1)) != ''
GROUP BY TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(sub_genres, ',', numbers.n), ',', -1))
ORDER BY usage_count DESC
LIMIT 20;

-- =============================================================================
-- Data Validation and Integrity Checks
-- =============================================================================

-- Check for records with malformed sub_genres
SELECT id, name, sub_genres, 'Malformed sub_genres format' as issue
FROM media_catalog
WHERE sub_genres IS NOT NULL 
  AND NOT ValidateSubGenres(sub_genres);

-- Check for records missing essential genre information
SELECT id, name, type, main_genre, language, 'Missing genre/language info' as issue
FROM media_catalog
WHERE (main_genre IS NULL OR main_genre = '')
  AND (language IS NULL OR language = '')
  AND type IN ('MOVIE', 'WEB_SERIES', 'DOCUMENTARY');

-- =============================================================================
-- Migration Verification
-- =============================================================================

-- Verify column additions
SHOW COLUMNS FROM media_catalog WHERE Field IN ('language', 'main_genre', 'sub_genres');

-- Verify indexes
SHOW INDEX FROM media_catalog WHERE Key_name LIKE 'idx_media_%';

-- Test validation function
SELECT 
    'Sub-Genres Validation Test' as test_type,
    ValidateSubGenres('Action,Drama,Thriller') as valid_case,
    ValidateSubGenres(',Action,Drama,') as invalid_case,
    ValidateSubGenres(NULL) as null_case;

-- Summary statistics with new fields
SELECT 
    'Total Media Records' as metric,
    COUNT(*) as value
FROM media_catalog
UNION ALL
SELECT 
    'Records with Language Info' as metric,
    COUNT(*) as value
FROM media_catalog
WHERE language IS NOT NULL AND language != ''
UNION ALL
SELECT 
    'Records with Genre Info' as metric,
    COUNT(*) as value
FROM media_catalog
WHERE main_genre IS NOT NULL AND main_genre != ''
UNION ALL
SELECT 
    'Records with Sub-Genres' as metric,
    COUNT(*) as value
FROM media_catalog
WHERE sub_genres IS NOT NULL AND sub_genres != '';

-- =============================================================================
-- End of Migration Script v1.4.0
-- =============================================================================