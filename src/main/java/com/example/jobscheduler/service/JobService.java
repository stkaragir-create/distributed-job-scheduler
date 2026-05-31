package com.example.jobscheduler.service;

import com.example.jobscheduler.entity.Job;
import com.example.jobscheduler.entity.JobStatus;
import com.example.jobscheduler.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public Job createJob(Job job) {

        job.setStatus(JobStatus.PENDING);
        job.setCreatedAt(LocalDateTime.now());
        job.setRetryCount(0);
        if (Boolean.TRUE.equals(job.getRecurring())) {

            if (job.getCronExpression() == null ||
                    job.getCronExpression().isBlank()) {

                throw new RuntimeException(
                        "Cron expression is required for recurring jobs");
            }

            try {
                CronExpression.parse(
                        job.getCronExpression());
            } catch (Exception ex) {

                throw new RuntimeException(
                        "Invalid cron expression");
            }
        }

        return jobRepository.save(job);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }
    public Job cancelJob(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setStatus(JobStatus.CANCELLED);

        return jobRepository.save(job);
    }
    public Job rescheduleJob(Long id, LocalDateTime newTime) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setScheduledTime(newTime);
        job.setStatus(JobStatus.PENDING);

        return jobRepository.save(job);
    }
    public Map<String, Long> getStats() {

        Map<String, Long> stats = new HashMap<>();

        stats.put("pending",
                jobRepository.countByStatus(JobStatus.PENDING));

        stats.put("completed",
                jobRepository.countByStatus(JobStatus.COMPLETED));

        stats.put("failed",
                jobRepository.countByStatus(JobStatus.FAILED));

        stats.put("cancelled",
                jobRepository.countByStatus(JobStatus.CANCELLED));

        return stats;
    }

    public void deleteJob(Long id) {

            if (!jobRepository.existsById(id)) {
                throw new RuntimeException("Job not found with id: " + id);
            }

            jobRepository.deleteById(id);
        }

    public List<Job> getPendingJobs() {
        return jobRepository.findByStatus(JobStatus.PENDING);
    }
    public LocalDateTime calculateNextRun(Job job) {

        if (!Boolean.TRUE.equals(job.getRecurring())) {
            return null;
        }

        CronExpression cron =
                CronExpression.parse(
                        job.getCronExpression());

        return cron.next(LocalDateTime.now());
    }

}