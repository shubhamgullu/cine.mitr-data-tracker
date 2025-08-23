-- =============================================================================
-- Cine Mitr - Movie Instagram Dashboard Database Schema
-- =============================================================================
-- This file contains all table definitions and sample data for the movie Instagram dashboard
-- Compatible with MySQL, PostgreSQL, and H2 databases
-- =============================================================================

-- Drop existing tables (if recreating)
DROP TABLE IF EXISTS movie_instagram_links;

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
-- Sample Data for Testing and Development
-- =============================================================================

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