package com.siva.tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class JobControllerTest {
    @Test
    void addStoresApplication() {
        JobController c = new JobController(new InMemoryJobRepository());
        JobApplication saved = c.add(new JobApplication(null, "Acme", "DevOps Engineer", "APPLIED"));
        assertNotNull(saved.id());
        assertEquals(1, c.all().size());
    }
}