package com.example.jobscheduler.controller;

import com.example.jobscheduler.entity.JobExecutionHistory;
import com.example.jobscheduler.repository.JobExecutionHistoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/history")
class JobHistoryController {

    private final JobExecutionHistoryRepository historyRepository;

    public JobHistoryController(
            JobExecutionHistoryRepository historyRepository) {

        this.historyRepository = historyRepository;
    }

    @GetMapping
    public List<JobExecutionHistory> getAllHistory() {

        return historyRepository.findAll();
    }

    @GetMapping("/{jobId}")
    public List<JobExecutionHistory> getHistoryByJobId(
            @PathVariable Long jobId) {

        return historyRepository.findByJobId(jobId);
    }
}