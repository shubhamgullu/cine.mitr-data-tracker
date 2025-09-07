-- CineMitr Data Tracker Sample Data
-- Generated on 2025-09-07

-- Insert sample metadata status (base data for file paths)
INSERT INTO metadata_status (path_category, path, is_available, meta_data) VALUES
('MEDIA_FILE', '/media/movies/dark_knight.mp4', true, '{"size": "2.5GB", "format": "MP4", "quality": "HD", "duration": "152min"}'),
('MEDIA_FILE', '/media/movies/inception.mp4', true, '{"size": "3.1GB", "format": "MP4", "quality": "4K", "duration": "148min"}'),
('CONTENT_FILE', '/content/youtube/video1.json', true, '{"platform": "YouTube", "uploaded": "2024-01-15", "views": 15420}'),
('UPLOADED_FILE', '/uploads/temp/batch_001.zip', false, '{"status": "processing", "items": 5, "size": "890MB"}'),
('UPLOADED_FILE', '/uploads/completed/batch_002.zip', true, '{"status": "completed", "items": 3, "size": "1.2GB"}');

-- Insert sample media catalog
INSERT INTO media_catalog (media_type, media_name, language, is_downloaded, download_path, main_genres, sub_genres, available_on) VALUES
('Movie', 'The Dark Knight', 'English', true, 1, 'Action', 'Superhero, Crime, Thriller', 'Netflix, HBO Max, Amazon Prime'),
('Movie', 'Inception', 'English', true, 2, 'Sci-Fi', 'Thriller, Drama, Mystery', 'Netflix, Hulu'),
('Web-Series', 'Breaking Bad', 'English', false, null, 'Crime', 'Drama, Thriller', 'Netflix'),
('Movie', 'The Matrix', 'English', false, null, 'Sci-Fi', 'Action, Thriller', 'HBO Max, Amazon Prime'),
('Documentary', 'Planet Earth', 'English', false, null, 'Nature', 'Wildlife, Environment', 'BBC iPlayer, Discovery+');

-- Insert sample content catalog
INSERT INTO content_catalog (link, content_type, content_metadata, status, priority, local_status, local_file_path) VALUES
('https://youtube.com/watch?v=dark-knight-trailer', 'Video', 'Official trailer for The Dark Knight movie, high quality HD video content', 'downloaded', 'high', 'available', 3),
('https://vimeo.com/inception-behind-scenes', 'Video', 'Behind the scenes footage from Inception movie production', 'new', 'medium', 'not-available', null),
('https://youtube.com/watch?v=breaking-bad-recap', 'Video', 'Complete recap of Breaking Bad series seasons 1-5', 'new', 'low', 'processing', null),
('https://dailymotion.com/matrix-analysis', 'Video', 'Deep analysis of The Matrix trilogy and its philosophical themes', 'error', 'medium', 'not-available', null),
('https://youtube.com/watch?v=planet-earth-clip', 'Video', 'Amazing wildlife footage from Planet Earth documentary series', 'downloaded', 'high', 'local', 4);

-- Insert sample upload catalog
INSERT INTO upload_catalog (source_link_id, source_data, status, media_format, metadata) VALUES
(1, 1, 'completed', 'HD Video', 'Upload completed successfully. File processed and ready for distribution. Quality: 1080p, Size: 2.5GB'),
(2, 2, 'in-progress', '4K Video', 'Currently processing 4K video content. Estimated completion: 2 hours. Quality: 2160p, Size: 3.1GB'),
(3, 4, 'new-content', 'HD Video', 'Auto-generated upload entry from content: https://youtube.com/watch?v=breaking-bad-recap'),
(4, 5, 'blocked', 'SD Video', 'Upload blocked due to copyright issues. Awaiting manual review and approval from content team'),
(5, 3, 'ready-to-upload', 'HD Video', 'Content processed and ready for upload to distribution channels. Quality verified.');

-- Insert Many-to-Many relationships
-- Content Media relationships (link content to associated media)
INSERT INTO content_media_mapping (content_id, media_id) VALUES
(1, 1),  -- Dark Knight trailer linked to The Dark Knight movie
(2, 2),  -- Inception behind-scenes linked to Inception movie
(3, 3),  -- Breaking Bad recap linked to Breaking Bad series
(4, 4),  -- Matrix analysis linked to The Matrix movie
(5, 5);  -- Planet Earth clip linked to Planet Earth documentary

-- Upload Media relationships (link uploads to associated media)
INSERT INTO upload_media_mapping (upload_id, media_id) VALUES
(1, 1),  -- First upload linked to The Dark Knight
(2, 2),  -- Second upload linked to Inception
(3, 3),  -- Third upload linked to Breaking Bad
(4, 4),  -- Fourth upload linked to The Matrix
(5, 5);  -- Fifth upload linked to Planet Earth

-- Insert sample statistics/analytics data
INSERT INTO states_catalog (date, total_views, subscribers, interaction, content_id, page) VALUES
('2024-01-15', 15420.0, 1250.0, 850.0, 1, 'cine.mitr'),
('2024-01-16', 18900.0, 1275.0, 920.0, 2, 'cine.mitr'),
('2024-01-17', 22500.0, 1290.0, 1150.0, 3, 'cine.mitr'),
('2024-01-18', 19800.0, 1305.0, 980.0, 4, 'cine.mitr'),
('2024-01-19', 25600.0, 1320.0, 1280.0, 5, 'cine.mitr'),
('2024-01-20', 28400.0, 1340.0, 1420.0, 1, 'cine.mitr.music'),
('2024-01-21', 31200.0, 1365.0, 1580.0, 2, 'cine.mitr.music');