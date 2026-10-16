package com.siva.tracker;

import java.util.List;

public interface JobRepository {
    JobApplication save(JobApplication job);
    List<JobApplication> findAll();
}