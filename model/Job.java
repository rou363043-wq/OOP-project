pacakge model;

import java.time.LocalDateTime;

public class Job{
    private String jobId,studentId,title,applicationName,description,cancelledBy,cancellationReason,status,assignedGpuId;
    private int workloadUnits,estimatedDuration,minutesRun;
    private LocalDateTime submissionTime;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private LocalDateTime cancellationTime;

     public Job(String jobId, String studentId, String title, String applicationName,
               String description, int durationMinutes, LocalDateTime submissionTime) {
        requireText(jobId, "Job ID");
        requireText(studentId, "Student ID");
        requireText(title, "Title");
        requireText(applicationName, "Application name");
        requireText(description, "Description");
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be positive.");
        }
        if (submissionTime == null) {
            throw new IllegalArgumentException("Submission time is required.");
        }

        this.jobId = jobId.trim();
        this.studentId = studentId.trim();
        this.title = title.trim();
        this.applicationName = applicationName.trim();
        this.description = description.trim();
        this.workloadUnits = durationMinutes;
        this.estimatedDuration = durationMinutes;
        this.submissionTime = submissionTime;
        this.status = "QUEUED";
    }
     public Job(String jobId, String studentId, String title, String applicationName,
               String description, int durationMinutes, LocalDateTime submissionTime,
               String status, String assignedGpuId, LocalDateTime startTime,
               LocalDateTime finishTime, int minutesRun,
               LocalDateTime cancellationTime, String cancelledBy, String cancellationReason) {
        this(jobId, studentId, title, applicationName, description, durationMinutes, submissionTime);
        this.status = status;
        this.assignedGpuId = assignedGpuId;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.minutesRun = Math.max(0, minutesRun);
        this.cancellationTime = cancellationTime;
        this.cancelledBy = cancelledBy;
        this.cancellationReason = cancellationReason;
    }

    public void start(String gpuId, LocalDateTime currentTime) {
        if (!"QUEUED".equals(status)) {
            throw new IllegalStateException("Only a QUEUED job can start.");
        }
        requireText(gpuId, "GPU ID");
        if (currentTime == null) {
            throw new IllegalArgumentException("Start time is required.");
        }
        this.assignedGpuId = gpuId.trim();
        this.startTime = currentTime;
        this.status = "RUNNING";
    }

    public void tickOneMinute() {
        if (!"RUNNING".equals(status)) {
            throw new IllegalStateException("Only a RUNNING job can advance.");
        }
        if (minutesRun < estimatedDuration) {
            minutesRun++;
        }
    }

    public int getRemainingMinutes() {
        if (!"QUEUED".equals(status) && !"RUNNING".equals(status)) {
            return 0;
        }
        return Math.max(0, estimatedDuration - minutesRun);
    }

    public void complete(LocalDateTime currentTime) {
        if (!"RUNNING".equals(status)) {
            throw new IllegalStateException("Only a RUNNING job can complete.");
        }
        if (currentTime == null) {
            throw new IllegalArgumentException("Finish time is required.");
        }
        this.finishTime = currentTime;
        this.status = "COMPLETED";
    }

    public void cancel(String byUser, String reason, LocalDateTime currentTime) {
        if (!"QUEUED".equals(status) && !"RUNNING".equals(status)) {
            throw new IllegalStateException("Job cannot be cancelled.");
        }
        requireText(byUser, "Cancelling user");
        requireText(reason, "Cancellation reason");
        if (currentTime == null) {
            throw new IllegalArgumentException("Cancellation time is required.");
        }
        if ("RUNNING".equals(status)) {
            this.finishTime = currentTime;
        }
        this.cancelledBy = byUser.trim();
        this.cancellationTime = currentTime;
        this.cancellationReason = reason.trim();
        this.status = "CANCELLED";
    }

    public int getChargedMinutes() {
        if ("COMPLETED".equals(status)) {
            return estimatedDuration;
        }
        if ("CANCELLED".equals(status)) {
            return minutesRun;
        }
        return 0;
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }

    public String getJobId() { return jobId; }
    public String getStudentId() { return studentId; }
    public String getTitle() { return title; }
    public String getApplicationName() { return applicationName; }
    public String getDescription() { return description; }
    public int getWorkloadUnits() { return workloadUnits; }
    public int getEstimatedDuration() { return estimatedDuration; }
    public LocalDateTime getSubmissionTime() { return submissionTime; }
    public String getStatus() { return status; }
    public String getAssignedGpuId() { return assignedGpuId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getFinishTime() { return finishTime; }
    public int getMinutesRun() { return minutesRun; }
    public LocalDateTime getCancellationTime() { return cancellationTime; }
    public String getCancelledBy() { return cancelledBy; }
    public String getCancellationReason() { return cancellationReason; }
}

}
