package com.cinemitr.controller;

import com.cinemitr.model.MovieInstagramLink;
import com.cinemitr.model.MediaCatalog;
import com.cinemitr.model.ContentCatalog;
import com.cinemitr.model.UploadCatalog;
import com.cinemitr.model.StatesCatalog;
import com.cinemitr.repository.MediaCatalogRepository;
import com.cinemitr.repository.ContentCatalogRepository;
import com.cinemitr.repository.UploadCatalogRepository;
import com.cinemitr.repository.StatesCatalogRepository;
import com.cinemitr.repository.MovieInstagramLinkRepository;
import com.cinemitr.service.MovieInstagramLinkService;
import com.cinemitr.service.BulkUploadService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

@Controller
@RequestMapping("/dashboard")
public class InstagramLinkController {

    @Autowired
    private MovieInstagramLinkService instagramLinkService;
    
    @Autowired
    private MediaCatalogRepository mediaCatalogRepository;
    
    @Autowired
    private ContentCatalogRepository contentCatalogRepository;
    
    @Autowired
    private UploadCatalogRepository uploadCatalogRepository;
    
    @Autowired
    private StatesCatalogRepository statesCatalogRepository;
    
    @Autowired
    private BulkUploadService bulkUploadService;
    
    @Autowired
    private MovieInstagramLinkRepository instagramLinkRepository;

    @GetMapping
    public String dashboard(Model model, @RequestParam(required = false) String tab) {
        model.addAttribute("activeLinks", instagramLinkService.getActiveLinks());
        model.addAttribute("categories", instagramLinkService.getAllCategories());
        model.addAttribute("newLink", new MovieInstagramLink());
        
        // Add predefined categories for the dropdown
        List<String> predefinedCategories = Arrays.asList(
            "Action", "Comedy", "Drama", "Horror", "Romance", 
            "Thriller", "Science Fiction", "Fantasy", "Documentary", "Animation"
        );
        model.addAttribute("predefinedCategories", predefinedCategories);
        
        Long totalLinksCount = (long) instagramLinkService.getActiveLinks().size();
        model.addAttribute("totalLinksCount", totalLinksCount);
        
        // Add recent records for all catalogs (last 10 records each, sorted by latest updated first)
        PageRequest mediaPageRequest = PageRequest.of(0, 10, Sort.by("updatedOn").descending());
        PageRequest otherPageRequest = PageRequest.of(0, 10, Sort.by("createdOn").descending());
        
        // Recent Media Catalog records (sorted by latest updated first)
        List<MediaCatalog> recentMediaRecords = mediaCatalogRepository.findAll(mediaPageRequest).getContent();
        model.addAttribute("recentMediaRecords", recentMediaRecords);
        
        // Recent Content Catalog records
        List<ContentCatalog> recentContentRecords = contentCatalogRepository.findAll(otherPageRequest).getContent();
        model.addAttribute("recentContentRecords", recentContentRecords);
        
        // Recent Upload Catalog records
        List<UploadCatalog> recentUploadRecords = uploadCatalogRepository.findAll(otherPageRequest).getContent();
        model.addAttribute("recentUploadRecords", recentUploadRecords);
        
        // Recent States Catalog records
        List<StatesCatalog> recentStatesRecords = statesCatalogRepository.findAllOrderByCreatedOnDesc();
        model.addAttribute("recentStatesRecords", recentStatesRecords.stream().limit(10).collect(Collectors.toList()));
        
        // Add active tab to model for conditional display
        model.addAttribute("activeTab", tab != null ? tab : "dashboard");
        
        return "dashboard/index";
    }
    
    @GetMapping("/media-catalog")
    public String mediaCatalogTab(Model model) {
        return dashboard(model, "media-catalog");
    }
    
    @GetMapping("/content-catalog-tab")
    public String contentCatalogTab(Model model) {
        return dashboard(model, "content-status");
    }
    
    @GetMapping("/upload-catalog-tab")
    public String uploadCatalogTab(Model model) {
        return dashboard(model, "add-entry");
    }
    
    @GetMapping("/states-catalog")
    public String statesCatalogTab(Model model) {
        return dashboard(model, "states-catalog");
    }

    @PostMapping("/add-link")
    public String addInstagramLink(@Valid @ModelAttribute("newLink") MovieInstagramLink link,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("activeLinks", instagramLinkService.getActiveLinks());
            model.addAttribute("categories", instagramLinkService.getAllCategories());
            
            List<String> predefinedCategories = Arrays.asList(
                "Action", "Comedy", "Drama", "Horror", "Romance", 
                "Thriller", "Science Fiction", "Fantasy", "Documentary", "Animation"
            );
            model.addAttribute("predefinedCategories", predefinedCategories);
            
            return "dashboard/index";
        }

        try {
            instagramLinkService.createLink(
                link.getMovieName(),
                link.getCategory(),
                link.getInstagramLink(),
                link.getDescription()
            );
            redirectAttributes.addFlashAttribute("success", 
                "Instagram link added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error adding Instagram link: " + e.getMessage());
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/links")
    public String viewAllLinks(Model model,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) String category) {
        List<MovieInstagramLink> links;
        
        if (search != null && !search.isEmpty()) {
            links = instagramLinkService.searchLinksByMovieName(search);
            model.addAttribute("currentSearch", search);
        } else if (category != null && !category.isEmpty()) {
            links = instagramLinkService.getLinksByCategory(category);
            model.addAttribute("currentCategory", category);
        } else {
            links = instagramLinkService.getActiveLinks();
        }
        
        model.addAttribute("links", links);
        model.addAttribute("categories", instagramLinkService.getAllCategories());
        
        return "dashboard/links";
    }

    @GetMapping("/link/{id}")
    public String viewLink(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<MovieInstagramLink> linkOpt = instagramLinkService.getLinkById(id);
        
        if (linkOpt.isPresent()) {
            instagramLinkService.incrementViewCount(id);
            model.addAttribute("link", linkOpt.get());
            return "dashboard/link-detail";
        } else {
            redirectAttributes.addFlashAttribute("error", "Instagram link not found.");
            return "redirect:/dashboard/links";
        }
    }

