# CineMitr Data Tracker API Documentation

## Overview

The CineMitr Data Tracker provides a comprehensive REST API for managing media content, download links, upload processes, and analytics. All endpoints follow RESTful conventions and return JSON responses.

**Base URL**: `http://localhost:8081/api`

## Authentication

Currently, the API does not require authentication. All endpoints are publicly accessible.

## Response Format

### Success Response
```json
{
  "data": [...],
  "status": "success"
}
```

### Error Response
```json
{
  "error": "Error message",
  "status": "error",
  "details": "Detailed error information"
}
```

---

## Media Catalog API

### Get All Media
**Endpoint**: `GET /api/media`

**Description**: Retrieve all media entries with pagination support

**Response**:
```json
[
  {
    "id": 1,
    "mediaName": "The Dark Knight",
    "mediaType": "Movie",
    "language": "English",
    "mainGenres": "Action",
    "subGenres": "Superhero, Crime",
    "isDownloaded": true,
    "availableOn": "Netflix, HBO Max",
    "downloadPath": "/media/movies/dark_knight.mp4"
  }
]
```

### Get Media by ID
**Endpoint**: `GET /api/media/{id}`

**Parameters**:
- `id` (path) - Media ID (required)

**Response**: Single media object or 404 if not found

### Create Media
**Endpoint**: `POST /api/media`

**Request Body**:
```json
{
  "mediaName": "Avatar",
  "mediaType": "Movie",
  "language": "English", 
  "mainGenres": "Action",
  "subGenres": "Fantasy, Adventure",
  "isDownloaded": false,
  "availableOn": "Disney+",
  "downloadPath": ""
}
```

**Validation Rules**:
- `mediaName` + `mediaType` combination must be unique
- `mediaType`: Must be one of "Movie", "Web-Series", "Documentary"
- `language`: Optional, defaults to null
- All other fields: Optional

### Update Media
**Endpoint**: `PUT /api/media/{id}`

**Parameters**:
- `id` (path) - Media ID (required)

**Request Body**: Same as Create Media

**Response**: Updated media object

### Delete Media
**Endpoint**: `DELETE /api/media/{id}`

**Parameters**:
- `id` (path) - Media ID (required)

**Response**: `204 No Content` on success

### Get Media Count
**Endpoint**: `GET /api/media/count`

**Response**:
```json
{
  "count": 42
}
```

---

## Content Catalog API

### Get All Content
**Endpoint**: `GET /api/content`

**Description**: Retrieve all content entries with associated media information

**Response**:
```json
[
  {
    "id": 1,
    "link": "https://example.com/video1",
    "mediaName": "The Dark Knight, Batman Begins",
    "mediaNamesList": ["The Dark Knight", "Batman Begins"],
    "mediaType": "Movie",
    "status": "downloaded",
    "priority": "high",
    "localStatus": "available",
    "localFilePath": "/local/path/video.mp4"
  }
]
```

### Get Content by ID
**Endpoint**: `GET /api/content/{id}`

**Parameters**:
- `id` (path) - Content ID (required)

### Create Content
**Endpoint**: `POST /api/content`

**Request Body**:
```json
{
  "link": "https://example.com/new-movie",
  "mediaName": "New Movie, Another Movie",
  "mediaType": "Movie",
  "status": "new",
  "priority": "medium",
  "localStatus": "not-available",
  "localFilePath": ""
}
```

**Multi-Media Support**: 
- Use comma-separated values in `mediaName` for multiple media associations
- Non-existing media will be auto-created with default values

**Auto-Sync Feature**:
- Creating content automatically creates corresponding upload entry with "new-content" status

**Validation Rules**:
- `link`: Required, must be valid URL format
- `status`: Must be one of "new", "downloaded", "error"
- `priority`: Must be one of "low", "medium", "high", "urgent"
- `localStatus`: Must be one of "available", "not-available", "local", "processing"

### Update Content
**Endpoint**: `PUT /api/content/{id}`

