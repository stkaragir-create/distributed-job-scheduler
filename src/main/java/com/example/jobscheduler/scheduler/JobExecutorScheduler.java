package com.example.jobscheduler.scheduler;

import com.example.jobscheduler.entity.Job;
import com.example.jobscheduler.entity.JobExecutionHistory;
import com.example.jobscheduler.entity.JobStatus;
import com.example.jobscheduler.repository.JobExecutionHistoryRepository;
import com.example.jobscheduler.repository.JobRepository;
import com.example.jobscheduler.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class JobExecutorScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(JobExecutorScheduler.class);

    private static final int MAX_RETRIES = 3;

    private final JobRepository jobRepository;
    private final EmailService emailService;
    private final JobExecutionHistoryRepository historyRepository;

    public JobExecutorScheduler(
            JobRepository jobRepository,
            EmailService emailService,
            JobExecutionHistoryRepository historyRepository) {

        this.jobRepository = jobRepository;
        this.emailService = emailService;
        this.historyRepository = historyRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void executeJobs() {

        logger.info("Scheduler triggered");

        List<Job> jobs =
                jobRepository.findByStatusAndScheduledTimeBefore(
                        JobStatus.PENDING,
                        LocalDateTime.now());

        logger.info("Found {} pending jobs", jobs.size());

        for (Job job : jobs) {

            try {

                // Mark as RUNNING
                job.setStatus(JobStatus.RUNNING);
                jobRepository.save(job);

                logger.info(
                        "Executing Job : {}",
                        job.getJobName());

                /*
                 * Actual business logic goes here
                 */

                // Simulate failure
                if ("FAIL".equalsIgnoreCase(job.getJobType())) {
                    throw new RuntimeException("Simulated Job Failure");
                }

                // Recurring Job
                if (Boolean.TRUE.equals(job.getRecurring())
                        && job.getCronExpression() != null
                        && !job.getCronExpression().isBlank()) {

                    LocalDateTime nextRun =
                            CronExpression
                                    .parse(job.getCronExpression())
                                    .next(LocalDateTime.now());

                    job.setScheduledTime(nextRun);
                    job.setStatus(JobStatus.PENDING);

                    logger.info(
                            "Recurring Job Rescheduled : {} Next Run = {}",
                            job.getJobName(),
                            nextRun
                    );

                } else {

                    // One-time Job
                    job.setStatus(JobStatus.COMPLETED);

                    logger.info(
                            "Job Completed : {}",
                            job.getJobName()
                    );
                }

                // Save SUCCESS history
                JobExecutionHistory successHistory =
                        new JobExecutionHistory();

                successHistory.setJobId(job.getId());
                successHistory.setJobName(job.getJobName());
                successHistory.setExecutionTime(LocalDateTime.now());
                successHistory.setStatus("SUCCESS");
                successHistory.setMessage("Job executed successfully");

                historyRepository.save(successHistory);

            } catch (Exception e) {

                Integer retries =
                        job.getRetryCount() == null
                                ? 0
                                : job.getRetryCount();

                retries++;

                job.setRetryCount(retries);

                JobExecutionHistory failureHistory =
                        new JobExecutionHistory();

                failureHistory.setJobId(job.getId());
                failureHistory.setJobName(job.getJobName());
                failureHistory.setExecutionTime(LocalDateTime.now());

                if (retries < MAX_RETRIES) {

                    job.setStatus(JobStatus.PENDING);

                    // Retry after 1 minute
                    job.setScheduledTime(
                            LocalDateTime.now().plusMinutes(1));

                    logger.warn(
                            "Retrying Job : {} Retry Count = {}",
                            job.getJobName(),
                            retries
                    );

                    failureHistory.setStatus("RETRY_" + retries);
                    failureHistory.setMessage(
                            "Job failed. Retrying attempt " + retries);

                } else {

                    job.setStatus(JobStatus.FAILED);

                    logger.error(
                            "Job Permanently Failed : {}",
                            job.getJobName(),
                            e
                    );

                    failureHistory.setStatus("FAILED");
                    failureHistory.setMessage(e.getMessage());

                    // Send email notification
                    if (job.getUserEmail() != null
                            && !job.getUserEmail().isBlank()) {

                        emailService.sendFailureMail(
                                job.getUserEmail(),
                                job.getJobName()
                        );
                    }
                }

                historyRepository.save(failureHistory);
            }

            jobRepository.save(job);
        }
    }
}