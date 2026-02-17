package com.andreadelorenzis.productivityApp.service;

import com.andreadelorenzis.productivityApp.dto.TaskDTO;
import com.andreadelorenzis.productivityApp.dto.TaskResponseDTO;
import com.andreadelorenzis.productivityApp.entity.Task;
import com.andreadelorenzis.productivityApp.entity.Frequency;
import com.andreadelorenzis.productivityApp.entity.Goal;
import com.andreadelorenzis.productivityApp.exception.ResourceNotFoundException;
import com.andreadelorenzis.productivityApp.repository.TaskRepository;
import com.andreadelorenzis.productivityApp.repository.FrequencyRepository;
import com.andreadelorenzis.productivityApp.repository.GoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final FrequencyRepository frequencyRepository;
    private final GoalRepository goalRepository;

    public TaskService(TaskRepository taskRepository, FrequencyRepository frequencyRepository,
            GoalRepository goalRepository) {
        this.taskRepository = taskRepository;
        this.frequencyRepository = frequencyRepository;
        this.goalRepository = goalRepository;
    }

    @Transactional
    public TaskResponseDTO createTask(TaskDTO dto) {
        validateTaskInput(dto);

        Task task = new Task();
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());

        // Validate and set frequency
        Frequency frequency = frequencyRepository.findById(dto.getFrequencyId())
                .orElseThrow(() -> new ResourceNotFoundException("Frequency not found"));
        task.setFrequency(frequency);

        // Validate and set goal if provided
        if (dto.getGoalId() != null) {
            Goal goal = goalRepository.findById(dto.getGoalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
            task.setGoal(goal);
        }

        if (dto.getOverflowQuantity() != null) {
            task.setOverflowQuantity(dto.getOverflowQuantity());
        }

        if (dto.getQuantity() != null) {
            task.setQuantity(dto.getQuantity());
        }

        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    public List<TaskResponseDTO> listTasks() {
        return taskRepository.findAllActive().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> listTasksByGoal(Long goalId) {
        // Validate goal exists
        goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        return taskRepository.findByGoalId(goalId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> listHabits() {
        return taskRepository.findAllHabits().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> listOneTimeTasks() {
        return taskRepository.findAllOneTimeTasks().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> searchTasks(String name) {
        return taskRepository.searchByName(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TaskResponseDTO getTask(Long id) {
        Task task = taskRepository.findById(id)
                .filter(t -> t.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return toResponse(task);
    }

    @Transactional
    public TaskResponseDTO updateTask(Long id, TaskDTO dto) {
        validateTaskInput(dto);

        Task task = taskRepository.findById(id)
                .filter(t -> t.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setName(dto.getName());
        task.setDescription(dto.getDescription());

        // Update frequency
        Frequency frequency = frequencyRepository.findById(dto.getFrequencyId())
                .orElseThrow(() -> new ResourceNotFoundException("Frequency not found"));
        task.setFrequency(frequency);

        // Update goal if provided
        if (dto.getGoalId() != null) {
            Goal goal = goalRepository.findById(dto.getGoalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
            task.setGoal(goal);
        } else {
            task.setGoal(null);
        }

        if (dto.getOverflowQuantity() != null) {
            task.setOverflowQuantity(dto.getOverflowQuantity());
        }

        if (dto.getQuantity() != null) {
            task.setQuantity(dto.getQuantity());
        }

        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .filter(t -> t.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Soft delete
        task.setDeletedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Transactional
    public TaskResponseDTO completeTask(Long id) {
        Task task = taskRepository.findById(id)
                .filter(t -> t.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setCompletedAt(LocalDateTime.now());
        Task saved = taskRepository.save(task);

        if (saved.getGoal() != null) {
            Goal goal = saved.getGoal();
            BigDecimal quantityToAdd = saved.getQuantity() != null ? saved.getQuantity() : BigDecimal.ONE;
            goal.setCurrentProgress(goal.getCurrentProgress().add(quantityToAdd));
            goalRepository.save(goal);
        }

        return toResponse(saved);
    }

    @Transactional
    public TaskResponseDTO uncompleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .filter(t -> t.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setCompletedAt(null);
        Task saved = taskRepository.save(task);

        if (saved.getGoal() != null) {
            Goal goal = saved.getGoal();
            BigDecimal quantityToSubtract = saved.getQuantity() != null ? saved.getQuantity() : BigDecimal.ONE;
            goal.setCurrentProgress(goal.getCurrentProgress().subtract(quantityToSubtract));
            // Ensure progress doesn't go below zero (optional, but good practice)
            if (goal.getCurrentProgress().compareTo(BigDecimal.ZERO) < 0) {
                goal.setCurrentProgress(BigDecimal.ZERO);
            }
            goalRepository.save(goal);
        }

        return toResponse(saved);
    }

    private void validateTaskInput(TaskDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Task name is required");
        }

        if (dto.getFrequencyId() == null) {
            throw new IllegalArgumentException("Frequency is required");
        }

        frequencyRepository.findById(dto.getFrequencyId())
                .orElseThrow(() -> new ResourceNotFoundException("Frequency not found"));
    }

    private TaskResponseDTO toResponse(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());

        if (task.getFrequency() != null) {
            dto.setFrequencyId(task.getFrequency().getId());
            dto.setFrequencyName(task.getFrequency().getName());
        }

        if (task.getGoal() != null) {
            dto.setGoalId(task.getGoal().getId());
            dto.setGoalName(task.getGoal().getName());
            if (task.getGoal().getUnit() != null) {
                dto.setGoalUnit(task.getGoal().getUnit().getCode());
            }
        }

        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setQuantity(task.getQuantity());
        dto.setOverflowQuantity(task.getOverflowQuantity());
        dto.setDeletedAt(task.getDeletedAt());

        return dto;
    }
}
