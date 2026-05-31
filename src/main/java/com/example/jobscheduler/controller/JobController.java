package com.example.jobscheduler.controller;

import com.example.jobscheduler.entity.Job;
import com.example.jobscheduler.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @PostMapping
    public Job createJob(@RequestBody Job job) {

        return jobService.createJob(job);
    }

    @GetMapping
    public List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }
    @PutMapping("/{id}/cancel")
    public Job cancelJob(@PathVariable Long id) {
        return jobService.cancelJob(id);
    }
    @PutMapping("/{id}/reschedule")
    public Job rescheduleJob(
            @PathVariable Long id,
            @RequestParam String scheduledTime) {

        LocalDateTime newTime = LocalDateTime.parse(scheduledTime);

        return jobService.rescheduleJob(id, newTime);
    }
    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        return jobService.getStats();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(
            @PathVariable Long id) {

        jobService.deleteJob(id);

        return ResponseEntity.ok(
                "Job deleted successfully. ID = " + id
        );
    }
}