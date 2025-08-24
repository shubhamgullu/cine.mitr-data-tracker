-- =============================================================================
-- Cine Mitr - Movie Instagram Dashboard Database Schema
-- =============================================================================
-- This file contains all table definitions and sample data for the movie Instagram dashboard
-- Compatible with MySQL, PostgreSQL, and H2 databases
-- =============================================================================

-- Drop existing tables (if recreating)
DROP TABLE IF EXISTS movie_instagram_links;
DROP TABLE IF EXISTS media_catalog;
DROP TABLE IF EXISTS content_catalog;
DROP TABLE IF EXISTS upload_catalog;
DROP TABLE IF EXISTS states_catalog;

-- =============================================================================
-- Table: movie_instagram_links
-- Purpose: Stores movie Instagram links with analytics and metadata
-- =============================================================================
CREATE TABLE movie_instagram_links (
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
    
    -- Indexes for better query performance
    INDEX idx_movie_name (movie_name),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_view_count (view_count DESC),
    INDEX idx_click_count (click_count DESC)
);

-- =============================================================================
-- Table: media_catalog
-- Purpose: Stores media information including movies, albums, web series, and documentaries
-- =============================================================================
CREATE TABLE media_catalog (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    platform VARCHAR(100),
    download_status VARCHAR(50) DEFAULT 'NOT_DOWNLOADED',
    location VARCHAR(500),
    description TEXT,
    fun_facts TEXT,
    created_by VARCHAR(100) DEFAULT 'system',
    updated_by VARCHAR(100) DEFAULT 'system',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes for better query performance
    INDEX idx_media_name (name),
    INDEX idx_media_type (type),
    INDEX idx_platform (platform),
    INDEX idx_download_status (download_status),
    INDEX idx_media_created_on (created_on)
);

-- =============================================================================
-- Table: content_catalog
-- Purpose: Tracks content links with status, priority, and metadata
-- =============================================================================
CREATE TABLE content_catalog (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    link VARCHAR(500) NOT NULL,
    media_catalog_type VARCHAR(50) NOT NULL,
    media_catalog_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    location VARCHAR(500),
    metadata TEXT,
    like_states VARCHAR(20),
    comment_states TEXT,
    upload_content_status VARCHAR(50),
    created_by VARCHAR(100) DEFAULT 'system',
    updated_by VARCHAR(100) DEFAULT 'system',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes for better query performance
    INDEX idx_content_status (status),
    INDEX idx_content_type (media_catalog_type),
    INDEX idx_content_name (media_catalog_name),
    INDEX idx_content_priority (priority),
    INDEX idx_content_created_on (created_on)
);

-- =============================================================================
-- Table: upload_catalog
-- Purpose: Manages upload operations with status and location tracking
-- =============================================================================
CREATE TABLE upload_catalog (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_catalog_link VARCHAR(500) NOT NULL,
    media_catalog_type VARCHAR(50) NOT NULL,
    media_catalog_name VARCHAR(255) NOT NULL,
    content_catalog_location VARCHAR(500),
    upload_catalog_location VARCHAR(500),
    upload_status VARCHAR(50) NOT NULL,
    upload_catalog_caption TEXT,
    created_by VARCHAR(100) DEFAULT 'system',
    updated_by VARCHAR(100) DEFAULT 'system',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes for better query performance
    INDEX idx_upload_status (upload_status),
    INDEX idx_upload_type (media_catalog_type),
    INDEX idx_upload_name (media_catalog_name),
    INDEX idx_upload_created_on (created_on)
);

