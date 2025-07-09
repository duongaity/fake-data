package com.example.fakedata.controller;

import com.example.fakedata.model.ApiResponse;
import com.example.fakedata.model.Task;
import com.example.fakedata.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Management", description = "APIs for managing tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieve a list of tasks with optional filtering")
    public ResponseEntity<ApiResponse<List<Task>>> getAllTasks(
            @Parameter(description = "Maximum number of tasks to return") 
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @Parameter(description = "Filter by task status") 
            @RequestParam(required = false) Task.TaskStatus status,
            @Parameter(description = "Filter by task priority") 
            @RequestParam(required = false) Task.TaskPriority priority,
            @Parameter(description = "Filter by assignee ID") 
            @RequestParam(required = false) String assigneeId,
            @Parameter(description = "Search keyword in title, description, assignee name, or category") 
            @RequestParam(required = false) String search) {
        
        List<Task> tasks;
        
        if (search != null && !search.trim().isEmpty()) {
            tasks = taskService.searchTasks(search.trim(), limit);
        } else if (status != null) {
            tasks = taskService.getTasksByStatus(status, limit);
        } else if (priority != null) {
            tasks = taskService.getTasksByPriority(priority, limit);
        } else if (assigneeId != null) {
            tasks = taskService.getTasksByAssignee(assigneeId, limit);
        } else {
            tasks = taskService.getAllTasks(limit);
        }
        
        ApiResponse<List<Task>> response = new ApiResponse<>();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("totalItems", tasks.size());
        metadata.put("totalTaskCount", taskService.getTotalTaskCount());
        metadata.put("currentTime", new Date().getTime());
        metadata.put("filters", createFilterMetadata(status, priority, assigneeId, search));
        
        response.ok(tasks, metadata);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get task by ID", description = "Retrieve a specific task by its ID")
    public ResponseEntity<ApiResponse<Task>> getTaskById(
            @Parameter(description = "Task ID") 
            @PathVariable String taskId) {
        
        Optional<Task> task = taskService.getTaskById(taskId);
        ApiResponse<Task> response = new ApiResponse<>();
        
        if (task.isPresent()) {
            response.ok(task.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create a new task", description = "Create a new task with the provided details")
    public ResponseEntity<ApiResponse<Task>> createTask(
            @Parameter(description = "Task details") 
            @RequestBody Task task) {
        
        try {
            Task createdTask = taskService.createTask(task);
            ApiResponse<Task> response = new ApiResponse<>();
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("created", true);
            metadata.put("createdAt", new Date().getTime());
            
            response.ok(createdTask, metadata);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update a task", description = "Update an existing task with new details")
    public ResponseEntity<ApiResponse<Task>> updateTask(
            @Parameter(description = "Task ID") 
            @PathVariable String taskId,
            @Parameter(description = "Updated task details") 
            @RequestBody Task task) {
        
        Optional<Task> updatedTask = taskService.updateTask(taskId, task);
        ApiResponse<Task> response = new ApiResponse<>();
        
        if (updatedTask.isPresent()) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("updated", true);
            metadata.put("updatedAt", new Date().getTime());
            
            response.ok(updatedTask.get(), metadata);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a task", description = "Delete a task by its ID")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteTask(
            @Parameter(description = "Task ID") 
            @PathVariable String taskId) {
        
        boolean deleted = taskService.deleteTask(taskId);
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        
        if (deleted) {
            Map<String, Object> result = new HashMap<>();
            result.put("deleted", true);
            result.put("taskId", taskId);
            result.put("deletedAt", new Date().getTime());
            
            response.ok(result);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status", description = "Retrieve tasks filtered by status")
    public ResponseEntity<ApiResponse<List<Task>>> getTasksByStatus(
            @Parameter(description = "Task status") 
            @PathVariable Task.TaskStatus status,
            @Parameter(description = "Maximum number of tasks to return") 
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        List<Task> tasks = taskService.getTasksByStatus(status, limit);
        ApiResponse<List<Task>> response = new ApiResponse<>();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("totalItems", tasks.size());
        metadata.put("status", status.getDisplayName());
        metadata.put("currentTime", new Date().getTime());
        
        response.ok(tasks, metadata);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get tasks by priority", description = "Retrieve tasks filtered by priority")
    public ResponseEntity<ApiResponse<List<Task>>> getTasksByPriority(
            @Parameter(description = "Task priority") 
            @PathVariable Task.TaskPriority priority,
            @Parameter(description = "Maximum number of tasks to return") 
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        List<Task> tasks = taskService.getTasksByPriority(priority, limit);
        ApiResponse<List<Task>> response = new ApiResponse<>();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("totalItems", tasks.size());
        metadata.put("priority", priority.getDisplayName());
        metadata.put("currentTime", new Date().getTime());
        
        response.ok(tasks, metadata);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/assignee/{assigneeId}")
    @Operation(summary = "Get tasks by assignee", description = "Retrieve tasks assigned to a specific user")
    public ResponseEntity<ApiResponse<List<Task>>> getTasksByAssignee(
            @Parameter(description = "Assignee ID") 
            @PathVariable String assigneeId,
            @Parameter(description = "Maximum number of tasks to return") 
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        List<Task> tasks = taskService.getTasksByAssignee(assigneeId, limit);
        ApiResponse<List<Task>> response = new ApiResponse<>();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("totalItems", tasks.size());
        metadata.put("assigneeId", assigneeId);
        metadata.put("currentTime", new Date().getTime());
        
        response.ok(tasks, metadata);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search tasks", description = "Search tasks by keyword in title, description, assignee name, or category")
    public ResponseEntity<ApiResponse<List<Task>>> searchTasks(
            @Parameter(description = "Search keyword") 
            @RequestParam String keyword,
            @Parameter(description = "Maximum number of tasks to return") 
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        List<Task> tasks = taskService.searchTasks(keyword, limit);
        ApiResponse<List<Task>> response = new ApiResponse<>();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("totalItems", tasks.size());
        metadata.put("searchKeyword", keyword);
        metadata.put("currentTime", new Date().getTime());
        
        response.ok(tasks, metadata);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get task statistics", description = "Get statistics about tasks")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTaskStats() {
        List<Task> allTasks = taskService.getAllTasks(Integer.MAX_VALUE);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTasks", allTasks.size());
        
        // Status distribution
        Map<String, Long> statusDistribution = allTasks.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    task -> task.getStatus().getDisplayName(),
                    java.util.stream.Collectors.counting()
                ));
        stats.put("statusDistribution", statusDistribution);
        
        // Priority distribution
        Map<String, Long> priorityDistribution = allTasks.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    task -> task.getPriority().getDisplayName(),
                    java.util.stream.Collectors.counting()
                ));
        stats.put("priorityDistribution", priorityDistribution);
        
        // Category distribution
        Map<String, Long> categoryDistribution = allTasks.stream()
                .filter(task -> task.getCategory() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                    Task::getCategory,
                    java.util.stream.Collectors.counting()
                ));
        stats.put("categoryDistribution", categoryDistribution);
        
        // Average completion percentage
        double avgCompletion = allTasks.stream()
                .mapToInt(Task::getCompletedPercentage)
                .average()
                .orElse(0.0);
        stats.put("averageCompletionPercentage", Math.round(avgCompletion * 100.0) / 100.0);
        
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("generatedAt", new Date().getTime());
        
        response.ok(stats, metadata);
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createFilterMetadata(Task.TaskStatus status, Task.TaskPriority priority, 
                                                   String assigneeId, String search) {
        Map<String, Object> filters = new HashMap<>();
        if (status != null) filters.put("status", status.getDisplayName());
        if (priority != null) filters.put("priority", priority.getDisplayName());
        if (assigneeId != null) filters.put("assigneeId", assigneeId);
        if (search != null) filters.put("search", search);
        return filters;
    }
}