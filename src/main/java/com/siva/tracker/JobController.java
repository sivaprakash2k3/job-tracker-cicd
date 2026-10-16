package com.siva.tracker;

import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
public class JobController {
    private final JobRepository repository;

    public JobController(JobRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<JobApplication> all() {
        return repository.findAll();
    }

    @PostMapping
    public JobApplication add(@RequestBody JobApplication req) {
        return repository.save(req);
    }
}