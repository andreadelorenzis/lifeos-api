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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final FrequencyRepository frequencyRepository;
    private final GoalRepository goalRepository;

    public TaskService(TaskRepository taskRepository, FrequencyRepository frequencyRepository, GoalRepository goalRepository) {
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
        task.setIsHabit(dto.getIsHabit());

        // Validate and set frequency for habits
        if (Boolean.TRUE.equals(dto.getIsHabit())) {
            if (dto.getFrequencyId() == null) {
                throw new IllegalArgumentException("Frequency is required for habits");
            }
            Frequency frequency = frequencyRepository.findById(dto.getFrequencyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Frequency not found"));
            task.setFrequency(frequency);
        }

        // Validate and set goal if provided
        if (dto.getGoalId() != null) {
            Goal goal = goalRepository.findById(dto.getGoalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
            task.setGoal(goal);
        }

        if (dto.getOverflowQuantity() != null) {
            task.setOverflowQuantity(dto.getOverflowQuantity());
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
        task.setIsHabit(dto.getIsHabit());

        // Update frequency for habits
        if (Boolean.TRUE.equals(dto.getIsHabit())) {
            if (dto.getFrequencyId() == null) {
                throw new IllegalArgumentException("Frequency is required for habits");
            }
            Frequency frequency = frequencyRepository.findById(dto.getFrequencyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Frequency not found"));
            task.setFrequency(frequency);
        } else {
            task.setFrequency(null);
        }

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
        return toResponse(saved);
    }

    private void validateTaskInput(TaskDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Task name is required");
        }

        if (dto.getIsHabit() == null) {
            throw new IllegalArgumentException("isHabit field is required");
        }

        // Validate frequency only for habits
        if (Boolean.TRUE.equals(dto.getIsHabit()) && dto.getFrequencyId() != null) {
            frequencyRepository.findById(dto.getFrequencyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Frequency not found"));
        }
    }

    private TaskResponseDTO toResponse(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setIsHabit(task.getIsHabit());

        if (task.getFrequency() != null) {
            dto.setFrequencyId(task.getFrequency().getId());
            dto.setFrequencyName(task.getFrequency().getName());
        }

        if (task.getGoal() != null) {
            dto.setGoalId(task.getGoal().getId());
            dto.setGoalName(task.getGoal().getName());
        }

        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setOverflowQuantity(task.getOverflowQuantity());
        dto.setDeletedAt(task.getDeletedAt());

        return dto;
    }
}
