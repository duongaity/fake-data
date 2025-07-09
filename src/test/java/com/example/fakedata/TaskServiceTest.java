package com.example.fakedata;

import com.example.fakedata.model.Task;
import com.example.fakedata.service.TaskService;
import com.example.fakedata.service.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl();
    }

    @Test
    void testGetAllTasksReturnCorrectNumber() {
        int limit = 5;
        List<Task> tasks = taskService.getAllTasks(limit);

        assertThat(tasks).hasSize(limit);
        assertThat(tasks).allSatisfy(task -> {
            assertThat(task.getTaskId()).isNotBlank();
            assertThat(task.getTitle()).isNotBlank();
            assertThat(task.getDescription()).isNotBlank();
            assertThat(task.getStatus()).isNotNull();
            assertThat(task.getPriority()).isNotNull();
            assertThat(task.getCreatedAt()).isNotNull();
            assertThat(task.getUpdatedAt()).isNotNull();
            assertThat(task.getAssigneeId()).isNotBlank();
            assertThat(task.getAssigneeName()).isNotBlank();
            assertThat(task.getCategory()).isNotBlank();
            assertThat(task.getEstimatedHours()).isPositive();
            assertThat(task.getCompletedPercentage()).isBetween(0, 100);
        });
    }

    @Test
    void testCreateTaskWithValidData() {
        Task newTask = new Task();
        newTask.setTitle("Test Task");
        newTask.setDescription("Test Description");
        newTask.setStatus(Task.TaskStatus.TODO);
        newTask.setPriority(Task.TaskPriority.HIGH);

        Task createdTask = taskService.createTask(newTask);

        assertThat(createdTask).isNotNull();
        assertThat(createdTask.getTaskId()).isNotBlank();
        assertThat(createdTask.getTitle()).isEqualTo("Test Task");
        assertThat(createdTask.getDescription()).isEqualTo("Test Description");
        assertThat(createdTask.getStatus()).isEqualTo(Task.TaskStatus.TODO);
        assertThat(createdTask.getPriority()).isEqualTo(Task.TaskPriority.HIGH);
        assertThat(createdTask.getCreatedAt()).isNotNull();
        assertThat(createdTask.getUpdatedAt()).isNotNull();
        assertThat(createdTask.getCompletedPercentage()).isEqualTo(0);
    }

    @Test
    void testCreateTaskWithDefaults() {
        Task newTask = new Task();
        newTask.setTitle("Minimal Task");
        newTask.setDescription("Minimal Description");

        Task createdTask = taskService.createTask(newTask);

        assertThat(createdTask.getStatus()).isEqualTo(Task.TaskStatus.TODO);
        assertThat(createdTask.getPriority()).isEqualTo(Task.TaskPriority.MEDIUM);
        assertThat(createdTask.getCompletedPercentage()).isEqualTo(0);
    }

    @Test
    void testGetTaskByIdReturnCorrectTask() {
        // Create a task first
        Task newTask = new Task();
        newTask.setTitle("Findable Task");
        newTask.setDescription("This task should be findable");
        Task createdTask = taskService.createTask(newTask);

        // Find the task by ID
        Optional<Task> foundTask = taskService.getTaskById(createdTask.getTaskId());

        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTaskId()).isEqualTo(createdTask.getTaskId());
        assertThat(foundTask.get().getTitle()).isEqualTo("Findable Task");
    }

    @Test
    void testGetTaskByIdReturnEmptyForNonExistentTask() {
        Optional<Task> foundTask = taskService.getTaskById("non-existent-id");
        assertThat(foundTask).isEmpty();
    }

    @Test
    void testUpdateTaskWithValidData() {
        // Create a task first
        Task newTask = new Task();
        newTask.setTitle("Original Task");
        newTask.setDescription("Original Description");
        Task createdTask = taskService.createTask(newTask);

        // Update the task
        Task updateData = new Task();
        updateData.setTitle("Updated Task");
        updateData.setDescription("Updated Description");
        updateData.setStatus(Task.TaskStatus.IN_PROGRESS);
        updateData.setCompletedPercentage(50);

        Optional<Task> updatedTask = taskService.updateTask(createdTask.getTaskId(), updateData);

        assertThat(updatedTask).isPresent();
        assertThat(updatedTask.get().getTitle()).isEqualTo("Updated Task");
        assertThat(updatedTask.get().getDescription()).isEqualTo("Updated Description");
        assertThat(updatedTask.get().getStatus()).isEqualTo(Task.TaskStatus.IN_PROGRESS);
        assertThat(updatedTask.get().getCompletedPercentage()).isEqualTo(50);
        assertThat(updatedTask.get().getUpdatedAt()).isAfter(updatedTask.get().getCreatedAt());
    }

    @Test
    void testUpdateNonExistentTaskReturnEmpty() {
        Task updateData = new Task();
        updateData.setTitle("Updated Task");

        Optional<Task> updatedTask = taskService.updateTask("non-existent-id", updateData);
        assertThat(updatedTask).isEmpty();
    }

    @Test
    void testDeleteTaskReturnTrueForExistingTask() {
        // Create a task first
        Task newTask = new Task();
        newTask.setTitle("Task to Delete");
        Task createdTask = taskService.createTask(newTask);

        // Delete the task
        boolean deleted = taskService.deleteTask(createdTask.getTaskId());
        assertThat(deleted).isTrue();

        // Verify task is deleted
        Optional<Task> foundTask = taskService.getTaskById(createdTask.getTaskId());
        assertThat(foundTask).isEmpty();
    }

    @Test
    void testDeleteNonExistentTaskReturnFalse() {
        boolean deleted = taskService.deleteTask("non-existent-id");
        assertThat(deleted).isFalse();
    }

    @Test
    void testGetTasksByStatus() {
        // Create tasks with specific status
        Task todoTask = new Task();
        todoTask.setTitle("TODO Task");
        todoTask.setStatus(Task.TaskStatus.TODO);
        taskService.createTask(todoTask);

        Task inProgressTask = new Task();
        inProgressTask.setTitle("In Progress Task");
        inProgressTask.setStatus(Task.TaskStatus.IN_PROGRESS);
        taskService.createTask(inProgressTask);

        List<Task> todoTasks = taskService.getTasksByStatus(Task.TaskStatus.TODO, 10);
        assertThat(todoTasks).isNotEmpty();
        assertThat(todoTasks).allSatisfy(task -> 
            assertThat(task.getStatus()).isEqualTo(Task.TaskStatus.TODO)
        );
    }

    @Test
    void testGetTasksByPriority() {
        // Create tasks with specific priority
        Task highPriorityTask = new Task();
        highPriorityTask.setTitle("High Priority Task");
        highPriorityTask.setPriority(Task.TaskPriority.HIGH);
        taskService.createTask(highPriorityTask);

        List<Task> highPriorityTasks = taskService.getTasksByPriority(Task.TaskPriority.HIGH, 10);
        assertThat(highPriorityTasks).isNotEmpty();
        assertThat(highPriorityTasks).allSatisfy(task -> 
            assertThat(task.getPriority()).isEqualTo(Task.TaskPriority.HIGH)
        );
    }

    @Test
    void testGetTasksByAssignee() {
        String assigneeId = "test-assignee-123";
        
        // Create task with specific assignee
        Task assignedTask = new Task();
        assignedTask.setTitle("Assigned Task");
        assignedTask.setAssigneeId(assigneeId);
        taskService.createTask(assignedTask);

        List<Task> assignedTasks = taskService.getTasksByAssignee(assigneeId, 10);
        assertThat(assignedTasks).isNotEmpty();
        assertThat(assignedTasks).allSatisfy(task -> 
            assertThat(task.getAssigneeId()).isEqualTo(assigneeId)
        );
    }

    @Test
    void testSearchTasks() {
        // Create task with searchable content
        Task searchableTask = new Task();
        searchableTask.setTitle("Searchable Task Title");
        searchableTask.setDescription("This task has searchable content");
        searchableTask.setCategory("SearchableCategory");
        taskService.createTask(searchableTask);

        // Search by title
        List<Task> titleResults = taskService.searchTasks("Searchable", 10);
        assertThat(titleResults).isNotEmpty();

        // Search by description
        List<Task> descriptionResults = taskService.searchTasks("searchable content", 10);
        assertThat(descriptionResults).isNotEmpty();

        // Search by category
        List<Task> categoryResults = taskService.searchTasks("SearchableCategory", 10);
        assertThat(categoryResults).isNotEmpty();
    }

    @Test
    void testGetTotalTaskCount() {
        long initialCount = taskService.getTotalTaskCount();
        
        // Create a new task
        Task newTask = new Task();
        newTask.setTitle("Count Test Task");
        taskService.createTask(newTask);

        long newCount = taskService.getTotalTaskCount();
        assertThat(newCount).isEqualTo(initialCount + 1);
    }

    @Test
    void testTaskStatusEnum() {
        assertThat(Task.TaskStatus.TODO.getDisplayName()).isEqualTo("To Do");
        assertThat(Task.TaskStatus.IN_PROGRESS.getDisplayName()).isEqualTo("In Progress");
        assertThat(Task.TaskStatus.IN_REVIEW.getDisplayName()).isEqualTo("In Review");
        assertThat(Task.TaskStatus.DONE.getDisplayName()).isEqualTo("Done");
        assertThat(Task.TaskStatus.CANCELLED.getDisplayName()).isEqualTo("Cancelled");
    }

    @Test
    void testTaskPriorityEnum() {
        assertThat(Task.TaskPriority.LOW.getDisplayName()).isEqualTo("Low");
        assertThat(Task.TaskPriority.MEDIUM.getDisplayName()).isEqualTo("Medium");
        assertThat(Task.TaskPriority.HIGH.getDisplayName()).isEqualTo("High");
        assertThat(Task.TaskPriority.URGENT.getDisplayName()).isEqualTo("Urgent");
    }
}