**Parameters**:
- `id` (path) - Content ID (required)

**Request Body**: Same as Create Content

### Delete Content
**Endpoint**: `DELETE /api/content/{id}`

**Parameters**:
- `id` (path) - Content ID (required)

### Get Content Count
**Endpoint**: `GET /api/content/count`

**Response**:
```json
{
  "count": 28
}
```

---

## Upload Catalog API

### Get All Uploads
**Endpoint**: `GET /api/upload`

**Description**: Retrieve all upload entries with metadata and media associations

**Response**:
```json
[
  {
    "id": 1,
    "sourceLink": "https://example.com/video1",
    "mediaName": "The Dark Knight",
    "mediaNamesList": ["The Dark Knight"],
    "mediaType": "Movie",
    "sourceData": "Upload source information",
    "status": "completed",
    "mediaData": "HD Video",
    "metadata": "Comprehensive metadata about the upload process"
  }
]
```

### Get Upload by ID
**Endpoint**: `GET /api/upload/{id}`

**Parameters**:
- `id` (path) - Upload ID (required)

### Create Upload
**Endpoint**: `POST /api/upload`

**Request Body**:
```json
{
  "sourceLink": "https://example.com/new-upload",
  "mediaName": "New Movie, Another Movie", 
  "mediaType": "Movie",
  "sourceData": "Source information",
  "status": "pending",
  "mediaData": "4K Video",
  "metadata": "Detailed metadata up to 9000 characters"
}
```

**Special Features**:

1. **Auto-Content Creation**:
   - If `sourceLink` matches existing content, links to it
   - If `sourceLink` is new, creates corresponding content entry
   - Empty `sourceLink` creates upload without content association

2. **Multi-Media Support**:
   - Comma-separated `mediaName` creates multiple media associations
   - Auto-creates non-existing media with default values

3. **Metadata Field**:
   - Supports up to 9000 characters
   - Optional field for detailed upload information

**Validation Rules**:
- `status`: Must be one of "pending", "new-content", "completed", "downloaded", "in-progress", "blocked", "ready-to-upload", "uploaded"
- `mediaData` (Media Format): Optional, common values include "HD Video", "4K Video", "Full HD", "Web-DL", "BluRay", "TV-Rip", "Web-Rip"
- `sourceData`: Auto-created if not provided
- `metadata`: Optional, up to 9000 characters

### Update Upload
**Endpoint**: `PUT /api/upload/{id}`

**Parameters**:
- `id` (path) - Upload ID (required)

**Request Body**: Same as Create Upload

### Delete Upload
**Endpoint**: `DELETE /api/upload/{id}`

**Parameters**:
- `id` (path) - Upload ID (required)

### Get Upload Count
**Endpoint**: `GET /api/upload/count`

**Response**:
```json
{
  "count": 15
}
```

---

## Statistics API

### Get All Statistics
**Endpoint**: `GET /api/states`

**Description**: Retrieve all statistics entries for analytics dashboard

**Response**:
```json
[
  {
    "id": 1,
    "date": "2024-01-15",
    "totalViews": 15420.0,
    "subscribers": 1250.0,
    "interaction": 850.0,
    "page": "CINE.MITR"
  }
]
```

### Get Statistics by ID
**Endpoint**: `GET /api/states/{id}`

**Parameters**:
- `id` (path) - Statistics ID (required)

### Create Statistics
**Endpoint**: `POST /api/states`

**Request Body**:
```json
{
  "date": "2024-01-20",
  "totalViews": 18500,
  "subscribers": 1320,
  "interaction": 1150,
  "page": "CINE.MITR.MUSIC"
}
```

**Validation Rules**:
- `date`: Required, format YYYY-MM-DD
- `date` + `page` combination must be unique
- `page`: Must be one of "CINE.MITR", "CINE.MITR.MUSIC"
- Numeric fields: Required, must be valid numbers

### Update Statistics
**Endpoint**: `PUT /api/states/{id}`

