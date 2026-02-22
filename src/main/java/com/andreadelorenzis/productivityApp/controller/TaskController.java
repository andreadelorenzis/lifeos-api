package com.andreadelorenzis.productivityApp.controller;

import com.andreadelorenzis.productivityApp.dto.TaskDTO;
import com.andreadelorenzis.productivityApp.dto.TaskProgressUpdateDTO;
import com.andreadelorenzis.productivityApp.dto.TaskResponseDTO;
import com.andreadelorenzis.productivityApp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "API endpoints for managing tasks and recurring habits")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Create a new task or habit", description = "Creates a new task. If isHabit is true, frequencyId is required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input - missing required fields or invalid frequency for habit", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Goal or Frequency not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskDTO dto) {
        TaskResponseDTO created = taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "List all tasks", description = "Retrieve all active (non-deleted) tasks with optional filtering by type, goal, or search term")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Goal not found when filtering by goalId", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<TaskResponseDTO>> listTasks(
            @Parameter(description = "Filter by task type: 'habit' or 'one-time'") @RequestParam(required = false) String type,
            @Parameter(description = "Filter tasks by goal ID") @RequestParam(required = false) Long goalId,
            @Parameter(description = "Search tasks by name (partial match)") @RequestParam(required = false) String search) {

        List<TaskResponseDTO> tasks;

        if (search != null && !search.isBlank()) {
            tasks = taskService.searchTasks(search);
        } else if (goalId != null) {
            tasks = taskService.listTasksByGoal(goalId);
        } else if ("habit".equalsIgnoreCase(type)) {
            tasks = taskService.listHabits();
        } else if ("one-time".equalsIgnoreCase(type)) {
            tasks = taskService.listOneTimeTasks();
        } else {
            tasks = taskService.listTasks();
        }

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID", description = "Retrieve a single task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<TaskResponseDTO> getTask(
            @Parameter(description = "Task ID") @PathVariable Long id) {
        TaskResponseDTO task = taskService.getTask(id);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task (full replacement)", description = "Update an entire task with all fields")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Task, Goal, or Frequency not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<TaskResponseDTO> updateTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @RequestBody TaskDTO dto) {
        TaskResponseDTO updated = taskService.updateTask(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a task", description = "Update specific fields of a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Task, Goal, or Frequency not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<TaskResponseDTO> patchTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @RequestBody TaskDTO dto) {
        TaskResponseDTO updated = taskService.updateTask(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task (soft delete)", description = "Soft delete a task by marking it as deleted without removing from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID") @PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Mark a task as completed", description = "Set the completedAt timestamp for a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task marked as completed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<TaskResponseDTO> completeTask(
            @Parameter(description = "Task ID") @PathVariable Long id) {
        TaskResponseDTO completed = taskService.completeTask(id);
        return ResponseEntity.ok(completed);
    }

    @PostMapping("/{id}/uncomplete")
    @Operation(summary = "Mark a task as uncompleted", description = "Remove the completedAt timestamp for a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task marked as uncompleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<TaskResponseDTO> uncompleteTask(
            @Parameter(description = "Task ID") @PathVariable Long id) {
        TaskResponseDTO uncompleted = taskService.uncompleteTask(id);
        return ResponseEntity.ok(uncompleted);
    }

    @PostMapping("/{id}/progress")
    @Operation(summary = "Add time-based progress to a task", description = "Updates a task's progress based on a quantity in seconds, converting to the goal's unit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task progress updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or unit type", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<TaskResponseDTO> addProgress(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Valid @RequestBody TaskProgressUpdateDTO dto) {
        TaskResponseDTO updated = taskService.addTaskProgress(id, dto);
        return ResponseEntity.ok(updated);
    }
}
