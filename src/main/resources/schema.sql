-- METADATA STATUS
CREATE TABLE metadata_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    path_category VARCHAR(255) NOT NULL,
    path VARCHAR(1024) NOT NULL,
    is_available BOOLEAN NOT NULL,
    meta_data CLOB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- MEDIA CATALOG
CREATE TABLE media_catalog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    media_type VARCHAR(255) NOT NULL,
    media_name VARCHAR(255) NOT NULL,
    language VARCHAR(255),
    is_downloaded BOOLEAN NOT NULL,
    download_path BIGINT,
    main_genres VARCHAR(255),
    sub_genres VARCHAR(255),
    available_on VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_media_name_type UNIQUE (media_name, media_type),
    CONSTRAINT fk_media_download_path FOREIGN KEY (download_path) REFERENCES metadata_status(id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE INDEX idx_media_type ON media_catalog(media_type);
CREATE INDEX idx_language ON media_catalog(language);

-- CONTENT CATALOG
CREATE TABLE content_catalog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    link VARCHAR(255) NOT NULL,
    media_id BIGINT,
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    local_status VARCHAR(20) NOT NULL,
    local_file_path BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_content_status CHECK (UPPER(status) IN ('NEW','DOWNLOADED','ERROR')),
    CONSTRAINT chk_content_priority CHECK (UPPER(priority) IN ('LOW','MEDIUM','HIGH')),
    CONSTRAINT chk_content_local_status CHECK (UPPER(local_status) IN ('DOWNLOADED','ERROR','NA')),
    CONSTRAINT fk_content_media FOREIGN KEY (media_id) REFERENCES media_catalog(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_content_local_file FOREIGN KEY (local_file_path) REFERENCES metadata_status(id) ON UPDATE CASCADE ON DELETE SET NULL
);

-- UPLOAD CATALOG
CREATE TABLE upload_catalog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_link_id BIGINT,
    source_data BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    media_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_upload_status CHECK (UPPER(status) IN ('COMPLETED','DOWNLOADED','IN-PROGRESS','BLOCKED','READY-TO-UPLOAD','UPLOADED')),
    CONSTRAINT fk_upload_source_data FOREIGN KEY (source_data) REFERENCES metadata_status(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_upload_source_link FOREIGN KEY (source_link_id) REFERENCES content_catalog(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_upload_media FOREIGN KEY (media_id) REFERENCES media_catalog(id) ON UPDATE CASCADE ON DELETE SET NULL
);

-- STATS CATALOG
CREATE TABLE stats_catalog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    total_views DOUBLE NOT NULL,
    subscribers DOUBLE NOT NULL,
    interaction DOUBLE NOT NULL,
    content_id BIGINT,
    page VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_stats_page CHECK (UPPER(page) IN ('CINE.MITR','CINE.MITR.MUSIC')),
    CONSTRAINT fk_stats_content FOREIGN KEY (content_id) REFERENCES content_catalog(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT uq_stats_day_page UNIQUE (date, page)
);