package com.siva.tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class JobControllerTest {
    @Test
    void addStoresApplication() {
        JobController c = new JobController();
        JobApplication saved = c.add(new JobApplication(0, "Acme", "DevOps Engineer", "APPLIED"));
        assertEquals(1, saved.id());
        assertEquals(1, c.all().size());
    }
}