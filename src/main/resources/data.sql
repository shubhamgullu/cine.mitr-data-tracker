-- Insert sample metadata status
INSERT INTO metadata_status (path_category, path, is_available, meta_data) VALUES
('MEDIA_FILE', '/media/movies/dark_knight.mp4', true, '{"size": "2.5GB", "format": "MP4", "quality": "HD"}'),
('MEDIA_FILE', '/local/path/movie.mp4', true, '{"size": "1.8GB", "format": "MP4", "quality": "FHD"}');

-- Insert sample media catalog
INSERT INTO media_catalog (media_type, media_name, language, is_downloaded, download_path, main_genres, sub_genres, available_on) VALUES
('Movie', 'The Dark Knight', 'English', true, 1, 'Action', 'Superhero, Crime', 'Netflix, HBO Max'),
('Movie', 'Sample Movie', 'English', true, 2, 'Drama', 'Thriller', 'Amazon Prime');

-- Insert sample content catalog
INSERT INTO content_catalog (link, media_id, status, priority, local_status, local_file_path) VALUES
('https://example.com/video1', 1, 'downloaded', 'high', 'downloaded', 2),
('https://example.com/video2', 2, 'new', 'medium', 'na', null);

-- Insert sample upload catalog
INSERT INTO upload_catalog (source_link_id, source_data, status, media_id) VALUES
(1, 1, 'completed', 1),
(2, 2, 'in-progress', 2);

-- Insert sample stats catalog
INSERT INTO stats_catalog (date, total_views, subscribers, interaction, content_id, page) VALUES
('2024-01-15', 15420, 1250, 850, 1, 'CINE.MITR'),
('2024-01-16', 16800, 1275, 920, 2, 'CINE.MITR.MUSIC');