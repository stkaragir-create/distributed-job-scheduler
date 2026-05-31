# distributed-job-scheduler
Distributed Job Scheduler
Overview

Distributed Job Scheduler is a Spring Boot based backend application that allows users to schedule, execute, monitor, and manage jobs.

The system supports:

One-time job execution
Recurring jobs using Cron Expressions
Automatic retry mechanism
Failure notifications through Email
Job execution history tracking
Monitoring APIs
PostgreSQL database persistence

This project demonstrates backend system design concepts such as scheduling, retry handling, monitoring, and job lifecycle management.

Features
Job Submission

Users can submit jobs for immediate or future execution.

Recurring Jobs

Supports recurring jobs using Cron Expressions.

Examples:

Every minute → 0 * * * * *
Every hour → 0 0 * * * *
Every day at midnight → 0 0 0 * * *
Job Management
Create Job
View Jobs
Cancel Job
Reschedule Job
Delete Job
Retry Mechanism

If a job fails:

Retry Count increases
Job is retried automatically
Maximum retries = 3

After maximum retries:

Job status becomes FAILED
Failure email is sent to the user
Email Notifications

Users receive email notifications when a job permanently fails.

Job Execution History

Every execution attempt is stored in the JobExecutionHistory table.

Stores:

Job Id
Job Name
Execution Time
Status
Message
Monitoring

Monitoring endpoints provide system statistics:

Total Pending Jobs
Total Completed Jobs
Total Failed Jobs
Total Cancelled Jobs
Architecture

Client
|
v
REST APIs
|
v
Controller Layer
|
v
Service Layer
|
v
Repository Layer
|
v
PostgreSQL Database

Scheduler
|
v
Job Executor
|
+--> Retry Logic
|
+--> Execution History
|
+--> Email Notification

Technology Stack
Technology	Version
Java	17
Spring Boot	3.x
Spring Data JPA	Latest
PostgreSQL	Latest
Maven	Latest
Spring Scheduler	Built-in
Spring Mail	Built-in
Database Schema
Job Table
Column	Type
id	Long
jobName	String
jobType	String
payload	String
userEmail	String
status	Enum
scheduledTime	Timestamp
createdAt	Timestamp
retryCount	Integer
recurring	Boolean
cronExpression	String
Job Execution History Table
Column	Type
id	Long
jobId	Long
jobName	String
executionTime	Timestamp
status	String
message	String
API Endpoints
Create Job

POST /jobs

Request:

{
"jobName": "Email Job",
"jobType": "EMAIL",
"payload": "sample payload",
"userEmail": "user@gmail.com",
"scheduledTime": "2026-06-01T10:00:00",
"recurring": false
}
Get All Jobs

GET /jobs

Cancel Job

PUT /jobs/{id}/cancel

Reschedule Job

PUT /jobs/{id}/reschedule?scheduledTime=2026-06-02T12:00:00

Delete Job

DELETE /jobs/{id}

Job Statistics

GET /jobs/stats

Execution History

GET /history

Monitoring

GET /monitor/health

GET /monitor/stats

How To Run
Clone Repository
git clone https://github.com/stkaragir-create/distributed-job-scheduler.git
Navigate To Project
cd distributed-job-scheduler
Configure Database

Update application.properties:

spring.datasource.url=jdbc:postgresql://localhost:5432/jobscheduler
spring.datasource.username=postgres
spring.datasource.password=your_password
Configure Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
Run Application
mvn spring-boot:run

Application starts on:

http://localhost:8080
Future Enhancements
JWT Authentication
RabbitMQ Integration
Docker Support
Kubernetes Deployment
Distributed Locking
Job Priority Queue
Dashboard UI
Prometheus & Grafana Monitoring

Author:

Siddhesh Karagir

Backend Developer

Java | Spring Boot | PostgreSQL | Distributed Systems