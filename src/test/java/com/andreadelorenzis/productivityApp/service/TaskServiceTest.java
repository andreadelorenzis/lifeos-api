package com.andreadelorenzis.productivityApp.service;

import com.andreadelorenzis.productivityApp.dto.TaskProgressUpdateDTO;
import com.andreadelorenzis.productivityApp.dto.TaskResponseDTO;
import com.andreadelorenzis.productivityApp.entity.Goal;
import com.andreadelorenzis.productivityApp.entity.Task;
import com.andreadelorenzis.productivityApp.entity.Unit;
import com.andreadelorenzis.productivityApp.repository.FrequencyRepository;
import com.andreadelorenzis.productivityApp.repository.GoalRepository;
import com.andreadelorenzis.productivityApp.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private FrequencyRepository frequencyRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private TaskService taskService;

    private Task mockTask;
    private Goal mockGoal;

    @BeforeEach
    void setUp() {
        mockGoal = new Goal();
        mockGoal.setId(1L);
        mockGoal.setCurrentProgress(new BigDecimal("10"));

        Unit unit = new Unit();
        mockGoal.setUnit(unit);

        mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setGoal(mockGoal);
        mockTask.setProgress(new BigDecimal("0"));
        mockTask.setQuantity(new BigDecimal("5"));
    }

    @Test
    void testCompleteTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);
        when(goalRepository.save(any(Goal.class))).thenAnswer(i -> i.getArguments()[0]);

        TaskResponseDTO response = taskService.completeTask(1L);

        assertNotNull(response.getCompletedAt());
        assertEquals(new BigDecimal("5"), response.getProgress());

        // 10 + 5 = 15
        assertEquals(new BigDecimal("15"), mockGoal.getCurrentProgress());
        verify(taskRepository).save(mockTask);
        verify(goalRepository).save(mockGoal);
    }

    @Test
    void testUncompleteTask_Success() {
        mockTask.setCompletedAt(LocalDateTime.now());
        mockTask.setProgress(new BigDecimal("5"));
        mockGoal.setCurrentProgress(new BigDecimal("15"));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);
        when(goalRepository.save(any(Goal.class))).thenAnswer(i -> i.getArguments()[0]);

        TaskResponseDTO response = taskService.uncompleteTask(1L);

        assertNull(response.getCompletedAt());
        assertEquals(new BigDecimal("0"), response.getProgress());

        // 15 - 5 = 10
        assertEquals(new BigDecimal("10"), mockGoal.getCurrentProgress());
        verify(taskRepository).save(mockTask);
        verify(goalRepository).save(mockGoal);
    }

    @Test
    void testAddTaskProgress_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);
        when(goalRepository.save(any(Goal.class))).thenAnswer(i -> i.getArguments()[0]);

        TaskProgressUpdateDTO updateDTO = new TaskProgressUpdateDTO();
        updateDTO.setQuantity(new BigDecimal("2")); // add 2 progress

        TaskResponseDTO response = taskService.addTaskProgress(1L, updateDTO);

        assertEquals(new BigDecimal("2"), response.getProgress());
        assertNull(response.getCompletedAt()); // Not yet completed (2 < 5)

        // 10 + 2 = 12
        assertEquals(new BigDecimal("12"), mockGoal.getCurrentProgress());
        verify(taskRepository).save(mockTask);
        verify(goalRepository).save(mockGoal);
    }

    @Test
    void testAddTaskProgress_GoalUpdateAndCompletion() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);
        when(goalRepository.save(any(Goal.class))).thenAnswer(i -> i.getArguments()[0]);

        TaskProgressUpdateDTO updateDTO = new TaskProgressUpdateDTO();
        updateDTO.setQuantity(new BigDecimal("5")); // add 5 progress, equals required quantity

        TaskResponseDTO response = taskService.addTaskProgress(1L, updateDTO);

        assertEquals(new BigDecimal("5"), response.getProgress());
        assertNotNull(response.getCompletedAt()); // Completed (5 = 5)

        // 10 + 5 = 15
        assertEquals(new BigDecimal("15"), mockGoal.getCurrentProgress());
        verify(taskRepository).save(mockTask);
        verify(goalRepository).save(mockGoal);
    }
}
