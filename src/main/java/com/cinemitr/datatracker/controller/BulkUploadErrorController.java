package com.cinemitr.datatracker.controller;

import com.cinemitr.datatracker.dto.BulkUploadErrorDTO;
import com.cinemitr.datatracker.service.BulkUploadErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bulk-errors")
@CrossOrigin(origins = "*")
public class BulkUploadErrorController {
    
    @Autowired
    private BulkUploadErrorService errorService;
    
    // Log a new bulk upload error
    @PostMapping
    public ResponseEntity<BulkUploadErrorDTO> logError(@RequestBody BulkUploadErrorDTO errorDTO) {
        try {
            BulkUploadErrorDTO loggedError = errorService.logError(
                errorDTO.getUploadType(),
                errorDTO.getBatchId(),
                errorDTO.getRowNumber(),
                errorDTO.getRawData(),
                errorDTO.getErrorType(),
                errorDTO.getErrorMessage(),
                errorDTO.getFieldName(),
                errorDTO.getAttemptedValue(),
                errorDTO.getSuggestions()
            );
            return ResponseEntity.ok(loggedError);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    // Get all bulk upload errors
    @GetMapping
    public ResponseEntity<List<BulkUploadErrorDTO>> getAllErrors() {
        try {
            List<BulkUploadErrorDTO> errors = errorService.getAllErrors();
            return ResponseEntity.ok(errors);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    // Get single error by ID
    @GetMapping("/{errorId}")
    public ResponseEntity<BulkUploadErrorDTO> getErrorById(@PathVariable Long errorId) {
        try {
            BulkUploadErrorDTO error = errorService.getAllErrors().stream()
                .filter(e -> e.getId().equals(errorId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Error not found"));
            return ResponseEntity.ok(error);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
    
    // Get errors by upload type
    @GetMapping("/type/{uploadType}")
    public ResponseEntity<List<BulkUploadErrorDTO>> getErrorsByUploadType(@PathVariable String uploadType) {
        try {
            List<BulkUploadErrorDTO> errors = errorService.getErrorsByUploadType(uploadType);
            return ResponseEntity.ok(errors);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    // Get errors by batch ID
    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<BulkUploadErrorDTO>> getErrorsByBatchId(@PathVariable String batchId) {
        try {
            List<BulkUploadErrorDTO> errors = errorService.getErrorsByBatchId(batchId);
            return ResponseEntity.ok(errors);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    // Get unresolved errors
    @GetMapping("/unresolved")
    public ResponseEntity<List<BulkUploadErrorDTO>> getUnresolvedErrors() {
        try {
            List<BulkUploadErrorDTO> errors = errorService.getUnresolvedErrors();
            return ResponseEntity.ok(errors);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    // Get unresolved errors by upload type
    @GetMapping("/unresolved/{uploadType}")
    public ResponseEntity<List<BulkUploadErrorDTO>> getUnresolvedErrorsByUploadType(@PathVariable String uploadType) {
        try {
            List<BulkUploadErrorDTO> errors = errorService.getUnresolvedErrorsByUploadType(uploadType);
            return ResponseEntity.ok(errors);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    // Get recent errors (last 7 days)
    @GetMapping("/recent")
    public ResponseEntity<List<BulkUploadErrorDTO>> getRecentErrors() {
        try {
            List<BulkUploadErrorDTO> errors = errorService.getRecentErrors();
            return ResponseEntity.ok(errors);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    // Mark error as resolved
    @PutMapping("/{errorId}/resolve")
    public ResponseEntity<BulkUploadErrorDTO> resolveError(@PathVariable Long errorId, 
                                                          @RequestBody Map<String, String> request) {
        try {
            String resolutionNotes = request.getOrDefault("resolution_notes", "");
            BulkUploadErrorDTO resolvedError = errorService.markAsResolved(errorId, resolutionNotes);
            return ResponseEntity.ok(resolvedError);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    // Get error statistics
    @GetMapping("/statistics")
    public ResponseEntity<BulkUploadErrorService.ErrorStatistics> getErrorStatistics() {
        try {
            BulkUploadErrorService.ErrorStatistics stats = errorService.getErrorStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    // Delete error
    @DeleteMapping("/{errorId}")
    public ResponseEntity<Void> deleteError(@PathVariable Long errorId) {
        try {
            errorService.deleteError(errorId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    // Get error count (for dashboard)
    @GetMapping("/count")
    public ResponseEntity<Long> getErrorCount() {
        try {
            BulkUploadErrorService.ErrorStatistics stats = errorService.getErrorStatistics();
            return ResponseEntity.ok(stats.getTotalErrors());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    // Get unresolved error count (for dashboard)
    @GetMapping("/unresolved-count")
    public ResponseEntity<Long> getUnresolvedErrorCount() {
        try {
            BulkUploadErrorService.ErrorStatistics stats = errorService.getErrorStatistics();
            return ResponseEntity.ok(stats.getUnresolvedErrors());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}