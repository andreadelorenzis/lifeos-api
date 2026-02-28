package com.andreadelorenzis.productivityApp.service;

import com.andreadelorenzis.productivityApp.dto.TaskDTO;
import com.andreadelorenzis.productivityApp.dto.TaskProgressUpdateDTO;
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
import java.time.LocalDate;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
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

        assignSelectedDays(task, dto);

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

        if (dto.getProgress() != null && dto.getQuantity() != null
                && dto.getQuantity().compareTo(BigDecimal.ZERO) == 1) {
            task.setProgress(dto.getProgress());
            checkAndHandleCompletion(task);
        }

        Task saved = taskRepository.save(task);

        // If task was marked completed in checkAndHandleCompletion, we need to update
        // goal
        if (saved.getCompletedAt() != null && saved.getGoal() != null) {
            updateGoalProgress(saved, true);
        }

        return toResponse(saved);
    }

    public List<TaskResponseDTO> listTasks() {
        return assignTaskOrderAndSort(taskRepository.findAllActive());
    }

    public List<TaskResponseDTO> getTasksDueToday(boolean includeOneTimeTasks) {
        LocalDate today = LocalDate.now();

        int dayOfWeek = today.getDayOfWeek().getValue();
        int dayOfMonth = today.getDayOfMonth();
        boolean isLastDayOfMonth = dayOfMonth == today.lengthOfMonth();

        int dayOfYear = today.getDayOfYear();
        boolean isLastDayOfYear = dayOfYear == today.lengthOfYear();

        List<Object[]> rows = taskRepository.findTasksDueToday(
                dayOfWeek,
                dayOfMonth,
                isLastDayOfMonth,
                dayOfYear,
                isLastDayOfYear,
                includeOneTimeTasks);
        List<Task> tasks = rows.stream()
                .map(row -> (Task) row[0])
                .collect(Collectors.toList());

        return assignTaskOrderAndSort(tasks);
    }

    public List<TaskResponseDTO> listTasksByGoal(Long goalId) {
        // Validate goal exists
        goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        return assignTaskOrderAndSort(taskRepository.findByGoalId(goalId));
    }

    public List<TaskResponseDTO> listHabits() {
        return assignTaskOrderAndSort(taskRepository.findAllHabits());
    }

    public List<TaskResponseDTO> listTasksByFrequency(Long frequencyId) {
        return assignTaskOrderAndSort(taskRepository.findTasksByFrequency(frequencyId));
    }

    public List<TaskResponseDTO> listOneTimeTasks() {
        return assignTaskOrderAndSort(taskRepository.findAllOneTimeTasks());
    }

    public List<TaskResponseDTO> searchTasks(String name) {
        return assignTaskOrderAndSort(taskRepository.searchByName(name));
    }

    private List<TaskResponseDTO> assignTaskOrderAndSort(List<Task> tasks) {
        // Base order (importance DESC, created ASC/DESC)
        tasks.sort((t1, t2) -> {
            Goal goal1 = t1.getGoal();
            Goal goal2 = t2.getGoal();

            Integer imp1 = goal1 != null ? goal1.getImportance() : -1;
            Integer imp2 = goal2 != null ? goal2.getImportance() : -1;
            int impCompare = imp2.compareTo(imp1); // DESC
            if (impCompare != 0)
                return impCompare;

            LocalDateTime cat1 = t1.getCreatedAt();
            LocalDateTime cat2 = t2.getCreatedAt();
            if (cat1 != null && cat2 != null) {
                return cat2.compareTo(cat1); // DESC
            }
            return 0;
        });

        List<TaskResponseDTO> dtos = tasks.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        // Assign taskOrder
        for (int i = 0; i < dtos.size(); i++) {
            dtos.get(i).setTaskOrder(i + 1);
        }

        // Final view order: urgency trumps the base rank
        dtos.sort((a, b) -> {
            if (a.isUrgent() && !b.isUrgent())
                return -1;
            if (!a.isUrgent() && b.isUrgent())
                return 1;
            return a.getTaskOrder().compareTo(b.getTaskOrder());
        });

        return dtos;
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

        assignSelectedDays(task, dto);

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

        boolean wasCompleted = task.getCompletedAt() != null;

        if (dto.getProgress() != null) {
            task.setProgress(dto.getProgress());
            checkAndHandleCompletion(task);
        }

        task.setUrgent(dto.isUrgent());

        boolean isCompleted = task.getCompletedAt() != null;

        Task saved = taskRepository.save(task);

        // Handle goal progress update if completion status changed
        if (saved.getGoal() != null) {
            if (!wasCompleted && isCompleted) {
                updateGoalProgress(saved, true);
            } else if (wasCompleted && !isCompleted) {
                updateGoalProgress(saved, false);
            }
        }

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
        if (task.getQuantity() != null) {
            task.setProgress(task.getQuantity());
        }
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
        if (task.getQuantity() != null) {
            task.setProgress(BigDecimal.ZERO);
        }
        Task saved = taskRepository.save(task);

        if (saved.getGoal() != null) {
            Goal goal = saved.getGoal();
            BigDecimal quantityToSubtract = saved.getQuantity() != null ? saved.getQuantity() : BigDecimal.ONE;
            goal.setCurrentProgress(goal.getCurrentProgress().subtract(quantityToSubtract));
            // Ensure progress doesn't go below zero
            if (goal.getCurrentProgress().compareTo(BigDecimal.ZERO) < 0) {
                goal.setCurrentProgress(BigDecimal.ZERO);
            }
            goalRepository.save(goal);
        }

        return toResponse(saved);
    }

    @Transactional
    public TaskResponseDTO setToUrgent(Long id, boolean urgent) {
        Task task = taskRepository.findById(id)
                .filter(t -> t.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setUrgent(urgent);

        Task saved = taskRepository.save(task);
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

    private void checkAndHandleCompletion(Task task) {
        if (task.getQuantity() != null && task.getProgress() != null) {
            if (task.getProgress().compareTo(task.getQuantity()) >= 0) {
                if (task.getCompletedAt() == null) {
                    task.setCompletedAt(LocalDateTime.now());
                }
            } else {
                if (task.getCompletedAt() != null) {
                    task.setCompletedAt(null);
                }
            }
        }
    }

    private void updateGoalProgress(Task task, boolean add) {
        if (task.getQuantity() != null) {
            Goal goal = task.getGoal();
            BigDecimal quantityToUpdate = task.getQuantity();

            if (add) {
                goal.setCurrentProgress(goal.getCurrentProgress().add(quantityToUpdate));
            } else {
                goal.setCurrentProgress(goal.getCurrentProgress().subtract(quantityToUpdate));
                if (goal.getCurrentProgress().compareTo(BigDecimal.ZERO) < 0) {
                    goal.setCurrentProgress(BigDecimal.ZERO);
                }
            }
            goalRepository.save(goal);
        }
    }

    @Transactional
    public TaskResponseDTO addTaskProgress(Long id, TaskProgressUpdateDTO dto) {
        Task task = taskRepository.findById(id)
                .filter(t -> t.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (task.getGoal() == null) {
            throw new IllegalArgumentException("Task is not linked to any goal");
        }

        Goal goal = task.getGoal();
        if (goal.getUnit() == null) {
            throw new IllegalArgumentException("Goal does not have a unit");
        }

        if (dto.getQuantity() == null) {
            throw new IllegalArgumentException("Quantity must be provided");
        }

        BigDecimal quantityToAdd = dto.getQuantity();

        BigDecimal currentProgress = task.getProgress() != null ? task.getProgress() : BigDecimal.ZERO;
        BigDecimal newProgress = currentProgress.add(quantityToAdd);

        if (newProgress.compareTo(BigDecimal.ZERO) < 0) {
            newProgress = BigDecimal.ZERO;
        }

        task.setProgress(newProgress);

        checkAndHandleCompletion(task);

        Task saved = taskRepository.save(task);

        BigDecimal newGoalProgress = goal.getCurrentProgress().add(quantityToAdd);
        if (newGoalProgress.compareTo(BigDecimal.ZERO) < 0) {
            newGoalProgress = BigDecimal.ZERO;
        }
        goal.setCurrentProgress(newGoalProgress);
        goalRepository.save(goal);

        return toResponse(saved);
    }

    private void assignSelectedDays(Task task, TaskDTO dto) {
        if (task.getWeekDays() != null)
            task.getWeekDays().clear();
        else
            task.setWeekDays(new HashSet<>());

        if (task.getMonthDays() != null)
            task.getMonthDays().clear();
        else
            task.setMonthDays(new HashSet<>());

        if (task.getYearDays() != null)
            task.getYearDays().clear();
        else
            task.setYearDays(new HashSet<>());

        if (dto.getSelectedDays() == null || task.getFrequency() == null) {
            return;
        }

        String freqName = task.getFrequency().getName().toLowerCase();
        if ("weekly".equals(freqName)) {
            task.getWeekDays().addAll(dto.getSelectedDays());
        } else if ("monthly".equals(freqName)) {
            task.getMonthDays().addAll(dto.getSelectedDays());
        } else if ("yearly".equals(freqName)) {
            task.getYearDays().addAll(dto.getSelectedDays());
        }
    }

    private TaskResponseDTO toResponse(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());

        if (task.getFrequency() != null) {
            dto.setFrequencyId(task.getFrequency().getId());
            dto.setFrequencyName(task.getFrequency().getName());

            String freqName = task.getFrequency().getName().toLowerCase();
            if ("weekly".equals(freqName)) {
                dto.setSelectedDays(
                        task.getWeekDays() != null ? new ArrayList<>(task.getWeekDays()) : new ArrayList<>());
            } else if ("monthly".equals(freqName)) {
                dto.setSelectedDays(
                        task.getMonthDays() != null ? new ArrayList<>(task.getMonthDays()) : new ArrayList<>());
            } else if ("yearly".equals(freqName)) {
                dto.setSelectedDays(
                        task.getYearDays() != null ? new ArrayList<>(task.getYearDays()) : new ArrayList<>());
            } else {
                dto.setSelectedDays(new ArrayList<>());
            }
        }

        if (task.getGoal() != null) {
            dto.setGoalId(task.getGoal().getId());
            dto.setGoalName(task.getGoal().getName());
            if (task.getGoal().getUnit() != null) {
                dto.setGoalUnitCode(task.getGoal().getUnit().getCode());
                dto.setGoalUnitName(task.getGoal().getUnit().getName());
            }
        }

        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setQuantity(task.getQuantity());
        dto.setOverflowQuantity(task.getOverflowQuantity());
        dto.setProgress(task.getProgress());
        dto.setDeletedAt(task.getDeletedAt());
        dto.setUrgent(task.isUrgent());

        return dto;
    }
}
