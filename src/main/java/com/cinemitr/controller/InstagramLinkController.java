package com.cinemitr.controller;

import com.cinemitr.model.MovieInstagramLink;
import com.cinemitr.service.MovieInstagramLinkService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
public class InstagramLinkController {

    @Autowired
    private MovieInstagramLinkService instagramLinkService;

    @GetMapping
    public String dashboard(Model model) {
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
        
        return "dashboard/index";
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
}