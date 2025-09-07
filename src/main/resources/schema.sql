-- CineMitr Data Tracker Database Schema
-- Generated from Entity POJOs on 2025-09-07

-- Drop all tables if they exist (in correct order to handle foreign key dependencies)
DROP TABLE IF EXISTS content_media_mapping;
DROP TABLE IF EXISTS upload_media_mapping;
DROP TABLE IF EXISTS states_catalog;
DROP TABLE IF EXISTS upload_catalog;
DROP TABLE IF EXISTS content_catalog;
DROP TABLE IF EXISTS media_catalog;
DROP TABLE IF EXISTS metadata_status;

-- Create metadata_status table (base table for file paths and metadata)
CREATE TABLE metadata_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    path_category VARCHAR(50) NOT NULL,
    path VARCHAR(1024) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT FALSE,
    meta_data TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_path_category CHECK (path_category IN ('MEDIA_FILE', 'CONTENT_FILE', 'UPLOADED_FILE'))
);

-- Create media_catalog table
CREATE TABLE media_catalog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    media_type VARCHAR(100) NOT NULL,
    media_name VARCHAR(500) NOT NULL,
    language VARCHAR(50),
    is_downloaded BOOLEAN NOT NULL DEFAULT FALSE,
    download_path BIGINT,
    main_genres VARCHAR(500),
    sub_genres VARCHAR(500),
    available_on VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_media_name_type UNIQUE (media_name, media_type),
    CONSTRAINT fk_media_download_path FOREIGN KEY (download_path) REFERENCES metadata_status(id) ON DELETE SET NULL
);

-- Create content_catalog table
CREATE TABLE content_catalog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    link VARCHAR(2048) NOT NULL UNIQUE,
    content_type VARCHAR(100),
    content_metadata TEXT,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    local_status VARCHAR(50) NOT NULL,
    local_file_path BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_content_local_path FOREIGN KEY (local_file_path) REFERENCES metadata_status(id) ON DELETE SET NULL
);

-- Create upload_catalog table
CREATE TABLE upload_catalog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_link_id BIGINT,
    source_data BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    media_format VARCHAR(100),
    metadata VARCHAR(9000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_upload_source_link FOREIGN KEY (source_link_id) REFERENCES content_catalog(id) ON DELETE SET NULL,
    CONSTRAINT fk_upload_source_data FOREIGN KEY (source_data) REFERENCES metadata_status(id) ON DELETE CASCADE
);

-- Create states_catalog table
CREATE TABLE states_catalog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    total_views DOUBLE NOT NULL,
    subscribers DOUBLE NOT NULL,
    interaction DOUBLE NOT NULL,
    content_id BIGINT,
    page VARCHAR(200) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_states_content FOREIGN KEY (content_id) REFERENCES content_catalog(id) ON DELETE SET NULL
);

-- Create many-to-many mapping table for content and media
CREATE TABLE content_media_mapping (
    content_id BIGINT NOT NULL,
    media_id BIGINT NOT NULL,
    PRIMARY KEY (content_id, media_id),
    CONSTRAINT fk_content_media_content FOREIGN KEY (content_id) REFERENCES content_catalog(id) ON DELETE CASCADE,
    CONSTRAINT fk_content_media_media FOREIGN KEY (media_id) REFERENCES media_catalog(id) ON DELETE CASCADE
);

-- Create many-to-many mapping table for upload and media
CREATE TABLE upload_media_mapping (
    upload_id BIGINT NOT NULL,
    media_id BIGINT NOT NULL,
    PRIMARY KEY (upload_id, media_id),
    CONSTRAINT fk_upload_media_upload FOREIGN KEY (upload_id) REFERENCES upload_catalog(id) ON DELETE CASCADE,
    CONSTRAINT fk_upload_media_media FOREIGN KEY (media_id) REFERENCES media_catalog(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_media_name ON media_catalog(media_name);
CREATE INDEX idx_media_type ON media_catalog(media_type);
CREATE INDEX idx_content_link ON content_catalog(link);
CREATE INDEX idx_content_status ON content_catalog(status);
CREATE INDEX idx_content_priority ON content_catalog(priority);
CREATE INDEX idx_upload_status ON upload_catalog(status);
CREATE INDEX idx_states_date ON states_catalog(date);
CREATE INDEX idx_metadata_category ON metadata_status(path_category);
CREATE INDEX idx_metadata_available ON metadata_status(is_available);

-- Add comments for documentation
COMMENT ON TABLE metadata_status IS 'Stores file paths and metadata information for various system components';
COMMENT ON TABLE media_catalog IS 'Stores media information including movies, web series, and documentaries';
COMMENT ON TABLE content_catalog IS 'Stores content links and their associated metadata and status';
COMMENT ON TABLE upload_catalog IS 'Stores upload information and status for content processing';
COMMENT ON TABLE states_catalog IS 'Stores analytics and statistics data';
COMMENT ON TABLE content_media_mapping IS 'Many-to-many mapping between content and media items';
COMMENT ON TABLE upload_media_mapping IS 'Many-to-many mapping between upload entries and media items';