-- =============================================================================
-- Table: states_catalog
-- Purpose: Tracks comprehensive user analytics and engagement metrics
-- =============================================================================
CREATE TABLE states_catalog (
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
-- Sample Data for Testing and Development
-- =============================================================================

-- Sample Media Catalog Data
INSERT INTO media_catalog (name, type, platform, download_status, location, description, fun_facts) VALUES
('Avengers: Endgame', 'MOVIE', 'netflix', 'DOWNLOADED', '/movies/avengers_endgame.mp4', 'Epic superhero saga conclusion', 'Highest grossing movie of all time'),
('Stranger Things S4', 'WEB_SERIES', 'netflix', 'NOT_DOWNLOADED', NULL, 'Supernatural drama series', 'Shot in multiple countries'),
('Taylor Swift - Midnights', 'ALBUM', 'spotify', 'PARTIALLY_DOWNLOADED', '/music/midnights/', 'Latest album release', 'Written during sleepless nights'),
('Our Planet', 'DOCUMENTARY', 'netflix', 'DOWNLOADED', '/docs/our_planet.mp4', 'Nature documentary series', 'Narrated by David Attenborough');

-- Sample Content Catalog Data  
INSERT INTO content_catalog (link, media_catalog_type, media_catalog_name, status, priority, location, metadata, like_states, comment_states, upload_content_status) VALUES
('https://instagram.com/p/avengers-action', 'MOVIE', 'Avengers: Endgame', 'DOWNLOADED', 'HIGH', '/content/avengers_clip.mp4', 'Duration: 60s, Resolution: 1080p', 'LIKED', 'Amazing action sequence!', 'UPLOADED'),
('https://youtube.com/watch?v=stranger-things', 'WEB_SERIES', 'Stranger Things S4', 'NEW', 'MEDIUM', NULL, 'Trailer content', NULL, NULL, NULL),
('https://spotify.com/track/midnights', 'ALBUM', 'Taylor Swift - Midnights', 'IN_PROGRESS', 'HIGH', '/music/preview.mp3', 'Preview track: 30s', 'LIKED', 'Love this song!', 'PENDING_UPLOAD');

-- Sample Upload Catalog Data
INSERT INTO upload_catalog (content_catalog_link, media_catalog_type, media_catalog_name, content_catalog_location, upload_catalog_location, upload_status, upload_catalog_caption) VALUES
('https://instagram.com/p/avengers-action', 'MOVIE', 'Avengers: Endgame', '/content/avengers_clip.mp4', '/uploads/instagram/avengers_post.mp4', 'COMPLETED', 'Epic battle scene from the Marvel saga! #Avengers #Marvel'),
('https://youtube.com/watch?v=music-video', 'ALBUM', 'Taylor Swift - Midnights', '/music/midnights_mv.mp4', '/uploads/youtube/midnights_upload.mp4', 'IN_PROGRESS', 'Official music video for Midnights album #TaylorSwift'),
('https://tiktok.com/@username/video', 'WEB_SERIES', 'Stranger Things S4', '/series/st4_clip.mp4', '/uploads/tiktok/st4_teaser.mp4', 'UPLOADED', 'Stranger Things Season 4 is here! 🔥 #StrangerThings');

-- Sample States Catalog Data
INSERT INTO states_catalog (views, subscribers, interactions, total_content, reach, impressions, profile_visits, website_clicks, email_clicks, call_clicks, followers_gained, followers_lost, reels_count, stories_count, avg_engagement_rate) VALUES
(15000, 2500, 1200, 45, 12000, 18000, 850, 120, 25, 8, 150, 12, 25, 80, 7.85),
(22000, 3200, 1800, 67, 19000, 28000, 1200, 180, 35, 15, 220, 18, 35, 120, 9.12),
(8500, 1200, 650, 28, 7200, 11000, 450, 75, 12, 3, 85, 8, 15, 45, 6.23);

-- Action Movies
INSERT INTO movie_instagram_links (movie_name, category, instagram_link, description, status, view_count, click_count) VALUES
('Avengers: Endgame', 'Action', 'https://instagram.com/p/avengers-endgame-action', 'Epic final battle scene from the Marvel superhero saga', 'ACTIVE', 1500, 89),
('John Wick 4', 'Action', 'https://instagram.com/p/john-wick-4-stunts', 'Behind the scenes of incredible action sequences', 'ACTIVE', 2300, 156),
('Fast X', 'Action', 'https://instagram.com/p/fast-x-cars', 'High-octane car chase sequences and stunts', 'ACTIVE', 1800, 134);

-- Comedy Movies  
INSERT INTO movie_instagram_links (movie_name, category, instagram_link, description, status, view_count, click_count) VALUES
('The Hangover', 'Comedy', 'https://instagram.com/p/hangover-funny-moments', 'Hilarious moments and bloopers from the comedy classic', 'ACTIVE', 950, 67),
('Superbad', 'Comedy', 'https://instagram.com/p/superbad-cast-reunion', 'Cast reunion and funny behind-the-scenes moments', 'ACTIVE', 1200, 78),
('Pineapple Express', 'Comedy', 'https://instagram.com/p/pineapple-express-comedy', 'Comedy gold moments and improvised scenes', 'ACTIVE', 880, 45);

-- Drama Movies
INSERT INTO movie_instagram_links (movie_name, category, instagram_link, description, status, view_count, click_count) VALUES
('The Shawshank Redemption', 'Drama', 'https://instagram.com/p/shawshank-redemption-scenes', 'Iconic scenes from the timeless prison drama', 'ACTIVE', 2100, 189),
('Forrest Gump', 'Drama', 'https://instagram.com/p/forrest-gump-quotes', 'Memorable quotes and emotional moments', 'ACTIVE', 1750, 145),
('The Godfather', 'Drama', 'https://instagram.com/p/godfather-classic-scenes', 'Classic scenes from the mafia masterpiece', 'ACTIVE', 1950, 167);

-- Horror Movies
INSERT INTO movie_instagram_links (movie_name, category, instagram_link, description, status, view_count, click_count) VALUES
('The Conjuring', 'Horror', 'https://instagram.com/p/conjuring-scary-moments', 'Spine-chilling scenes from the horror classic', 'ACTIVE', 1400, 98),
('Halloween', 'Horror', 'https://instagram.com/p/halloween-michael-myers', 'Michael Myers terrifying moments compilation', 'ACTIVE', 1100, 76),
('A Quiet Place', 'Horror', 'https://instagram.com/p/quiet-place-suspense', 'Tense and suspenseful moments without sound', 'ACTIVE', 1300, 89);

-- Romance Movies
INSERT INTO movie_instagram_links (movie_name, category, instagram_link, description, status, view_count, click_count) VALUES
('Titanic', 'Romance', 'https://instagram.com/p/titanic-love-story', 'Epic love story scenes from the ocean liner tragedy', 'ACTIVE', 2200, 198),
('The Notebook', 'Romance', 'https://instagram.com/p/notebook-romantic-scenes', 'Heart-melting romantic moments and quotes', 'ACTIVE', 1850, 167),
('Pride and Prejudice', 'Romance', 'https://instagram.com/p/pride-prejudice-romance', 'Period romance at its finest with stunning visuals', 'ACTIVE', 1600, 134);

-- Thriller Movies
INSERT INTO movie_instagram_links (movie_name, category, instagram_link, description, status, view_count, click_count) VALUES
('Inception', 'Thriller', 'https://instagram.com/p/inception-dream-sequences', 'Mind-bending dream sequences and plot twists', 'ACTIVE', 2400, 234),
('The Dark Knight', 'Thriller', 'https://instagram.com/p/dark-knight-joker', 'Jokers most iconic scenes and memorable quotes', 'ACTIVE', 2800, 267),
('Shutter Island', 'Thriller', 'https://instagram.com/p/shutter-island-mystery', 'Psychological thriller moments and plot reveals', 'ACTIVE', 1700, 145);

-- Science Fiction Movies
INSERT INTO movie_instagram_links (movie_name, category, instagram_link, description, status, view_count, click_count) VALUES
('Blade Runner 2049', 'Science Fiction', 'https://instagram.com/p/blade-runner-2049-visuals', 'Stunning futuristic visuals and cinematography', 'ACTIVE', 1500, 123),
('Interstellar', 'Science Fiction', 'https://instagram.com/p/interstellar-space-scenes', 'Epic space exploration and emotional moments', 'ACTIVE', 2100, 189),
('The Matrix', 'Science Fiction', 'https://instagram.com/p/matrix-action-sequences', 'Iconic bullet-time effects and philosophical themes', 'ACTIVE', 2000, 178);

-- Fantasy Movies
INSERT INTO movie_instagram_links (movie_name, category, instagram_link, description, status, view_count, click_count) VALUES
('Lord of the Rings', 'Fantasy', 'https://instagram.com/p/lotr-epic-battles', 'Epic battle sequences from Middle-earth saga', 'ACTIVE', 2500, 245),
('Harry Potter', 'Fantasy', 'https://instagram.com/p/harry-potter-magic', 'Magical moments and spellbinding scenes', 'ACTIVE', 2300, 223),
('Pan''s Labyrinth', 'Fantasy', 'https://instagram.com/p/pans-labyrinth-fantasy', 'Dark fantasy creatures and stunning visuals', 'ACTIVE', 1400, 112);

-- Documentary Movies  
INSERT INTO movie_instagram_links (movie_name, category, instagram_link, description, status, view_count, click_count) VALUES
('Free Solo', 'Documentary', 'https://instagram.com/p/free-solo-climbing', 'Death-defying rock climbing without ropes', 'ACTIVE', 1800, 156),
('Won''t You Be My Neighbor?', 'Documentary', 'https://instagram.com/p/mister-rogers-documentary', 'Heartwarming moments from Fred Rogers life', 'ACTIVE', 1200, 98),
('March of the Penguins', 'Documentary', 'https://instagram.com/p/march-penguins-nature', 'Beautiful nature documentary about penguin migration', 'ACTIVE', 1100, 87);

-- Animation Movies
INSERT INTO movie_instagram_links (movie_name, category, instagram_link, description, status, view_count, click_count) VALUES
('Toy Story 4', 'Animation', 'https://instagram.com/p/toy-story-4-animation', 'Behind the scenes of Pixar animation magic', 'ACTIVE', 1900, 167),
('Spider-Man: Into the Spider-Verse', 'Animation', 'https://instagram.com/p/spiderverse-animation-style', 'Revolutionary animation style and visual effects', 'ACTIVE', 2200, 198),
('Frozen', 'Animation', 'https://instagram.com/p/frozen-let-it-go', 'Let It Go and other memorable musical moments', 'ACTIVE', 2000, 189);

-- =============================================================================
-- Views for Analytics and Reporting
-- =============================================================================

-- View: Top performing links by views
CREATE VIEW top_viewed_links AS
SELECT 
    id,
    movie_name,
    category,
    instagram_link,
    view_count,
    click_count,
    (click_count * 100.0 / NULLIF(view_count, 0)) as click_through_rate,
    created_at
FROM movie_instagram_links
WHERE status = 'ACTIVE'
ORDER BY view_count DESC;

-- View: Category performance analytics
CREATE VIEW category_analytics AS
SELECT 
    category,
    COUNT(*) as total_links,
    SUM(view_count) as total_views,
    SUM(click_count) as total_clicks,
    AVG(view_count) as avg_views_per_link,
    AVG(click_count) as avg_clicks_per_link,
    (SUM(click_count) * 100.0 / NULLIF(SUM(view_count), 0)) as category_ctr
FROM movie_instagram_links
WHERE status = 'ACTIVE'
GROUP BY category
ORDER BY total_views DESC;

-- View: Recent activity (last 30 days)
CREATE VIEW recent_activity AS
SELECT 
    id,
    movie_name,
    category,
    view_count,
    click_count,
    created_at
FROM movie_instagram_links
WHERE status = 'ACTIVE' 
  AND created_at >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 30 DAY)
