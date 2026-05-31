package com.example.jobscheduler.repository;

import com.example.jobscheduler.entity.JobExecutionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobExecutionHistoryRepository
        extends JpaRepository<JobExecutionHistory, Long> {

    List<JobExecutionHistory> findByJobId(Long jobId);
}