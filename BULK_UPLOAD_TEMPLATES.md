# Bulk Upload Templates

This document describes the CSV template files for bulk uploading data to the CineMitr Data Tracker application.

## Available Templates

### 1. Media Catalog Template (`sample_media.csv`)

**Purpose**: Bulk upload media entries (movies, web series, documentaries)

**Required Columns**:
- `media_name` - Name of the media (String, required)
- `media_type` - Type: Movie, Web-Series, or Documentary (String, required)
- `language` - Language of the media (String, optional)
- `main_genres` - Primary genre (String, optional)
- `sub_genres` - Secondary genres, comma-separated (String, optional)
- `is_downloaded` - Download status: Yes/No (String, required)
- `download_path` - Path to downloaded file (String, optional)
- `available_on` - Platforms where available (String, optional)

**Example**:
```csv
media_name,media_type,language,main_genres,sub_genres,is_downloaded,download_path,available_on
"The Matrix","Movie","English","Action","Sci-Fi, Cyberpunk","Yes","/media/movies/matrix.mp4","Netflix, Prime Video"
```

### 2. Content Catalog Template (`sample_content.csv`)

**Purpose**: Bulk upload content download links and metadata

**Required Columns**:
- `link` - Download URL (String, required)
- `content_type` - Type of content: Video, Audio, Document, Movie, Music, Image, Web-Series, Documentary (String, optional)
- `content_metadata` - Additional metadata, description, or information about the content (String, optional)
- `media_type` - Media type: Movie, Web-Series, Documentary (String, required)
- `media_name` - Associated media names, comma-separated for multiple (String, required)
- `status` - Download status: new, downloaded, error (String, required)
- `priority` - Priority level: low, medium, high, urgent (String, required)
- `local_status` - Local file status: available, not-available, local, processing (String, required)
- `local_file_path` - Path to local file (String, optional)

**Multi-Media Support**: Use comma-separated values in `media_name` column:
```csv
"https://example.com/multi_media","Video","Multi-content package","Movie","The Matrix, Free Solo","new","urgent","not-available",""
```

**Example CSV Format**:
```csv
link,content_type,content_metadata,media_type,media_name,status,priority,local_status,local_file_path
"https://example.com/video","Video","High-quality movie content","Movie","The Matrix","new","high","downloaded","/path/file"
```

### 3. Upload Catalog Template (`sample_upload.csv`)

**Purpose**: Bulk upload upload process entries

**Required Columns**:
- `source_link` - Source download URL (String, optional)
- `media_names` - Associated media names, comma-separated (String, optional)
- `media_type` - Media type: Movie, Web-Series, Documentary (String, optional)
- `source_data` - Upload source information (String, optional)
- `status` - Upload status: pending, new-content, completed, downloaded, in-progress, blocked, ready-to-upload, uploaded (String, required)
- `media_format` - Media format: HD Video, 4K Video, Full HD, Web-DL, BluRay, TV-Rip, Web-Rip (String, optional)
- `metadata` - Additional metadata (String, up to 9000 characters, optional)

**Special Features**:
- **Auto-Content Creation**: If `source_link` matches an existing content entry, it will be linked. If not, a new content entry is created automatically.
- **Multi-Media Support**: Multiple media names can be specified in the `media_names` column.
- **Empty Source Links**: Leave `source_link` empty for uploads without associated content.

### 4. Statistics Template (`sample_stats.csv`)

**Purpose**: Bulk upload analytics and statistics data

**Required Columns**:
- `date` - Date in YYYY-MM-DD format (String, required)
- `total_views` - Total view count (Number, required)
- `subscribers` - Subscriber count (Number, required)
- `interaction` - Interaction count (Number, required)  
- `page` - Page identifier: CINE.MITR or CINE.MITR.MUSIC (String, required)

## Import Guidelines

### General Rules

1. **CSV Format**: All files must be valid CSV with proper escaping
2. **Headers**: First row must contain exact column names as shown
3. **Encoding**: UTF-8 encoding recommended
4. **Quotes**: Use double quotes around text values containing commas or special characters

### Data Validation

1. **Required Fields**: Fields marked as required cannot be empty
2. **Enum Values**: Status and category fields must match exact values shown
3. **Unique Constraints**: 
   - Media: Combination of `media_name` + `media_type` must be unique
   - Stats: Combination of `date` + `page` must be unique
4. **Foreign Key Relations**: Media names referenced in content/upload must exist or will be auto-created

### Multi-Media Functionality

Both content and upload templates support multiple media associations:
- Separate media names with commas: `"Media 1, Media 2, Media 3"`
- Each media will be linked to the same content/upload entry
- Non-existing media will be auto-created with default values

### Auto-Sync Features

1. **Content-to-Upload Sync**: Adding content entries automatically creates corresponding upload entries with "new-content" status
2. **Media Auto-Creation**: Referenced media that doesn't exist will be created automatically
3. **Content Auto-Creation**: Upload entries with new source links create corresponding content entries

## Error Handling

- **Validation Errors**: Invalid enum values or constraint violations will be rejected
- **Duplicate Prevention**: Unique constraints prevent duplicate entries
- **Graceful Failures**: Individual row failures won't affect other rows
- **Error Messages**: Detailed error messages help identify and fix issues

## Import Process

1. **Prepare CSV**: Use templates as starting point, modify data as needed
2. **Validate Data**: Ensure all required fields and valid enum values
3. **Import via API**: Use appropriate API endpoints for bulk import
4. **Review Results**: Check for any errors or warnings in import response
5. **Verify Data**: Confirm imported data appears correctly in application

## Best Practices

1. **Start Small**: Test with a few rows before bulk importing
2. **Backup Data**: Always backup existing data before large imports
3. **Validate First**: Use small test imports to validate data format
4. **Monitor Relationships**: Verify Many-to-Many relationships are created correctly
5. **Check Constraints**: Ensure all constraint validations pass

## Example Workflows

### Adding New Movies
1. Update `sample_media.csv` with movie information
2. Update `sample_content.csv` with download links
3. Import media first, then content (auto-creates uploads)

### Bulk Upload Processing
1. Update `sample_upload.csv` with source links and metadata
2. Import uploads (auto-creates missing content and media entries)
3. Update status as processing progresses

### Analytics Import
1. Export existing statistics to understand current data format
2. Update `sample_stats.csv` with new analytics data
3. Import statistics for dashboard updates

---

**Note**: These templates reflect the latest database schema with Many-to-Many relationships, enhanced metadata support, and bi-directional synchronization features.