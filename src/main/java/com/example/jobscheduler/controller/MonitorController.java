package com.example.jobscheduler.controller;

import com.example.jobscheduler.entity.JobStatus;
import com.example.jobscheduler.repository.JobRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/monitor")
public class MonitorController {

    private final JobRepository jobRepository;

    public MonitorController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {

        Map<String, Object> response = new HashMap<>();

        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());

        return response;
    }

    @GetMapping("/stats")
    public Map<String, Long> stats() {

        Map<String, Long> stats = new HashMap<>();

        stats.put(
                "pending",
                jobRepository.countByStatus(JobStatus.PENDING));

        stats.put(
                "running",
                jobRepository.countByStatus(JobStatus.RUNNING));

        stats.put(
                "completed",
                jobRepository.countByStatus(JobStatus.COMPLETED));

        stats.put(
                "failed",
                jobRepository.countByStatus(JobStatus.FAILED));

        stats.put(
                "cancelled",
                jobRepository.countByStatus(JobStatus.CANCELLED));

        return stats;
    }
}