package com.example.jobscheduler.repository;

import com.example.jobscheduler.entity.Job;
import com.example.jobscheduler.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatusAndScheduledTimeBefore(JobStatus status,
                                                 LocalDateTime time);

    Long countByStatus(JobStatus jobStatus);

    List<Job> findByStatus(JobStatus jobStatus);
}