ORDER BY created_at DESC;

-- View: Media Catalog Analytics
CREATE VIEW media_catalog_analytics AS
SELECT 
    type,
    COUNT(*) as total_items,
    COUNT(CASE WHEN download_status = 'DOWNLOADED' THEN 1 END) as downloaded_count,
    COUNT(CASE WHEN download_status = 'NOT_DOWNLOADED' THEN 1 END) as not_downloaded_count,
    COUNT(CASE WHEN download_status = 'PARTIALLY_DOWNLOADED' THEN 1 END) as partial_count,
    platform,
    COUNT(*) as platform_count
FROM media_catalog
GROUP BY type, platform
ORDER BY total_items DESC;

-- View: Content Status Overview
CREATE VIEW content_status_overview AS
SELECT 
    status,
    COUNT(*) as total_count,
    media_catalog_type,
    priority,
    COUNT(CASE WHEN upload_content_status = 'UPLOADED' THEN 1 END) as uploaded_count
FROM content_catalog
GROUP BY status, media_catalog_type, priority
ORDER BY total_count DESC;

-- View: Upload Progress Tracking
CREATE VIEW upload_progress_tracking AS
SELECT 
    upload_status,
    COUNT(*) as count,
    media_catalog_type,
    AVG(CASE WHEN upload_status = 'COMPLETED' THEN 100 
             WHEN upload_status = 'IN_PROGRESS' THEN 50 
             WHEN upload_status = 'UPLOADED' THEN 100 
             ELSE 0 END) as completion_percentage
