package com.siva.tracker;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "Job Tracker v2 - deployed automatically by Jenkins. Try /applications or /actuator/health";
    }
}