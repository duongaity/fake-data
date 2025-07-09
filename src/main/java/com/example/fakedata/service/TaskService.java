package com.example.fakedata.service;

import com.example.fakedata.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    List<Task> getAllTasks(Integer limit);
    List<Task> getTasksByStatus(Task.TaskStatus status, Integer limit);
    List<Task> getTasksByPriority(Task.TaskPriority priority, Integer limit);
    List<Task> getTasksByAssignee(String assigneeId, Integer limit);
    Optional<Task> getTaskById(String taskId);
    Task createTask(Task task);
    Optional<Task> updateTask(String taskId, Task task);
    boolean deleteTask(String taskId);
    long getTotalTaskCount();
    List<Task> searchTasks(String keyword, Integer limit);
}