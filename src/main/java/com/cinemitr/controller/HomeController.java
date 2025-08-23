package com.cinemitr.controller;

import com.cinemitr.service.MovieInstagramLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private MovieInstagramLinkService instagramLinkService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("recentLinks", instagramLinkService.getActiveLinks());
        model.addAttribute("totalLinks", instagramLinkService.getActiveLinks().size());
        return "index";
    }

}