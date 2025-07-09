package com.example.fakedata;

import com.example.fakedata.controller.TaskController;
import com.example.fakedata.model.Task;
import com.example.fakedata.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllTasks() throws Exception {
        List<Task> mockTasks = createMockTasks();
        when(taskService.getAllTasks(anyInt())).thenReturn(mockTasks);
        when(taskService.getTotalTaskCount()).thenReturn(2L);

        mockMvc.perform(get("/api/tasks")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.metadata.totalItems").value(2))
                .andExpect(jsonPath("$.metadata.totalTaskCount").value(2));
    }

    @Test
    void testGetTaskById() throws Exception {
        Task mockTask = createMockTask();
        when(taskService.getTaskById("task-1")).thenReturn(Optional.of(mockTask));

        mockMvc.perform(get("/api/tasks/task-1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.taskId").value("task-1"))
                .andExpect(jsonPath("$.data.title").value("Test Task 1"))
                .andExpect(jsonPath("$.data.status").value("TODO"));
    }

    @Test
    void testGetTaskByIdNotFound() throws Exception {
        when(taskService.getTaskById("non-existent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/non-existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTask() throws Exception {
        Task newTask = new Task();
        newTask.setTitle("New Task");
        newTask.setDescription("New Description");
        newTask.setStatus(Task.TaskStatus.TODO);
        newTask.setPriority(Task.TaskPriority.HIGH);

        Task createdTask = createMockTask();
        createdTask.setTitle("New Task");
        createdTask.setDescription("New Description");

        when(taskService.createTask(any(Task.class))).thenReturn(createdTask);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.title").value("New Task"))
                .andExpect(jsonPath("$.data.description").value("New Description"))
                .andExpect(jsonPath("$.metadata.created").value(true));
    }

    @Test
    void testUpdateTask() throws Exception {
        Task updateData = new Task();
        updateData.setTitle("Updated Task");
        updateData.setDescription("Updated Description");
        updateData.setStatus(Task.TaskStatus.IN_PROGRESS);

        Task updatedTask = createMockTask();
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setStatus(Task.TaskStatus.IN_PROGRESS);

        when(taskService.updateTask(eq("task-1"), any(Task.class))).thenReturn(Optional.of(updatedTask));

        mockMvc.perform(put("/api/tasks/task-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.title").value("Updated Task"))
                .andExpect(jsonPath("$.data.description").value("Updated Description"))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.metadata.updated").value(true));
    }

    @Test
    void testUpdateTaskNotFound() throws Exception {
        Task updateData = new Task();
        updateData.setTitle("Updated Task");

        when(taskService.updateTask(eq("non-existent"), any(Task.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/tasks/non-existent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTask() throws Exception {
        when(taskService.deleteTask("task-1")).thenReturn(true);

        mockMvc.perform(delete("/api/tasks/task-1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.deleted").value(true))
                .andExpect(jsonPath("$.data.taskId").value("task-1"));
    }

    @Test
    void testDeleteTaskNotFound() throws Exception {
        when(taskService.deleteTask("non-existent")).thenReturn(false);

        mockMvc.perform(delete("/api/tasks/non-existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTasksByStatus() throws Exception {
        List<Task> mockTasks = createMockTasks();
        when(taskService.getTasksByStatus(eq(Task.TaskStatus.TODO), anyInt())).thenReturn(mockTasks);

        mockMvc.perform(get("/api/tasks/status/TODO")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.metadata.status").value("To Do"));
    }

    @Test
    void testGetTasksByPriority() throws Exception {
        List<Task> mockTasks = createMockTasks();
        when(taskService.getTasksByPriority(eq(Task.TaskPriority.HIGH), anyInt())).thenReturn(mockTasks);

        mockMvc.perform(get("/api/tasks/priority/HIGH")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.metadata.priority").value("High"));
    }

    @Test
    void testGetTasksByAssignee() throws Exception {
        List<Task> mockTasks = createMockTasks();
        when(taskService.getTasksByAssignee(eq("assignee-1"), anyInt())).thenReturn(mockTasks);

        mockMvc.perform(get("/api/tasks/assignee/assignee-1")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.metadata.assigneeId").value("assignee-1"));
    }

    @Test
    void testSearchTasks() throws Exception {
        List<Task> mockTasks = createMockTasks();
        when(taskService.searchTasks(eq("test"), anyInt())).thenReturn(mockTasks);

        mockMvc.perform(get("/api/tasks/search")
                .param("keyword", "test")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.metadata.searchKeyword").value("test"));
    }

    @Test
    void testGetTaskStats() throws Exception {
        List<Task> mockTasks = createMockTasks();
        when(taskService.getAllTasks(Integer.MAX_VALUE)).thenReturn(mockTasks);

        mockMvc.perform(get("/api/tasks/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.totalTasks").value(2))
                .andExpect(jsonPath("$.data.statusDistribution").exists())
                .andExpect(jsonPath("$.data.priorityDistribution").exists())
                .andExpect(jsonPath("$.data.categoryDistribution").exists())
                .andExpect(jsonPath("$.data.averageCompletionPercentage").exists());
    }

    @Test
    void testGetAllTasksWithFilters() throws Exception {
        List<Task> mockTasks = createMockTasks();
        when(taskService.getTasksByStatus(eq(Task.TaskStatus.TODO), anyInt())).thenReturn(mockTasks);

        mockMvc.perform(get("/api/tasks")
                .param("status", "TODO")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.metadata.filters.status").value("To Do"));
    }

    @Test
    void testGetAllTasksWithSearch() throws Exception {
        List<Task> mockTasks = createMockTasks();
        when(taskService.searchTasks(eq("test"), anyInt())).thenReturn(mockTasks);

        mockMvc.perform(get("/api/tasks")
                .param("search", "test")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.metadata.filters.search").value("test"));
    }

    private Task createMockTask() {
        Task task = new Task();
        task.setTaskId("task-1");
        task.setTitle("Test Task 1");
        task.setDescription("Test Description 1");
        task.setStatus(Task.TaskStatus.TODO);
        task.setPriority(Task.TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setAssigneeId("assignee-1");
        task.setAssigneeName("John Doe");
        task.setCategory("Development");
        task.setEstimatedHours(8);
        task.setCompletedPercentage(0);
        return task;
    }

    private List<Task> createMockTasks() {
        Task task1 = createMockTask();
        
        Task task2 = new Task();
        task2.setTaskId("task-2");
        task2.setTitle("Test Task 2");
        task2.setDescription("Test Description 2");
        task2.setStatus(Task.TaskStatus.IN_PROGRESS);
        task2.setPriority(Task.TaskPriority.MEDIUM);
        task2.setCreatedAt(LocalDateTime.now());
        task2.setUpdatedAt(LocalDateTime.now());
        task2.setAssigneeId("assignee-2");
        task2.setAssigneeName("Jane Smith");
        task2.setCategory("Testing");
        task2.setEstimatedHours(4);
        task2.setCompletedPercentage(50);

        return Arrays.asList(task1, task2);
    }
}