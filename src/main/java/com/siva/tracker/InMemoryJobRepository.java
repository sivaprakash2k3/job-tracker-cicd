package com.siva.tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!aws")
public class InMemoryJobRepository implements JobRepository {
    private final Map<String, JobApplication> store = new ConcurrentHashMap<>();

    @Override
    public JobApplication save(JobApplication job) {
        String id = UUID.randomUUID().toString();
        JobApplication saved = new JobApplication(id, job.company(), job.role(), job.status());
        store.put(id, saved);
        return saved;
    }

    @Override
    public List<JobApplication> findAll() {
        return new ArrayList<>(store.values());
    }
}