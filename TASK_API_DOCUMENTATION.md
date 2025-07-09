# Task Management API Documentation

This document describes the Task Management API endpoints that provide full CRUD operations for managing tasks.

## Overview

The Task API allows you to create, read, update, and delete tasks. It includes advanced features like filtering, searching, and statistics.

## Base URL

```
/api/tasks
```

## Task Model

```json
{
  "taskId": "string",
  "title": "string",
  "description": "string",
  "status": "TODO|IN_PROGRESS|IN_REVIEW|DONE|CANCELLED",
  "priority": "LOW|MEDIUM|HIGH|URGENT",
  "dueDate": "2025-07-09T14:00:00",
  "createdAt": "2025-07-09T14:00:00",
  "updatedAt": "2025-07-09T14:00:00",
  "assigneeId": "string",
  "assigneeName": "string",
  "category": "string",
  "estimatedHours": 8,
  "completedPercentage": 50
}
```

## API Endpoints

### 1. Get All Tasks

**GET** `/api/tasks`

Retrieve a list of tasks with optional filtering.

**Query Parameters:**
- `limit` (optional, default: 10) - Maximum number of tasks to return
- `status` (optional) - Filter by task status
- `priority` (optional) - Filter by task priority
- `assigneeId` (optional) - Filter by assignee ID
- `search` (optional) - Search keyword in title, description, assignee name, or category

**Example:**
```bash
GET /api/tasks?limit=5&status=TODO&priority=HIGH
```

**Response:**
```json
{
  "data": [
    {
      "taskId": "task-123",
      "title": "Implement user authentication",
      "description": "Add JWT-based authentication system",
      "status": "TODO",
      "priority": "HIGH",
      "dueDate": "2025-07-15T09:00:00",
      "createdAt": "2025-07-09T14:00:00",
      "updatedAt": "2025-07-09T14:00:00",
      "assigneeId": "user-456",
      "assigneeName": "John Doe",
      "category": "Development",
      "estimatedHours": 16,
      "completedPercentage": 0
    }
  ],
  "metadata": {
    "totalItems": 1,
    "totalTaskCount": 15,
    "currentTime": 1720533600000,
    "filters": {
      "status": "To Do",
      "priority": "High"
    }
  }
}
```

### 2. Get Task by ID

**GET** `/api/tasks/{taskId}`

Retrieve a specific task by its ID.

**Example:**
```bash
GET /api/tasks/task-123
```

**Response:**
```json
{
  "data": {
    "taskId": "task-123",
    "title": "Implement user authentication",
    "description": "Add JWT-based authentication system",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2025-07-15T09:00:00",
    "createdAt": "2025-07-09T14:00:00",
    "updatedAt": "2025-07-09T14:00:00",
    "assigneeId": "user-456",
    "assigneeName": "John Doe",
    "category": "Development",
    "estimatedHours": 16,
    "completedPercentage": 0
  }
}
```

### 3. Create Task

**POST** `/api/tasks`

Create a new task.

**Request Body:**
```json
{
  "title": "New Task",
  "description": "Task description",
  "status": "TODO",
  "priority": "MEDIUM",
  "dueDate": "2025-07-20T09:00:00",
  "assigneeId": "user-789",
  "assigneeName": "Jane Smith",
  "category": "Testing",
  "estimatedHours": 8
}
```

**Response:**
```json
{
  "data": {
    "taskId": "task-456",
    "title": "New Task",
    "description": "Task description",
    "status": "TODO",
    "priority": "MEDIUM",
    "dueDate": "2025-07-20T09:00:00",
    "createdAt": "2025-07-09T14:00:00",
    "updatedAt": "2025-07-09T14:00:00",
    "assigneeId": "user-789",
    "assigneeName": "Jane Smith",
    "category": "Testing",
    "estimatedHours": 8,
    "completedPercentage": 0
  },
  "metadata": {
    "created": true,
    "createdAt": 1720533600000
  }
}
```

### 4. Update Task

**PUT** `/api/tasks/{taskId}`

Update an existing task.

**Request Body:**
```json
{
  "title": "Updated Task Title",
  "status": "IN_PROGRESS",
  "completedPercentage": 25
}
```

