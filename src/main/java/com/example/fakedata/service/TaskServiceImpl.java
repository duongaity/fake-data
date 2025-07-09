package com.example.fakedata.service;

import com.example.fakedata.model.Task;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final Faker faker = new Faker(new Locale("en"));
    private final Map<String, Task> taskStorage = new ConcurrentHashMap<>();

    // Initialize with some sample data
    public TaskServiceImpl() {
        initializeSampleTasks();
    }

    @Override
    public List<Task> getAllTasks(Integer limit) {
        return taskStorage.values().stream()
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .limit(limit != null ? limit : 10)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getTasksByStatus(Task.TaskStatus status, Integer limit) {
        return taskStorage.values().stream()
                .filter(task -> task.getStatus() == status)
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .limit(limit != null ? limit : 10)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getTasksByPriority(Task.TaskPriority priority, Integer limit) {
        return taskStorage.values().stream()
                .filter(task -> task.getPriority() == priority)
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .limit(limit != null ? limit : 10)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getTasksByAssignee(String assigneeId, Integer limit) {
        return taskStorage.values().stream()
                .filter(task -> Objects.equals(task.getAssigneeId(), assigneeId))
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .limit(limit != null ? limit : 10)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Task> getTaskById(String taskId) {
        return Optional.ofNullable(taskStorage.get(taskId));
    }

    @Override
    public Task createTask(Task task) {
        if (task.getTaskId() == null || task.getTaskId().isEmpty()) {
            task.setTaskId(UUID.randomUUID().toString());
        }
        
        LocalDateTime now = LocalDateTime.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        
        // Set defaults if not provided
        if (task.getStatus() == null) {
            task.setStatus(Task.TaskStatus.TODO);
        }
        if (task.getPriority() == null) {
            task.setPriority(Task.TaskPriority.MEDIUM);
        }
        if (task.getCompletedPercentage() == null) {
            task.setCompletedPercentage(0);
        }
        
        taskStorage.put(task.getTaskId(), task);
        return task;
    }

    @Override
    public Optional<Task> updateTask(String taskId, Task updatedTask) {
        Task existingTask = taskStorage.get(taskId);
        if (existingTask == null) {
            return Optional.empty();
        }

        // Update fields
        if (updatedTask.getTitle() != null) {
            existingTask.setTitle(updatedTask.getTitle());
        }
        if (updatedTask.getDescription() != null) {
            existingTask.setDescription(updatedTask.getDescription());
        }
        if (updatedTask.getStatus() != null) {
            existingTask.setStatus(updatedTask.getStatus());
        }
        if (updatedTask.getPriority() != null) {
            existingTask.setPriority(updatedTask.getPriority());
        }
        if (updatedTask.getDueDate() != null) {
            existingTask.setDueDate(updatedTask.getDueDate());
        }
        if (updatedTask.getAssigneeId() != null) {
            existingTask.setAssigneeId(updatedTask.getAssigneeId());
        }
        if (updatedTask.getAssigneeName() != null) {
            existingTask.setAssigneeName(updatedTask.getAssigneeName());
        }
        if (updatedTask.getCategory() != null) {
            existingTask.setCategory(updatedTask.getCategory());
        }
        if (updatedTask.getEstimatedHours() != null) {
            existingTask.setEstimatedHours(updatedTask.getEstimatedHours());
        }
        if (updatedTask.getCompletedPercentage() != null) {
            existingTask.setCompletedPercentage(updatedTask.getCompletedPercentage());
        }

        existingTask.setUpdatedAt(LocalDateTime.now());
        taskStorage.put(taskId, existingTask);
        return Optional.of(existingTask);
    }

    @Override
    public boolean deleteTask(String taskId) {
        return taskStorage.remove(taskId) != null;
    }

    @Override
    public long getTotalTaskCount() {
        return taskStorage.size();
    }

    @Override
    public List<Task> searchTasks(String keyword, Integer limit) {
        String lowerKeyword = keyword.toLowerCase();
        return taskStorage.values().stream()
                .filter(task -> 
                    task.getTitle().toLowerCase().contains(lowerKeyword) ||
                    task.getDescription().toLowerCase().contains(lowerKeyword) ||
                    (task.getAssigneeName() != null && task.getAssigneeName().toLowerCase().contains(lowerKeyword)) ||
                    (task.getCategory() != null && task.getCategory().toLowerCase().contains(lowerKeyword))
                )
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .limit(limit != null ? limit : 10)
                .collect(Collectors.toList());
    }

    private void initializeSampleTasks() {
        for (int i = 0; i < 15; i++) {
            Task task = generateFakeTask();
            taskStorage.put(task.getTaskId(), task);
        }
    }

    private Task generateFakeTask() {
        Task task = new Task();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTitle(generateTaskTitle());
        task.setDescription(faker.lorem().paragraph(2));
        task.setStatus(getRandomStatus());
        task.setPriority(getRandomPriority());
        task.setCategory(getRandomCategory());
        task.setAssigneeId(UUID.randomUUID().toString());
        task.setAssigneeName(faker.name().fullName());
        task.setEstimatedHours(faker.number().numberBetween(1, 40));
        task.setCompletedPercentage(getCompletedPercentageByStatus(task.getStatus()));
        
        LocalDateTime createdAt = LocalDateTime.now().minusDays(faker.number().numberBetween(0, 30));
        task.setCreatedAt(createdAt);
        task.setUpdatedAt(createdAt.plusHours(faker.number().numberBetween(0, 48)));
        
        // Set due date based on priority
        int daysToAdd = switch (task.getPriority()) {
            case URGENT -> faker.number().numberBetween(1, 3);
            case HIGH -> faker.number().numberBetween(2, 7);
            case MEDIUM -> faker.number().numberBetween(5, 14);
            case LOW -> faker.number().numberBetween(10, 30);
        };
        task.setDueDate(createdAt.plusDays(daysToAdd));
        
        return task;
    }

    private String generateTaskTitle() {
        String[] prefixes = {
            "Implement", "Fix", "Update", "Create", "Design", "Review", "Test", 
            "Deploy", "Configure", "Optimize", "Refactor", "Document"
        };
        String[] subjects = {
            "user authentication", "database schema", "API endpoints", "frontend components",
            "payment integration", "email notifications", "file upload", "search functionality",
            "user dashboard", "admin panel", "mobile app", "security features",
            "performance monitoring", "data migration", "third-party integration"
        };
        
        return faker.options().option(prefixes) + " " + faker.options().option(subjects);
    }

    private Task.TaskStatus getRandomStatus() {
        Task.TaskStatus[] statuses = Task.TaskStatus.values();
        return statuses[faker.number().numberBetween(0, statuses.length)];
    }

    private Task.TaskPriority getRandomPriority() {
        Task.TaskPriority[] priorities = Task.TaskPriority.values();
        return priorities[faker.number().numberBetween(0, priorities.length)];
    }

    private String getRandomCategory() {
        String[] categories = {
            "Development", "Testing", "Design", "Documentation", "DevOps", 
            "Security", "Performance", "Bug Fix", "Feature", "Maintenance"
        };
        return faker.options().option(categories);
    }

    private Integer getCompletedPercentageByStatus(Task.TaskStatus status) {
        return switch (status) {
            case TODO -> 0;
            case IN_PROGRESS -> faker.number().numberBetween(10, 70);
            case IN_REVIEW -> faker.number().numberBetween(80, 95);
            case DONE -> 100;
            case CANCELLED -> faker.number().numberBetween(0, 50);
        };
    }
}