FROM upload_catalog
GROUP BY upload_status, media_catalog_type
ORDER BY count DESC;

-- View: Analytics Summary Dashboard
CREATE VIEW analytics_dashboard AS
SELECT 
    SUM(views) as total_views,
    SUM(subscribers) as total_subscribers,
    SUM(interactions) as total_interactions,
    SUM(total_content) as content_pieces,
    AVG(avg_engagement_rate) as avg_engagement,
    SUM(followers_gained) as followers_gained,
    SUM(followers_lost) as followers_lost,
    SUM(reels_count) as total_reels,
    SUM(stories_count) as total_stories
FROM states_catalog;

-- =============================================================================
-- Stored Procedures for Common Operations
-- =============================================================================

-- Procedure: Update link analytics
DELIMITER $$
CREATE PROCEDURE UpdateLinkAnalytics(
    IN link_id BIGINT,
    IN increment_views INT DEFAULT 0,
    IN increment_clicks INT DEFAULT 0
)
BEGIN
    UPDATE movie_instagram_links 
    SET 
        view_count = view_count + increment_views,
        click_count = click_count + increment_clicks,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = link_id AND status = 'ACTIVE';
END$$
DELIMITER ;

-- Procedure: Get category statistics
DELIMITER $$
CREATE PROCEDURE GetCategoryStats(IN category_name VARCHAR(100))
BEGIN
    SELECT 
        category,
        COUNT(*) as total_links,
        SUM(view_count) as total_views,
        SUM(click_count) as total_clicks,
        AVG(view_count) as avg_views,
        AVG(click_count) as avg_clicks
    FROM movie_instagram_links
    WHERE category = category_name AND status = 'ACTIVE'
    GROUP BY category;