**Response:**
```json
{
  "data": {
    "taskId": "task-456",
    "title": "Updated Task Title",
    "description": "Task description",
    "status": "IN_PROGRESS",
    "priority": "MEDIUM",
    "dueDate": "2025-07-20T09:00:00",
    "createdAt": "2025-07-09T14:00:00",
    "updatedAt": "2025-07-09T14:30:00",
    "assigneeId": "user-789",
    "assigneeName": "Jane Smith",
    "category": "Testing",
    "estimatedHours": 8,
    "completedPercentage": 25
  },
  "metadata": {
    "updated": true,
    "updatedAt": 1720535400000
  }
}
```

### 5. Delete Task

**DELETE** `/api/tasks/{taskId}`

Delete a task by its ID.

**Example:**
```bash
DELETE /api/tasks/task-456
```

**Response:**
```json
{
  "data": {
    "deleted": true,
    "taskId": "task-456",
    "deletedAt": 1720535400000
  }
}
```

### 6. Get Tasks by Status

**GET** `/api/tasks/status/{status}`

Retrieve tasks filtered by status.

**Example:**
```bash
GET /api/tasks/status/IN_PROGRESS?limit=10
```

### 7. Get Tasks by Priority

**GET** `/api/tasks/priority/{priority}`

Retrieve tasks filtered by priority.

**Example:**
```bash
GET /api/tasks/priority/HIGH?limit=10
```

### 8. Get Tasks by Assignee

**GET** `/api/tasks/assignee/{assigneeId}`

Retrieve tasks assigned to a specific user.

**Example:**
```bash
GET /api/tasks/assignee/user-456?limit=10
```

### 9. Search Tasks

**GET** `/api/tasks/search`

Search tasks by keyword.

**Query Parameters:**
- `keyword` (required) - Search keyword
- `limit` (optional, default: 10) - Maximum number of tasks to return

**Example:**
```bash
GET /api/tasks/search?keyword=authentication&limit=5
```

### 10. Get Task Statistics

**GET** `/api/tasks/stats`

Get statistics about tasks.

**Response:**
```json
{
  "data": {
    "totalTasks": 15,
    "statusDistribution": {
      "To Do": 5,
      "In Progress": 4,
      "In Review": 2,
      "Done": 3,
      "Cancelled": 1
    },
    "priorityDistribution": {
      "Low": 3,
      "Medium": 6,
      "High": 4,
      "Urgent": 2
    },
    "categoryDistribution": {
      "Development": 8,
      "Testing": 4,
      "Design": 2,
      "Documentation": 1
    },
    "averageCompletionPercentage": 42.5
  },
  "metadata": {
    "generatedAt": 1720533600000
  }
}
```

## Status Values

- `TODO` - To Do
- `IN_PROGRESS` - In Progress
- `IN_REVIEW` - In Review
- `DONE` - Done
- `CANCELLED` - Cancelled

## Priority Values

- `LOW` - Low
- `MEDIUM` - Medium
- `HIGH` - High
- `URGENT` - Urgent

## Error Responses

### 404 Not Found
```json
{
  "timestamp": "2025-07-09T14:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/tasks/non-existent-id"
}
```

### 400 Bad Request
```json
{
  "timestamp": "2025-07-09T14:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/tasks"
}
```

## Sample Data

The API comes pre-loaded with 15 sample tasks with realistic data including:
- Various statuses and priorities
- Different categories (Development, Testing, Design, etc.)
- Realistic task titles and descriptions
- Assigned users with names
- Due dates based on priority levels
- Completion percentages based on status

## Testing

The API includes comprehensive unit and integration tests:
- **TaskServiceTest**: Tests all service layer functionality
- **TaskControllerTest**: Tests all REST endpoints with MockMvc

Run tests with:
```bash
./mvnw test
```

## Swagger Documentation

The API is documented with OpenAPI 3.0 annotations and can be accessed via Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Features

- **Full CRUD Operations**: Create, Read, Update, Delete tasks
- **Advanced Filtering**: Filter by status, priority, assignee
- **Search Functionality**: Search across title, description, assignee, category
- **Statistics**: Get insights about task distribution and completion
- **Realistic Fake Data**: Pre-loaded with sample tasks for testing
- **Comprehensive Testing**: Unit and integration tests included
- **API Documentation**: Swagger/OpenAPI documentation
- **Consistent Response Format**: All responses use the same ApiResponse wrapper