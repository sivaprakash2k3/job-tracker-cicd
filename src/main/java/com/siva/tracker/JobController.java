package com.siva.tracker;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
public class JobController {
    private final Map<Integer, JobApplication> store = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger();

    @GetMapping
    public List<JobApplication> all() {
        return new ArrayList<>(store.values());
    }

    @PostMapping
    public JobApplication add(@RequestBody JobApplication req) {
        int id = seq.incrementAndGet();
        JobApplication saved = new JobApplication(id, req.company(), req.role(), req.status());
        store.put(id, saved);
        return saved;
    }
}