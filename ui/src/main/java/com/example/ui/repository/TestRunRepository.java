package com.example.ui.repository;

import com.example.ui.model.TestRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRunRepository extends JpaRepository<TestRun, Long> {
    // Check if we've already imported this file to avoid duplicates
    boolean existsByFileName(String fileName);
}