END$$
DELIMITER ;

-- =============================================================================
-- Database Maintenance Scripts
-- =============================================================================

-- Clean up inactive links older than 1 year
DELETE FROM movie_instagram_links 
WHERE status = 'INACTIVE' 
  AND updated_at < DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 YEAR);

-- Archive old links (move to archive table if needed)
-- CREATE TABLE movie_instagram_links_archive AS SELECT * FROM movie_instagram_links WHERE created_at < DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 2 YEAR);

-- =============================================================================
-- Performance Optimization Queries  
-- =============================================================================

-- Query: Find trending content (high recent engagement)
SELECT 
    movie_name,
    category,
    view_count,
    click_count,
    (click_count * 100.0 / NULLIF(view_count, 0)) as ctr,
    DATEDIFF(CURRENT_DATE, DATE(created_at)) as days_old
FROM movie_instagram_links
WHERE status = 'ACTIVE'
  AND created_at >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 7 DAY)
  AND view_count > 100
ORDER BY view_count DESC, click_count DESC;

-- Query: Content gaps analysis (categories with few links)
SELECT 
    category,
    COUNT(*) as link_count
FROM movie_instagram_links
WHERE status = 'ACTIVE'
GROUP BY category
HAVING COUNT(*) < 5
ORDER BY link_count ASC;

-- =============================================================================
-- Data Export Queries
-- =============================================================================

-- Export all active links for backup
SELECT 
    id,
    movie_name,
    category,
    instagram_link,
    description,
    status,
    view_count,
    click_count,
    created_at,
    updated_at
FROM movie_instagram_links
WHERE status = 'ACTIVE'
ORDER BY created_at DESC;

-- Export analytics summary
SELECT 
    category,
    COUNT(*) as total_links,
    SUM(view_count) as total_views,
    SUM(click_count) as total_clicks,
    AVG(view_count) as avg_views,
    AVG(click_count) as avg_clicks,
    MAX(view_count) as max_views,
    MIN(view_count) as min_views
FROM movie_instagram_links
WHERE status = 'ACTIVE'
GROUP BY category
ORDER BY total_views DESC;

-- =============================================================================
-- End of Schema File
-- =============================================================================