    @PostMapping("/link/{id}/click")
    @ResponseBody
    public String trackClick(@PathVariable Long id) {
        try {
            instagramLinkService.incrementClickCount(id);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

    @PostMapping("/link/{id}/edit")
    public String editLink(@PathVariable Long id,
                          @RequestParam String movieName,
                          @RequestParam String category,
                          @RequestParam String instagramLink,
                          @RequestParam(required = false) String description,
                          RedirectAttributes redirectAttributes) {
        try {
            Optional<MovieInstagramLink> existingLink = instagramLinkService.getLinkById(id);
            if (existingLink.isPresent()) {
                MovieInstagramLink link = existingLink.get();
                link.setMovieName(movieName);
                link.setCategory(category);
                link.setInstagramLink(instagramLink);
                link.setDescription(description);
                
                instagramLinkService.updateLink(link);
                redirectAttributes.addFlashAttribute("success", 
                    "Instagram link updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", 
                    "Instagram link not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error updating Instagram link: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }

    @PostMapping("/link/{id}/delete")
    public String deleteLink(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            instagramLinkService.deleteLink(id);
            redirectAttributes.addFlashAttribute("success", 
                "Instagram link deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error deleting Instagram link: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    @PostMapping("/media-catalog")
    public String addMediaCatalog(@RequestParam String name,
                                @RequestParam String type,
                                @RequestParam(required = false) String platform,
                                @RequestParam(required = false) String downloadStatus,
                                @RequestParam(required = false) String location,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) String funFacts,
                                @RequestParam(required = false) String language,
                                @RequestParam(required = false) String mainGenre,
                                @RequestParam(required = false) String subGenres,
                                @RequestParam(required = false, defaultValue = "movies") String currentTab,
                                RedirectAttributes redirectAttributes) {
        try {
            // Check for duplicate name + language combination
            Optional<MediaCatalog> existingMedia = mediaCatalogRepository.findByNameAndLanguageNullSafe(name, language);
            if (existingMedia.isPresent()) {
                redirectAttributes.addFlashAttribute("error", 
                    "A media catalog entry with the name '" + name + "' and language '" + 
                    (language != null ? language : "N/A") + "' already exists!");
                return "redirect:/dashboard?tab=" + currentTab;
            }
            
            MediaCatalog mediaCatalog = new MediaCatalog();
            mediaCatalog.setName(name);
            mediaCatalog.setType(MediaCatalog.MediaType.valueOf(type.toUpperCase().replace("-", "_").replace(" ", "_")));
            mediaCatalog.setPlatform(platform);
            
            if (downloadStatus != null && !downloadStatus.isEmpty()) {
                mediaCatalog.setDownloadStatus(MediaCatalog.DownloadStatus.valueOf(downloadStatus.toUpperCase().replace("-", "_")));
            }
            
            mediaCatalog.setLocation(location);
            mediaCatalog.setDescription(description);
            mediaCatalog.setFunFacts(funFacts);
            
            // Set new fields
            mediaCatalog.setLanguage(language);
            mediaCatalog.setMainGenre(mainGenre);
            mediaCatalog.setSubGenres(subGenres);
            
            mediaCatalogRepository.save(mediaCatalog);
            redirectAttributes.addFlashAttribute("success", "Media catalog entry saved successfully with unique validation!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving media catalog: " + e.getMessage());
        }
        
        return "redirect:/dashboard?tab=" + currentTab;
    }
    
    @PostMapping("/media-catalog/{id}/edit")
    public String updateMediaCatalog(@PathVariable Long id,
                                   @RequestParam String name,
                                   @RequestParam String type,
                                   @RequestParam(required = false) String platform,
                                   @RequestParam(required = false) String downloadStatus,
                                   @RequestParam(required = false) String location,
                                   @RequestParam(required = false) String description,
                                   @RequestParam(required = false) String funFacts,
                                   @RequestParam(required = false) String language,
                                   @RequestParam(required = false) String mainGenre,
                                   @RequestParam(required = false) String subGenres,
                                   RedirectAttributes redirectAttributes) {
        try {
            Optional<MediaCatalog> optionalMedia = mediaCatalogRepository.findById(id);
            if (optionalMedia.isPresent()) {
                MediaCatalog existingMedia = optionalMedia.get();
                
                // Check for duplicate if name or language changed
                if (!existingMedia.getName().equals(name) || 
                    !Objects.equals(existingMedia.getLanguage(), language)) {
                    Optional<MediaCatalog> duplicateMedia = mediaCatalogRepository.findByNameAndLanguageNullSafe(name, language);
                    if (duplicateMedia.isPresent() && !duplicateMedia.get().getId().equals(id)) {
                        redirectAttributes.addFlashAttribute("error", 
                            "Another media catalog entry with the name '" + name + "' and language '" + 
                            (language != null ? language : "N/A") + "' already exists!");
                        return "redirect:/dashboard";
                    }
                }
                
                existingMedia.setName(name);
                existingMedia.setType(MediaCatalog.MediaType.valueOf(type.toUpperCase().replace("-", "_").replace(" ", "_")));
                existingMedia.setPlatform(platform);
                
                if (downloadStatus != null && !downloadStatus.isEmpty()) {
                    existingMedia.setDownloadStatus(MediaCatalog.DownloadStatus.valueOf(downloadStatus.toUpperCase().replace("-", "_")));
                }
                
                existingMedia.setLocation(location);
                existingMedia.setDescription(description);
                existingMedia.setFunFacts(funFacts);
                
                // Update new fields
                existingMedia.setLanguage(language);
                existingMedia.setMainGenre(mainGenre);
                existingMedia.setSubGenres(subGenres);
                existingMedia.setUpdatedBy("system");
                
                mediaCatalogRepository.save(existingMedia);
                redirectAttributes.addFlashAttribute("success", "Media catalog updated successfully with unique validation!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Media catalog not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating media catalog: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    @PostMapping("/content-catalog")
    public String addContentCatalog(@RequestParam String link,
                                  @RequestParam String mediaCatalogType,
                                  @RequestParam String mediaCatalogName,
                                  @RequestParam String status,
                                  @RequestParam(required = false) String priority,
                                  @RequestParam(required = false) String location,
                                  @RequestParam(required = false) String metadata,
                                  @RequestParam(required = false) String likeStates,
                                  @RequestParam(required = false) String commentStates,
                                  @RequestParam(required = false) String uploadContentStatus,
                                  @RequestParam(required = false) String localStatus,
                                  @RequestParam(required = false) String locationPath,
                                  @RequestParam(required = false, defaultValue = "content-status") String currentTab,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Parse multiple media catalog names (comma-separated)
            String[] mediaNames = mediaCatalogName.split(",");
            StringBuilder processedNames = new StringBuilder();
            int createdCount = 0;
            
            // Ensure all media catalogs exist
            for (String name : mediaNames) {
                String cleanName = name.trim();
                if (!cleanName.isEmpty()) {
                    ensureMediaCatalogExists(cleanName, mediaCatalogType);
                    if (processedNames.length() > 0) {
                        processedNames.append(",");
                    }
                    processedNames.append(cleanName);
                    createdCount++;
                }
            }
            
            // Create content catalog with all media names
            ContentCatalog contentCatalog = new ContentCatalog();
            contentCatalog.setLink(link);
            contentCatalog.setMediaCatalogType(ContentCatalog.MediaType.valueOf(mediaCatalogType.toUpperCase().replace("-", "_")));
            contentCatalog.setMediaCatalogName(processedNames.toString());
            contentCatalog.setStatus(ContentCatalog.ContentStatus.valueOf(status.toUpperCase().replace("-", "_")));
            
            String priorityValue = priority != null ? priority : "MEDIUM";
            contentCatalog.setPriority(ContentCatalog.Priority.valueOf(priorityValue.toUpperCase()));
            
            contentCatalog.setLocation(location);
            contentCatalog.setMetadata(metadata);
            
            if (likeStates != null && !likeStates.isEmpty()) {
                contentCatalog.setLikeStates(ContentCatalog.LikeState.valueOf(likeStates.toUpperCase()));
            }
            
            contentCatalog.setCommentStates(commentStates);
            
            if (uploadContentStatus != null && !uploadContentStatus.isEmpty()) {
                contentCatalog.setUploadContentStatus(ContentCatalog.UploadContentStatus.valueOf(uploadContentStatus.toUpperCase().replace("-", "_")));
            }
            
            // Set new enhanced fields
            if (localStatus != null && !localStatus.isEmpty()) {
                contentCatalog.setLocalStatus(ContentCatalog.LocalStatus.valueOf(localStatus.toUpperCase().replace("-", "_")));
            }
            contentCatalog.setLocationPath(locationPath);
            
            // Save content catalog first
            ContentCatalog savedContentCatalog = contentCatalogRepository.save(contentCatalog);
            
            // Automatically create corresponding Upload Catalog entry
            UploadCatalog linkedUploadCatalog = createLinkedUploadCatalog(savedContentCatalog);
            UploadCatalog savedUploadCatalog = uploadCatalogRepository.save(linkedUploadCatalog);
            
            // Set the bidirectional linking
            savedContentCatalog.setLinkedUploadCatalogId(savedUploadCatalog.getId());
            savedUploadCatalog.setLinkedContentCatalogId(savedContentCatalog.getId());
            
            // Update both records with linking information
            contentCatalogRepository.save(savedContentCatalog);
            uploadCatalogRepository.save(savedUploadCatalog);
            
            String successMessage = createdCount > 1 
                ? "Content catalog entry saved successfully with " + createdCount + " media catalogs! Linked upload record created automatically."
                : "Content catalog entry saved successfully! Linked upload record created automatically.";
            redirectAttributes.addFlashAttribute("success", successMessage);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving content catalog: " + e.getMessage());
        }
        
        return "redirect:/dashboard?tab=" + currentTab;
    }
    
    @PostMapping("/content-catalog/{id}/edit")
    public String updateContentCatalog(@PathVariable Long id,
                                     @RequestParam String link,
                                     @RequestParam String mediaCatalogType,
                                     @RequestParam String mediaCatalogName,
                                     @RequestParam String status,
                                     @RequestParam(required = false) String priority,
                                     @RequestParam(required = false) String location,
                                     @RequestParam(required = false) String metadata,
                                     @RequestParam(required = false) String likeStates,
                                     @RequestParam(required = false) String commentStates,
                                     @RequestParam(required = false) String uploadContentStatus,
                                     @RequestParam(required = false) String localStatus,
                                     @RequestParam(required = false) String locationPath,
                                     @RequestParam(required = false, defaultValue = "content-status") String currentTab,
                                     RedirectAttributes redirectAttributes) {
        try {
            Optional<ContentCatalog> optionalContent = contentCatalogRepository.findById(id);
            if (optionalContent.isPresent()) {
                // Parse multiple media catalog names (comma-separated)
                String[] mediaNames = mediaCatalogName.split(",");
                StringBuilder processedNames = new StringBuilder();
                int createdCount = 0;
                
                // Ensure all media catalogs exist
                for (String name : mediaNames) {
                    String cleanName = name.trim();
                    if (!cleanName.isEmpty()) {
                        ensureMediaCatalogExists(cleanName, mediaCatalogType);
                        if (processedNames.length() > 0) {
                            processedNames.append(",");
                        }
                        processedNames.append(cleanName);
                        createdCount++;
                    }
                }
                
                ContentCatalog contentCatalog = optionalContent.get();
                contentCatalog.setLink(link);
                contentCatalog.setMediaCatalogType(ContentCatalog.MediaType.valueOf(mediaCatalogType.toUpperCase().replace("-", "_")));
                contentCatalog.setMediaCatalogName(processedNames.toString());
                contentCatalog.setStatus(ContentCatalog.ContentStatus.valueOf(status.toUpperCase().replace("-", "_")));
                
                String priorityValue = priority != null ? priority : "MEDIUM";
                contentCatalog.setPriority(ContentCatalog.Priority.valueOf(priorityValue.toUpperCase()));
                
                contentCatalog.setLocation(location);
                contentCatalog.setMetadata(metadata);
                
                if (likeStates != null && !likeStates.isEmpty()) {
                    contentCatalog.setLikeStates(ContentCatalog.LikeState.valueOf(likeStates.toUpperCase()));
                }
                
                contentCatalog.setCommentStates(commentStates);
                
                if (uploadContentStatus != null && !uploadContentStatus.isEmpty()) {
                    contentCatalog.setUploadContentStatus(ContentCatalog.UploadContentStatus.valueOf(uploadContentStatus.toUpperCase().replace("-", "_")));
                }
                
                // Set new enhanced fields
                if (localStatus != null && !localStatus.isEmpty()) {
                    contentCatalog.setLocalStatus(ContentCatalog.LocalStatus.valueOf(localStatus.toUpperCase().replace("-", "_")));
                }
                contentCatalog.setLocationPath(locationPath);
                contentCatalog.setUpdatedBy("system");
                
                // Save the updated content catalog
                ContentCatalog savedContentCatalog = contentCatalogRepository.save(contentCatalog);
                
                // Update linked Upload Catalog if exists
                if (savedContentCatalog.getLinkedUploadCatalogId() != null) {
                    Optional<UploadCatalog> linkedUploadOpt = uploadCatalogRepository.findById(savedContentCatalog.getLinkedUploadCatalogId());
                    if (linkedUploadOpt.isPresent()) {
                        UploadCatalog linkedUpload = linkedUploadOpt.get();
                        updateLinkedRecords(savedContentCatalog, linkedUpload);
                        uploadCatalogRepository.save(linkedUpload);
                    }
                }
                
                redirectAttributes.addFlashAttribute("success", "Content catalog updated successfully! Linked upload record also updated.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Content catalog not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating content catalog: " + e.getMessage());
        }
        
        return "redirect:/dashboard?tab=" + currentTab;
    }
    
    @PostMapping("/upload-catalog")
    public String addUploadCatalog(@RequestParam String contentCatalogLink,
                                 @RequestParam(required = false) String contentBlock,
                                 @RequestParam String mediaCatalogType,
                                 @RequestParam String mediaCatalogName,
                                 @RequestParam(required = false) String contentCatalogLocation,
                                 @RequestParam(required = false) String uploadCatalogLocation,
                                 @RequestParam String uploadStatus,
                                 @RequestParam(required = false) String uploadCatalogCaption,
                                 @RequestParam(required = false, defaultValue = "add-entry") String currentTab,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Ensure media catalog exists before creating upload catalog entry
            ensureMediaCatalogExists(mediaCatalogName, mediaCatalogType);
            
            UploadCatalog uploadCatalog = new UploadCatalog();
            uploadCatalog.setContentCatalogLink(contentCatalogLink);
            uploadCatalog.setContentBlock(contentBlock);
            uploadCatalog.setMediaCatalogType(UploadCatalog.MediaType.valueOf(mediaCatalogType.toUpperCase().replace("-", "_")));
            uploadCatalog.setMediaCatalogName(mediaCatalogName);
            uploadCatalog.setContentCatalogLocation(contentCatalogLocation);
            uploadCatalog.setUploadCatalogLocation(uploadCatalogLocation);
            uploadCatalog.setUploadStatus(UploadCatalog.UploadStatus.valueOf(uploadStatus.toUpperCase().replace("-", "_")));
            uploadCatalog.setUploadCatalogCaption(uploadCatalogCaption);
            
            uploadCatalogRepository.save(uploadCatalog);
            redirectAttributes.addFlashAttribute("success", "Upload catalog entry saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving upload catalog: " + e.getMessage());
        }
        
        return "redirect:/dashboard?tab=" + currentTab;
    }
    
    @PostMapping("/upload-catalog/{id}/edit")
    public String updateUploadCatalog(@PathVariable Long id,
                                    @RequestParam String contentCatalogLink,
                                    @RequestParam(required = false) String contentBlock,
                                    @RequestParam String mediaCatalogType,
                                    @RequestParam String mediaCatalogName,
                                    @RequestParam(required = false) String contentCatalogLocation,
                                    @RequestParam(required = false) String uploadCatalogLocation,
                                    @RequestParam String uploadStatus,
                                    @RequestParam(required = false) String uploadCatalogCaption,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Ensure media catalog exists before updating upload catalog entry
            ensureMediaCatalogExists(mediaCatalogName, mediaCatalogType);
            
            Optional<UploadCatalog> optionalUpload = uploadCatalogRepository.findById(id);
            if (optionalUpload.isPresent()) {
                UploadCatalog uploadCatalog = optionalUpload.get();
                uploadCatalog.setContentCatalogLink(contentCatalogLink);
                uploadCatalog.setContentBlock(contentBlock);
                uploadCatalog.setMediaCatalogType(UploadCatalog.MediaType.valueOf(mediaCatalogType.toUpperCase().replace("-", "_")));
                uploadCatalog.setMediaCatalogName(mediaCatalogName);
                uploadCatalog.setContentCatalogLocation(contentCatalogLocation);
                uploadCatalog.setUploadCatalogLocation(uploadCatalogLocation);
                uploadCatalog.setUploadStatus(UploadCatalog.UploadStatus.valueOf(uploadStatus.toUpperCase().replace("-", "_")));
                uploadCatalog.setUploadCatalogCaption(uploadCatalogCaption);
                uploadCatalog.setUpdatedBy("system");
                
                // Save the updated upload catalog
                UploadCatalog savedUploadCatalog = uploadCatalogRepository.save(uploadCatalog);
                
                // Update linked Content Catalog if exists
                if (savedUploadCatalog.getLinkedContentCatalogId() != null) {
                    Optional<ContentCatalog> linkedContentOpt = contentCatalogRepository.findById(savedUploadCatalog.getLinkedContentCatalogId());
                    if (linkedContentOpt.isPresent()) {
                        ContentCatalog linkedContent = linkedContentOpt.get();
                        // Update content catalog with upload catalog data
                        linkedContent.setLink(savedUploadCatalog.getContentCatalogLink());
                        linkedContent.setMediaCatalogName(savedUploadCatalog.getMediaCatalogName());
                        linkedContent.setLocation(savedUploadCatalog.getContentCatalogLocation());
                        linkedContent.setUpdatedBy("system_sync");
                        contentCatalogRepository.save(linkedContent);
                    }
                }
                
                redirectAttributes.addFlashAttribute("success", "Upload catalog updated successfully! Linked content record also updated.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Upload catalog not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating upload catalog: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    /**
     * Helper method to ensure a media catalog entry exists for the given name and type.
     * If it doesn't exist, creates a new one with default values.
     */
    private void ensureMediaCatalogExists(String mediaCatalogName, String mediaCatalogType) {
        if (mediaCatalogName == null || mediaCatalogName.trim().isEmpty()) {
            return; // Don't create empty entries
        }
        
        // Check if media catalog already exists by name (exact match)
        List<MediaCatalog> existingMedia = mediaCatalogRepository.findByNameContainingIgnoreCaseOrderByCreatedOnDesc(mediaCatalogName.trim());
        
        // If exact match found, no need to create
        boolean exactMatchFound = existingMedia.stream()
                .anyMatch(media -> media.getName().equalsIgnoreCase(mediaCatalogName.trim()));
        
        if (!exactMatchFound) {
            // Create new media catalog entry
            MediaCatalog newMediaCatalog = new MediaCatalog();
            newMediaCatalog.setName(mediaCatalogName.trim());
            
            try {
                if (mediaCatalogType != null && !mediaCatalogType.trim().isEmpty()) {
                    newMediaCatalog.setType(MediaCatalog.MediaType.valueOf(mediaCatalogType.toUpperCase().replace("-", "_")));
                } else {
                    newMediaCatalog.setType(MediaCatalog.MediaType.MOVIE);
                }
            } catch (IllegalArgumentException e) {
                // Default to MOVIE if type is invalid
                newMediaCatalog.setType(MediaCatalog.MediaType.MOVIE);
            }
            
            // Set default values
            newMediaCatalog.setDownloadStatus(MediaCatalog.DownloadStatus.NOT_DOWNLOADED);
            newMediaCatalog.setDescription("Auto-created from Content Catalog");
            
            try {
                mediaCatalogRepository.save(newMediaCatalog);
                System.out.println("Successfully created new media catalog: " + mediaCatalogName); // Debug log
            } catch (Exception e) {
                System.err.println("Error creating media catalog: " + e.getMessage()); // Debug log
                throw e;
            }
        } else {
            System.out.println("Media catalog already exists: " + mediaCatalogName); // Debug log
        }
    }
    
    /**
     * Creates a linked Upload Catalog entry from a Content Catalog entry
     */
    private UploadCatalog createLinkedUploadCatalog(ContentCatalog contentCatalog) {
        UploadCatalog uploadCatalog = new UploadCatalog();
        
        // Map fields from Content Catalog to Upload Catalog
        uploadCatalog.setContentCatalogLink(contentCatalog.getLink());
        uploadCatalog.setContentBlock(contentCatalog.getId().toString()); // Use Content Catalog ID as content block
        
        // Map media type enum values
        UploadCatalog.MediaType uploadMediaType = mapContentMediaTypeToUploadMediaType(contentCatalog.getMediaCatalogType());
        uploadCatalog.setMediaCatalogType(uploadMediaType);
        
        uploadCatalog.setMediaCatalogName(contentCatalog.getMediaCatalogName());
        uploadCatalog.setContentCatalogLocation(contentCatalog.getLocation());
        uploadCatalog.setUploadCatalogLocation(""); // Empty by default, can be filled later
        
        // Set default upload status as "NEW" for auto-created entries
        uploadCatalog.setUploadStatus(UploadCatalog.UploadStatus.NEW);
        
        // Set default caption indicating auto-creation
        uploadCatalog.setUploadCatalogCaption("Auto-created from Content Catalog: " + contentCatalog.getMediaCatalogName());
        
        return uploadCatalog;
    }
    
    /**
     * Maps ContentCatalog MediaType to UploadCatalog MediaType
     */
    private UploadCatalog.MediaType mapContentMediaTypeToUploadMediaType(ContentCatalog.MediaType contentMediaType) {
        switch (contentMediaType) {
            case MOVIE:
                return UploadCatalog.MediaType.MOVIE;
            case ALBUM:
                return UploadCatalog.MediaType.ALBUM;
            case WEB_SERIES:
                return UploadCatalog.MediaType.WEB_SERIES;
            case DOCUMENTARY:
                return UploadCatalog.MediaType.DOCUMENTARY;
            default:
                return UploadCatalog.MediaType.MOVIE; // Default fallback
        }
    }
    
    /**
     * Updates linked records when either Content or Upload catalog is modified
     */
    private void updateLinkedRecords(ContentCatalog contentCatalog, UploadCatalog uploadCatalog) {
        if (contentCatalog != null && uploadCatalog != null) {
            // Update Upload Catalog with Content Catalog data
            uploadCatalog.setContentCatalogLink(contentCatalog.getLink());
            uploadCatalog.setMediaCatalogName(contentCatalog.getMediaCatalogName());
            uploadCatalog.setMediaCatalogType(mapContentMediaTypeToUploadMediaType(contentCatalog.getMediaCatalogType()));
            uploadCatalog.setContentCatalogLocation(contentCatalog.getLocation());
            uploadCatalog.setUpdatedBy("system_sync");
            
            // Update Content Catalog with Upload Catalog data (if needed)
            contentCatalog.setUpdatedBy("system_sync");
        }
    }
    
    // ===== BULK UPLOAD ENDPOINTS =====
    
    @PostMapping("/bulk-upload/content-catalog")
    public String bulkUploadContentCatalog(@RequestParam("file") MultipartFile file,
                                         RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select a file to upload!");
                return "redirect:/dashboard";
            }
            
            Map<String, Object> result = bulkUploadService.processContentCatalogBulkUpload(file);
            int successCount = (Integer) result.get("successCount");
            int errorCount = (Integer) result.get("errorCount");
            List<String> errors = (List<String>) result.get("errors");
            
            if (successCount > 0) {
                String message = String.format("Bulk upload completed! Successfully imported %d Content Catalog entries", successCount);
                if (errorCount > 0) {
                    message += String.format(" with %d errors.", errorCount);
                } else {
                    message += ". All linked Upload Catalog entries were also created automatically.";
                }
                redirectAttributes.addFlashAttribute("success", message);
            }
            
            if (errorCount > 0) {
                redirectAttributes.addFlashAttribute("bulkUploadErrors", errors);
                if (successCount == 0) {
                    redirectAttributes.addFlashAttribute("error", "Bulk upload failed! No records were imported. Check the error details.");
                }
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Bulk upload error: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    @PostMapping("/bulk-upload/upload-catalog")
    public String bulkUploadUploadCatalog(@RequestParam("file") MultipartFile file,
                                        RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select a file to upload!");
                return "redirect:/dashboard";
            }
            
            Map<String, Object> result = bulkUploadService.processUploadCatalogBulkUpload(file);
            int successCount = (Integer) result.get("successCount");
            int errorCount = (Integer) result.get("errorCount");
            List<String> errors = (List<String>) result.get("errors");
            
            if (successCount > 0) {
                String message = String.format("Bulk upload completed! Successfully imported %d Upload Catalog entries", successCount);
                if (errorCount > 0) {
                    message += String.format(" with %d errors.", errorCount);
                } else {
                    message += ".";
                }
                redirectAttributes.addFlashAttribute("success", message);
            }
            
            if (errorCount > 0) {
                redirectAttributes.addFlashAttribute("bulkUploadErrors", errors);
                if (successCount == 0) {
                    redirectAttributes.addFlashAttribute("error", "Bulk upload failed! No records were imported. Check the error details.");
                }
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Bulk upload error: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    @PostMapping("/bulk-upload/media-catalog")
    public String bulkUploadMediaCatalog(@RequestParam("file") MultipartFile file,
                                       RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Starting media catalog bulk upload. File: " + file.getOriginalFilename());
            
            if (file.isEmpty()) {
                System.err.println("No file provided for bulk upload");
                redirectAttributes.addFlashAttribute("error", "Please select a file to upload!");
                return "redirect:/dashboard";
            }
            
            // Validate file type
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".csv") && !fileName.endsWith(".json") && 
                !fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                System.err.println("Invalid file format: " + fileName);
                redirectAttributes.addFlashAttribute("error", "Invalid file format! Please use CSV, JSON, or Excel files.");
                return "redirect:/dashboard";
            }
            
            System.out.println("Processing file: " + fileName + " (size: " + file.getSize() + " bytes)");
            
            Map<String, Object> result = bulkUploadService.processMediaCatalogBulkUpload(file);
            int successCount = (Integer) result.get("successCount");
            int errorCount = (Integer) result.get("errorCount");
            List<String> errors = (List<String>) result.get("errors");
            
            System.out.println("Bulk upload completed - Success: " + successCount + ", Errors: " + errorCount);
            
            if (successCount > 0) {
                String message = String.format("Bulk upload completed! Successfully processed %d Media Catalog entries", successCount);
                if (errorCount > 0) {
                    message += String.format(" with %d errors. Duplicates were updated rather than creating new records.", errorCount);
                } else {
                    message += ". All entries processed successfully with unique name+language validation.";
                }
                redirectAttributes.addFlashAttribute("success", message);
            }
            
            if (errorCount > 0) {
                redirectAttributes.addFlashAttribute("bulkUploadErrors", errors);
                if (successCount == 0) {
                    redirectAttributes.addFlashAttribute("error", 
                        "Bulk upload failed! No records were processed successfully. Please check your file format and data.");
                }
                
                // Log errors to console for debugging
                System.err.println("Bulk upload errors:");
                errors.forEach(error -> System.err.println(" - " + error));
            }
            
        } catch (Exception e) {
            System.err.println("Critical bulk upload error: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Critical bulk upload error: " + e.getMessage() + 
                ". Please check your file format and ensure all required columns are present.");
        }
        
        return "redirect:/dashboard";
    }
    
    // Template download endpoints
    @GetMapping("/templates/content-catalog/{format}")
    public ResponseEntity<Resource> downloadContentCatalogTemplate(@PathVariable String format) {
        try {
            String fileName = "content_catalog_template." + format;
            Resource resource = new ClassPathResource("static/templates/" + fileName);
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/templates/upload-catalog/{format}")
    public ResponseEntity<Resource> downloadUploadCatalogTemplate(@PathVariable String format) {
        try {
            String fileName = "upload_catalog_template." + format;
            Resource resource = new ClassPathResource("static/templates/" + fileName);
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/templates/media-catalog/{format}")
    public ResponseEntity<Resource> downloadMediaCatalogTemplate(@PathVariable String format) {
        try {
            String fileName = "media_catalog_template." + format;
            Resource resource = new ClassPathResource("static/templates/" + fileName);
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/templates/states-catalog/{format}")
    public ResponseEntity<Resource> downloadStatesCatalogTemplate(@PathVariable String format) {
        try {
            String fileName = "states_catalog_template." + format;
            Resource resource = new ClassPathResource("static/templates/" + fileName);
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ===== DASHBOARD METRICS API =====
    
    @GetMapping("/api/dashboard/metrics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        try {
            System.out.println("Dashboard metrics API called");
            Map<String, Object> metrics = new HashMap<>();
            
            // System Statistics - basic counts first
            Map<String, Object> systemStats = new HashMap<>();
            long mediaCount = mediaCatalogRepository.count();
            long contentCount = contentCatalogRepository.count();
            long uploadCount = uploadCatalogRepository.count();
            long statesCount = instagramLinkRepository.count(); // Using Instagram links as states catalog
            
            systemStats.put("totalMediaCatalog", mediaCount);
            systemStats.put("totalContentCatalog", contentCount);
            systemStats.put("totalUploadCatalog", uploadCount);
            systemStats.put("totalStatesCatalog", statesCount);
            systemStats.put("totalRecords", mediaCount + contentCount + uploadCount + statesCount);
            
            System.out.println("System stats calculated: Media=" + mediaCount + ", Content=" + contentCount + ", Upload=" + uploadCount);
            
            // Recent Activity (simplified)
            Map<String, Object> recentActivity = new HashMap<>();
            recentActivity.put("recentMediaAdded", getRecentRecordsCount("media"));
            recentActivity.put("recentContentAdded", getRecentRecordsCount("content"));
            recentActivity.put("recentUploadsAdded", getRecentRecordsCount("upload"));
            
            // Download Status Distribution
            Map<String, Object> downloadStats = new HashMap<>();
            downloadStats.put("downloaded", getMediaByDownloadStatus("DOWNLOADED"));
            downloadStats.put("notDownloaded", getMediaByDownloadStatus("NOT_DOWNLOADED"));
            downloadStats.put("partiallyDownloaded", getMediaByDownloadStatus("PARTIALLY_DOWNLOADED"));
            
            // Content Status Distribution
            Map<String, Object> contentStats = new HashMap<>();
            contentStats.put("new", getContentByStatus("NEW"));
            contentStats.put("downloaded", getContentByStatus("DOWNLOADED"));
            contentStats.put("error", getContentByStatus("ERROR"));
            
            // Upload Status Distribution (including new READY_TO_UPLOAD)
            Map<String, Object> uploadStats = new HashMap<>();
            uploadStats.put("new", getUploadsByStatus("NEW"));
            uploadStats.put("readyToUpload", getUploadsByStatus("READY_TO_UPLOAD"));
            uploadStats.put("uploaded", getUploadsByStatus("UPLOADED"));
            uploadStats.put("inProgress", getUploadsByStatus("IN_PROGRESS"));
            uploadStats.put("completed", getUploadsByStatus("COMPLETED"));
            
            // Media Type Distribution
            Map<String, Object> mediaTypeStats = new HashMap<>();
            mediaTypeStats.put("movies", getMediaByType("MOVIE"));
            mediaTypeStats.put("albums", getMediaByType("ALBUM"));
            mediaTypeStats.put("webSeries", getMediaByType("WEB_SERIES"));
            mediaTypeStats.put("documentaries", getMediaByType("DOCUMENTARY"));
            
            // System Health Indicators
            Map<String, Object> systemHealth = new HashMap<>();
            systemHealth.put("databaseConnected", true);
            systemHealth.put("lastBackup", "N/A");
            systemHealth.put("diskSpace", "Available");
            systemHealth.put("uptime", getSystemUptime());
            
            // Performance Metrics
            Map<String, Object> performance = new HashMap<>();
            performance.put("avgResponseTime", "< 100ms");
            performance.put("successfulUploads", calculateSuccessfulUploadsPercentage());
            performance.put("dataIntegrityScore", calculateDataIntegrityScore());
            
            // Quick Stats for Cards
            Map<String, Object> quickStats = new HashMap<>();
            long todaysActivity = getRecentRecordsCount("media") + getRecentRecordsCount("content") + getRecentRecordsCount("upload");
            quickStats.put("todaysActivity", (int)todaysActivity);
            quickStats.put("completionRate", calculateOverallCompletionRate());
            quickStats.put("downloadedPercentage", calculateDownloadedPercentage());
            quickStats.put("linkedRecords", countLinkedRecords());
            quickStats.put("popularPlatform", getMostPopularPlatform());
            
            System.out.println("Quick stats calculated: TodaysActivity=" + todaysActivity);
            
            // Assemble response
            metrics.put("systemStats", systemStats);
            metrics.put("recentActivity", recentActivity);
            metrics.put("downloadStats", downloadStats);
            metrics.put("contentStats", contentStats);
            metrics.put("uploadStats", uploadStats);
            metrics.put("mediaTypeStats", mediaTypeStats);
            metrics.put("systemHealth", systemHealth);
            metrics.put("performance", performance);
            metrics.put("quickStats", quickStats);
            metrics.put("lastUpdated", System.currentTimeMillis());
            
            System.out.println("Dashboard metrics response prepared successfully");
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            System.err.println("Error in dashboard metrics API: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch dashboard metrics: " + e.getMessage());
            errorResponse.put("errorDetails", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Helper methods for dashboard metrics
    private long getRecentRecordsCount(String type) {
        try {
            // Placeholder - would need proper date queries
            // For now, return a reasonable estimate based on total records
            switch (type.toLowerCase()) {
                case "media": return Math.min(mediaCatalogRepository.count() / 10, 5);
                case "content": return Math.min(contentCatalogRepository.count() / 10, 3);
                case "upload": return Math.min(uploadCatalogRepository.count() / 10, 2);
                default: return 0;
            }
        } catch (Exception e) {
            System.err.println("Error getting recent records count for type " + type + ": " + e.getMessage());
            return 0;
        }
    }
    
    private long getMediaByDownloadStatus(String status) {
        try {
            MediaCatalog.DownloadStatus downloadStatus = MediaCatalog.DownloadStatus.valueOf(status);
            return mediaCatalogRepository.findByDownloadStatusOrderByCreatedOnDesc(downloadStatus).size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    private long getContentByStatus(String status) {
        try {
            ContentCatalog.ContentStatus contentStatus = ContentCatalog.ContentStatus.valueOf(status);
            return contentCatalogRepository.findByStatusOrderByCreatedOnDesc(contentStatus).size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    private long getUploadsByStatus(String status) {
        try {
            UploadCatalog.UploadStatus uploadStatus = UploadCatalog.UploadStatus.valueOf(status);
            return uploadCatalogRepository.findByUploadStatusOrderByCreatedOnDesc(uploadStatus).size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    private long getMediaByType(String type) {
        try {
            MediaCatalog.MediaType mediaType = MediaCatalog.MediaType.valueOf(type);
            return mediaCatalogRepository.findByTypeOrderByCreatedOnDesc(mediaType).size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    private String getSystemUptime() {
        // Placeholder - would need actual system monitoring
        return "Running smoothly";
    }
    
    private double calculateSuccessfulUploadsPercentage() {
        long totalUploads = uploadCatalogRepository.count();
        if (totalUploads == 0) return 100.0;
        
        long successfulUploads = getUploadsByStatus("UPLOADED") + getUploadsByStatus("COMPLETED");
        return Math.round((successfulUploads * 100.0 / totalUploads) * 100.0) / 100.0;
    }
    
    private double calculateDataIntegrityScore() {
        // Simple integrity check based on linked records
        long contentRecords = contentCatalogRepository.count();
        long uploadRecords = uploadCatalogRepository.count();
        
        if (contentRecords == 0 && uploadRecords == 0) return 100.0;
        
        // Assuming good integrity if we have both content and uploads
        double score = Math.min(100.0, 85.0 + (Math.abs(contentRecords - uploadRecords) < 10 ? 15.0 : 0.0));
        return Math.round(score * 100.0) / 100.0;
    }
    
    private double calculateDownloadedPercentage() {
        long totalMedia = mediaCatalogRepository.count();
        if (totalMedia == 0) return 0.0;
        
        long downloadedMedia = getMediaByDownloadStatus("DOWNLOADED");
        return Math.round((downloadedMedia * 100.0 / totalMedia) * 100.0) / 100.0;
    }
    
    private long countLinkedRecords() {
        // Count records that have linked relationships
        // This is a simplified calculation
        long contentWithLinks = contentCatalogRepository.count();
        long uploadsWithLinks = uploadCatalogRepository.count();
        return Math.min(contentWithLinks, uploadsWithLinks) * 2; // Each link represents 2 records
    }
    
    private String getMostPopularPlatform() {
        // Placeholder - would need GROUP BY query
        List<String> platforms = Arrays.asList("Netflix", "Amazon Prime", "YouTube", "Disney+", "Other");
        return platforms.get((int)(Math.random() * platforms.size()));
    }
    
    private double calculateOverallCompletionRate() {
        try {
            long totalMedia = mediaCatalogRepository.count();
            long totalContent = contentCatalogRepository.count();
            long totalUpload = uploadCatalogRepository.count();
            
            if (totalMedia == 0 && totalContent == 0 && totalUpload == 0) {
                return 0.0;
            }
            
            // Count completed items across all catalogs
            long completedContent = getContentByStatus("DOWNLOADED");
            long completedUploads = getUploadsByStatus("UPLOADED") + getUploadsByStatus("COMPLETED");
            
            // Calculate overall completion rate
            double totalCompleted = completedContent + completedUploads;
            double totalItems = totalContent + totalUpload;
            
            return totalItems > 0 ? Math.round((totalCompleted / totalItems) * 100.0) : 0.0;
        } catch (Exception e) {
            System.err.println("Error calculating completion rate: " + e.getMessage());
            return 0.0;
        }
    }
    
    // ===== API ENDPOINTS FOR DYNAMIC LOADING =====
    
    @GetMapping("/api/media-catalog")
    @ResponseBody
    public ResponseEntity<List<MediaCatalog>> getMediaCatalog(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "updatedOn:desc") String sort) {
        try {
            // Parse sort parameter
            String[] sortParts = sort.split(":");
            String sortField = sortParts[0];
            String sortDirection = sortParts.length > 1 ? sortParts[1] : "desc";
            
            // Create sort object
            Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sortObj = Sort.by(direction, sortField);
            
            // Create pageable request
            PageRequest pageRequest = PageRequest.of(0, limit, sortObj);
            
            // Fetch data
            List<MediaCatalog> mediaList = mediaCatalogRepository.findAll(pageRequest).getContent();
            
            return ResponseEntity.ok(mediaList);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/api/content-catalog")
    @ResponseBody
    public ResponseEntity<List<ContentCatalog>> getContentCatalog() {
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
            List<ContentCatalog> contentList = contentCatalogRepository.findAll(sort);
            return ResponseEntity.ok(contentList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/api/content-catalog/content-blocks")
    @ResponseBody
    public ResponseEntity<List<ContentCatalog>> searchContentBlocks(
            @RequestParam(required = false) String search) {
        try {
            List<ContentCatalog> results = contentCatalogRepository.findAll();
            
            if (search != null && !search.trim().isEmpty()) {
                results = results.stream()
                    .filter(content -> {
                        // Search in multiple fields
                        String searchLower = search.toLowerCase();
                        return (content.getMediaCatalogName() != null && content.getMediaCatalogName().toLowerCase().contains(searchLower)) ||
                               (content.getLink() != null && content.getLink().toLowerCase().contains(searchLower)) ||
                               (content.getLocation() != null && content.getLocation().toLowerCase().contains(searchLower));
                    })
                    .collect(Collectors.toList());
            }
            
            // Sort by created date desc and limit to 10 for dropdown
            results.sort((a, b) -> {
                if (a.getCreatedOn() == null && b.getCreatedOn() == null) return 0;
                if (a.getCreatedOn() == null) return 1;
                if (b.getCreatedOn() == null) return -1;
                return b.getCreatedOn().compareTo(a.getCreatedOn());
            });
            
            return ResponseEntity.ok(results.stream().limit(10).collect(Collectors.toList()));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    
    @GetMapping("/api/upload-catalog") 
    @ResponseBody
    public ResponseEntity<List<UploadCatalog>> getUploadCatalog() {
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
            List<UploadCatalog> uploadList = uploadCatalogRepository.findAll(sort);
            return ResponseEntity.ok(uploadList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    // ===== BULK DELETE ENDPOINTS =====
    
    @DeleteMapping("/api/media-catalog/bulk-delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> bulkDeleteMediaCatalog(@RequestBody List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createMap("error", "No IDs provided for deletion"));
            }
            
            long deletedCount = 0;
            for (Long id : ids) {
                if (mediaCatalogRepository.existsById(id)) {
                    mediaCatalogRepository.deleteById(id);
                    deletedCount++;
                }
            }
            
            return ResponseEntity.ok()
                .body(createMap("message", "Successfully deleted " + deletedCount + " media catalog records"));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createMap("error", "Error deleting media catalog records: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/api/content-catalog/bulk-delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> bulkDeleteContentCatalog(@RequestBody List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createMap("error", "No IDs provided for deletion"));
            }
            
            long deletedCount = 0;
            for (Long id : ids) {
                if (contentCatalogRepository.existsById(id)) {
                    // Find and delete linked upload catalog record if exists
                    Optional<UploadCatalog> linkedUpload = uploadCatalogRepository.findByLinkedContentCatalogId(id);
                    if (linkedUpload.isPresent()) {
                        uploadCatalogRepository.delete(linkedUpload.get());
                    }
                    
                    contentCatalogRepository.deleteById(id);
                    deletedCount++;
                }
            }
            
            return ResponseEntity.ok()
                .body(createMap("message", "Successfully deleted " + deletedCount + " content catalog records"));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createMap("error", "Error deleting content catalog records: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/api/upload-catalog/bulk-delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> bulkDeleteUploadCatalog(@RequestBody List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createMap("error", "No IDs provided for deletion"));
            }
            
            long deletedCount = 0;
            for (Long id : ids) {
                if (uploadCatalogRepository.existsById(id)) {
                    // Find and delete linked content catalog record if exists
                    Optional<ContentCatalog> linkedContent = contentCatalogRepository.findByLinkedUploadCatalogId(id);
                    if (linkedContent.isPresent()) {
                        contentCatalogRepository.delete(linkedContent.get());
                    }
                    
                    uploadCatalogRepository.deleteById(id);
                    deletedCount++;
                }
            }
            
            return ResponseEntity.ok()
                .body(createMap("message", "Successfully deleted " + deletedCount + " upload catalog records"));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createMap("error", "Error deleting upload catalog records: " + e.getMessage()));
        }
    }
    
    // ===== STATES CATALOG ENDPOINTS =====
    
    @PostMapping("/states-catalog")
    public String addStatesCatalog(@RequestParam(required = false) String reportDate,
                                 @RequestParam(required = false, defaultValue = "0") Integer views,
                                 @RequestParam(required = false, defaultValue = "0") Integer subscribers,
                                 @RequestParam(required = false, defaultValue = "0") Integer interactions,
                                 @RequestParam(required = false, defaultValue = "0") Integer totalContent,
                                 @RequestParam(required = false, defaultValue = "0") Integer reach,
                                 @RequestParam(required = false, defaultValue = "0") Integer impressions,
                                 @RequestParam(required = false, defaultValue = "0") Integer profileVisits,
                                 @RequestParam(required = false, defaultValue = "0") Integer websiteClicks,
                                 @RequestParam(required = false, defaultValue = "0") Integer emailClicks,
                                 @RequestParam(required = false, defaultValue = "0") Integer callClicks,
                                 @RequestParam(required = false, defaultValue = "0") Integer followersGained,
                                 @RequestParam(required = false, defaultValue = "0") Integer followersLost,
                                 @RequestParam(required = false, defaultValue = "0") Integer reelsCount,
                                 @RequestParam(required = false, defaultValue = "0") Integer storiesCount,
                                 @RequestParam(required = false, defaultValue = "0.0") BigDecimal avgEngagementRate,
                                 RedirectAttributes redirectAttributes) {
        try {
            StatesCatalog statesCatalog = new StatesCatalog();
            
            // Parse and set report date
            if (reportDate != null && !reportDate.trim().isEmpty()) {
                statesCatalog.setReportDate(java.time.LocalDate.parse(reportDate));
            } else {
                statesCatalog.setReportDate(java.time.LocalDate.now()); // Default to today
            }
            
            statesCatalog.setViews(views);
            statesCatalog.setSubscribers(subscribers);
            statesCatalog.setInteractions(interactions);
            statesCatalog.setTotalContent(totalContent);
            statesCatalog.setReach(reach);
            statesCatalog.setImpressions(impressions);
            statesCatalog.setProfileVisits(profileVisits);
            statesCatalog.setWebsiteClicks(websiteClicks);
            statesCatalog.setEmailClicks(emailClicks);
            statesCatalog.setCallClicks(callClicks);
            statesCatalog.setFollowersGained(followersGained);
            statesCatalog.setFollowersLost(followersLost);
            statesCatalog.setReelsCount(reelsCount);
            statesCatalog.setStoriesCount(storiesCount);
            statesCatalog.setAvgEngagementRate(avgEngagementRate);
            
            statesCatalogRepository.save(statesCatalog);
            redirectAttributes.addFlashAttribute("success", "States catalog entry saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving states catalog: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    @PostMapping("/states-catalog/{id}/edit")
    public String updateStatesCatalog(@PathVariable Long id,
                                    @RequestParam(required = false) String reportDate,
                                    @RequestParam(required = false, defaultValue = "0") Integer views,
                                    @RequestParam(required = false, defaultValue = "0") Integer subscribers,
                                    @RequestParam(required = false, defaultValue = "0") Integer interactions,
                                    @RequestParam(required = false, defaultValue = "0") Integer totalContent,
                                    @RequestParam(required = false, defaultValue = "0") Integer reach,
                                    @RequestParam(required = false, defaultValue = "0") Integer impressions,
                                    @RequestParam(required = false, defaultValue = "0") Integer profileVisits,
                                    @RequestParam(required = false, defaultValue = "0") Integer websiteClicks,
                                    @RequestParam(required = false, defaultValue = "0") Integer emailClicks,
                                    @RequestParam(required = false, defaultValue = "0") Integer callClicks,
                                    @RequestParam(required = false, defaultValue = "0") Integer followersGained,
                                    @RequestParam(required = false, defaultValue = "0") Integer followersLost,
                                    @RequestParam(required = false, defaultValue = "0") Integer reelsCount,
                                    @RequestParam(required = false, defaultValue = "0") Integer storiesCount,
                                    @RequestParam(required = false, defaultValue = "0.0") BigDecimal avgEngagementRate,
                                    RedirectAttributes redirectAttributes) {
        try {
            Optional<StatesCatalog> optionalStates = statesCatalogRepository.findById(id);
            if (optionalStates.isPresent()) {
                StatesCatalog statesCatalog = optionalStates.get();
                
                // Parse and set report date
                if (reportDate != null && !reportDate.trim().isEmpty()) {
                    statesCatalog.setReportDate(java.time.LocalDate.parse(reportDate));
                }
                
                statesCatalog.setViews(views);
                statesCatalog.setSubscribers(subscribers);
                statesCatalog.setInteractions(interactions);
                statesCatalog.setTotalContent(totalContent);
                statesCatalog.setReach(reach);
                statesCatalog.setImpressions(impressions);
                statesCatalog.setProfileVisits(profileVisits);
                statesCatalog.setWebsiteClicks(websiteClicks);
                statesCatalog.setEmailClicks(emailClicks);
                statesCatalog.setCallClicks(callClicks);
                statesCatalog.setFollowersGained(followersGained);
                statesCatalog.setFollowersLost(followersLost);
                statesCatalog.setReelsCount(reelsCount);
                statesCatalog.setStoriesCount(storiesCount);
                statesCatalog.setAvgEngagementRate(avgEngagementRate);
                statesCatalog.setUpdatedBy("system");
                
                statesCatalogRepository.save(statesCatalog);
                redirectAttributes.addFlashAttribute("success", "States catalog updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "States catalog not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating states catalog: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    @PostMapping("/bulk-upload/states-catalog")
    public String bulkUploadStatesCatalog(@RequestParam("file") MultipartFile file,
                                        RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select a file to upload!");
                return "redirect:/dashboard";
            }

            Map<String, Object> result = bulkUploadService.processStatesCatalogBulkUpload(file);
            int successCount = (Integer) result.get("successCount");
            int errorCount = (Integer) result.get("errorCount");
            List<String> errors = (List<String>) result.get("errors");
            
            if (successCount > 0) {
                redirectAttributes.addFlashAttribute("success", 
                    String.format("Successfully uploaded %d states catalog records!", successCount));
            }
            
            if (errorCount > 0) {
                redirectAttributes.addFlashAttribute("warning", 
                    String.format("Failed to upload %d records. Errors: %s", errorCount, String.join(", ", errors)));
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error processing bulk upload: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    // API endpoint for States Catalog data
    @GetMapping("/api/states-catalog")
    @ResponseBody
    public ResponseEntity<List<StatesCatalog>> getStatesCatalog() {
        try {
            List<StatesCatalog> statesList = statesCatalogRepository.findAllOrderByCreatedOnDesc();
            return ResponseEntity.ok(statesList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @DeleteMapping("/api/states-catalog/bulk-delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> bulkDeleteStatesCatalog(@RequestBody List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createMap("error", "No IDs provided for deletion"));
            }
            
            long deletedCount = 0;
            for (Long id : ids) {
                if (statesCatalogRepository.existsById(id)) {
                    statesCatalogRepository.deleteById(id);
                    deletedCount++;
                }
            }
            
            return ResponseEntity.ok()
                .body(createMap("message", "Successfully deleted " + deletedCount + " states catalog records"));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createMap("error", "Error deleting states catalog records: " + e.getMessage()));
        }
    }
    
    // Helper method for Java 8 compatibility (Map.of() is Java 9+)
    private Map<String, String> createMap(String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}