**Parameters**:
- `id` (path) - Statistics ID (required)

**Request Body**: Same as Create Statistics

### Delete Statistics
**Endpoint**: `DELETE /api/states/{id}`

**Parameters**:
- `id` (path) - Statistics ID (required)

### Get Statistics Count
**Endpoint**: `GET /api/states/count`

**Response**:
```json
{
  "count": 365
}
```

---

## Advanced Features

### Many-to-Many Relationships

The API supports complex relationships between entities:

1. **Content-Media**: Multiple media can be associated with single content entry
2. **Upload-Media**: Multiple media can be associated with single upload entry
3. **Automatic Junction Tables**: Backend manages relationship tables automatically

### Bi-directional Synchronization

1. **Content → Upload**: Creating content automatically creates upload with "new-content" status
2. **Upload → Content**: New source links automatically create corresponding content entries

### Data Validation

1. **Unique Constraints**:
   - Media: `mediaName` + `mediaType` must be unique
   - Statistics: `date` + `page` must be unique

2. **Enum Validation**: All status and category fields validated against predefined values

3. **Foreign Key Integrity**: Relationships maintained automatically with proper cascade options

### Error Handling

**Common HTTP Status Codes**:
- `200 OK` - Successful GET, PUT requests
- `201 Created` - Successful POST requests  
- `204 No Content` - Successful DELETE requests
- `400 Bad Request` - Validation errors, malformed JSON
- `404 Not Found` - Resource not found
- `409 Conflict` - Unique constraint violations
- `500 Internal Server Error` - Server-side errors

**Error Response Examples**:
```json
{
  "error": "Validation failed",
  "status": "error", 
  "details": "Media with name 'Avatar' and type 'Movie' already exists"
}
```

### Bulk Operations

While individual endpoints handle single entities, the system supports:
1. **CSV Import**: Use provided templates for bulk data import
2. **Batch Processing**: Multiple entities can be created via repeated API calls
3. **Auto-Creation**: Related entities created automatically when needed

---

## Usage Examples

### Creating Movie with Content and Upload

1. **Create Media** (optional - will be auto-created):
```bash
curl -X POST http://localhost:8081/api/media \
  -H "Content-Type: application/json" \
  -d '{"mediaName":"New Movie","mediaType":"Movie","language":"English"}'
```

2. **Create Content** (auto-creates upload):
```bash
curl -X POST http://localhost:8081/api/content \
  -H "Content-Type: application/json" \
  -d '{"link":"https://example.com/movie","mediaName":"New Movie","status":"new","priority":"high","localStatus":"not-available"}'
```

3. **Update Upload Status**:
```bash
curl -X PUT http://localhost:8081/api/upload/1 \
  -H "Content-Type: application/json" \
  -d '{"status":"completed","mediaData":"HD Video","metadata":"Upload completed successfully"}'
```

### Multi-Media Content

Create content linking multiple media:
```bash
curl -X POST http://localhost:8081/api/content \
  -H "Content-Type: application/json" \
  -d '{"link":"https://example.com/collection","mediaName":"Movie 1, Movie 2, Movie 3","status":"new","priority":"medium","localStatus":"not-available"}'
```

### Statistics Tracking

Add daily analytics:
```bash
curl -X POST http://localhost:8081/api/states \
  -H "Content-Type: application/json" \
  -d '{"date":"2024-01-21","totalViews":20000,"subscribers":1400,"interaction":1200,"page":"CINE.MITR"}'
```

---

## Rate Limiting

Currently, no rate limiting is implemented. Consider implementing rate limiting for production use.

## Versioning

Current API version: **v1**  
All endpoints are backward compatible within major versions.

## Support

For API issues or questions:
1. Check application logs for detailed error messages
2. Verify request format matches documentation examples
3. Ensure all required fields are provided with valid values
4. Review database constraints and validation rules

---

**Last Updated**: September 2025  
**API Version**: 1.0.0  
**Application Version**: 1.0.0