package com.cinemitr.datatracker.config;

import com.cinemitr.datatracker.entity.MediaCatalog;
import com.cinemitr.datatracker.entity.MetadataStatus;
import com.cinemitr.datatracker.entity.ContentCatalog;
import com.cinemitr.datatracker.entity.UploadCatalog;
import com.cinemitr.datatracker.entity.StatesCatalog;
import com.cinemitr.datatracker.enums.PathCategory;
import com.cinemitr.datatracker.repository.MediaCatalogRepository;
import com.cinemitr.datatracker.repository.MetadataStatusRepository;
import com.cinemitr.datatracker.repository.ContentCatalogRepository;
import com.cinemitr.datatracker.repository.UploadCatalogRepository;
import com.cinemitr.datatracker.repository.StatsCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private MetadataStatusRepository metadataStatusRepository;

    @Autowired
    private MediaCatalogRepository mediaCatalogRepository;

    @Autowired
    private ContentCatalogRepository contentCatalogRepository;

    @Autowired
    private UploadCatalogRepository uploadCatalogRepository;

    @Autowired
    private StatsCatalogRepository statsCatalogRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize data if the database is empty
        if (mediaCatalogRepository.count() == 0) {
            initializeSampleData();
        }
    }

    private void initializeSampleData() {
        try {
            // Create MetadataStatus entries
            MetadataStatus metadata1 = new MetadataStatus();
            metadata1.setPathCategory(PathCategory.MEDIA_FILE);
            metadata1.setPath("/media/movies/dark_knight.mp4");
            metadata1.setIsAvailable(true);
            metadata1.setMetaData("{\"size\": \"2.5GB\", \"format\": \"MP4\", \"quality\": \"HD\"}");
            metadata1 = metadataStatusRepository.save(metadata1);

            MetadataStatus metadata2 = new MetadataStatus();
            metadata2.setPathCategory(PathCategory.MEDIA_FILE);
            metadata2.setPath("/local/path/movie.mp4");
            metadata2.setIsAvailable(true);
            metadata2.setMetaData("{\"size\": \"1.8GB\", \"format\": \"MP4\", \"quality\": \"FHD\"}");
            metadata2 = metadataStatusRepository.save(metadata2);

            // Create MediaCatalog entries
            MediaCatalog media1 = new MediaCatalog();
            media1.setMediaType("Movie");
            media1.setMediaName("The Dark Knight");
            media1.setLanguage("English");
            media1.setIsDownloaded(true);
            media1.setDownloadPath(metadata1);
            media1.setMainGenres("Action");
            media1.setSubGenres("Superhero, Crime");
            media1.setAvailableOn("Netflix, HBO Max");
            media1 = mediaCatalogRepository.save(media1);

            MediaCatalog media2 = new MediaCatalog();
            media2.setMediaType("Movie");
            media2.setMediaName("Sample Movie");
            media2.setLanguage("English");
            media2.setIsDownloaded(true);
            media2.setDownloadPath(metadata2);
            media2.setMainGenres("Drama");
            media2.setSubGenres("Thriller");
            media2.setAvailableOn("Amazon Prime");
            media2 = mediaCatalogRepository.save(media2);

            // Create ContentCatalog entries
            ContentCatalog content1 = new ContentCatalog();
            content1.setLink("https://example.com/video1");
            content1.addMedia(media1);
            content1.setStatus("downloaded");
            content1.setPriority("high");
            content1.setLocalStatus("downloaded");
            content1.setLocalFilePath(metadata2);
            contentCatalogRepository.save(content1);

            ContentCatalog content2 = new ContentCatalog();
            content2.setLink("https://example.com/video2");
            content2.addMedia(media2);
            content2.setStatus("new");
            content2.setPriority("medium");
            content2.setLocalStatus("na");
            content2.setLocalFilePath(null);
            contentCatalogRepository.save(content2);

            // Create UploadCatalog entries
            UploadCatalog upload1 = new UploadCatalog();
            upload1.setSourceLink(content1);
            upload1.setSourceData(metadata1);
            upload1.setStatus("completed");
            upload1.addMedia(media1);
            uploadCatalogRepository.save(upload1);

            UploadCatalog upload2 = new UploadCatalog();
            upload2.setSourceLink(content2);
            upload2.setSourceData(metadata2);
            upload2.setStatus("in-progress");
            upload2.addMedia(media2);
            uploadCatalogRepository.save(upload2);

            // Create StatsCatalog entries
            StatesCatalog stats1 = new StatesCatalog();
            stats1.setDate(Date.from(LocalDate.of(2024, 1, 15).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            stats1.setTotalViews(15420.0);
            stats1.setSubscribers(1250.0);
            stats1.setInteraction(850.0);
            stats1.setContent(content1);
            stats1.setPage("CINE.MITR");
            statsCatalogRepository.save(stats1);

            StatesCatalog stats2 = new StatesCatalog();
            stats2.setDate(Date.from(LocalDate.of(2024, 1, 16).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            stats2.setTotalViews(16800.0);
            stats2.setSubscribers(1275.0);
            stats2.setInteraction(920.0);
            stats2.setContent(content2);
            stats2.setPage("CINE.MITR.MUSIC");
            statsCatalogRepository.save(stats2);

            System.out.println("Sample data initialized successfully!");
        } catch (Exception e) {
            System.err.println("Error initializing sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}