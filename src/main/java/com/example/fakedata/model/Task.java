package com.example.fakedata.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Task implements Serializable {
    private String taskId;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String assigneeId;
    private String assigneeName;
    private String category;
    private Integer estimatedHours;
    private Integer completedPercentage;

    public enum TaskStatus {
        TODO("To Do"),
        IN_PROGRESS("In Progress"),
        IN_REVIEW("In Review"),
        DONE("Done"),
        CANCELLED("Cancelled");

        private final String displayName;

        TaskStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum TaskPriority {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        URGENT("Urgent");

        private final String displayName;

        TaskPriority(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}