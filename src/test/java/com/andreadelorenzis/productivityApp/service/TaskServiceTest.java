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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.andreadelorenzis.productivityApp.entity.Frequency;

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

    @Test
    void testGetTasksDueToday_Scheduling() {
        LocalDate today = LocalDate.now();
        int dayOfWeek = today.getDayOfWeek().getValue();
        int dayOfMonth = today.getDayOfMonth();
        int dayOfYear = today.getDayOfYear();

        Frequency freqOneTime = new Frequency();
        freqOneTime.setName("one-time");
        Frequency freqDaily = new Frequency();
        freqDaily.setName("daily");
        Frequency freqWeekly = new Frequency();
        freqWeekly.setName("weekly");
        Frequency freqMonthly = new Frequency();
        freqMonthly.setName("monthly");
        Frequency freqYearly = new Frequency();
        freqYearly.setName("yearly");

        Task tOneTime = new Task();
        tOneTime.setId(101L);
        tOneTime.setFrequency(freqOneTime);

        Task tDaily = new Task();
        tDaily.setId(102L);
        tDaily.setFrequency(freqDaily);

        Task tWeeklyDue = new Task();
        tWeeklyDue.setId(103L);
        tWeeklyDue.setFrequency(freqWeekly);
        tWeeklyDue.setWeekDays(Set.of(dayOfWeek));

        Task tWeeklyNotDue = new Task();
        tWeeklyNotDue.setId(104L);
        tWeeklyNotDue.setFrequency(freqWeekly);
        tWeeklyNotDue.setWeekDays(Set.of(dayOfWeek == 1 ? 2 : 1));

        Task tMonthlyDue = new Task();
        tMonthlyDue.setId(105L);
        tMonthlyDue.setFrequency(freqMonthly);
        tMonthlyDue.setMonthDays(Set.of(dayOfMonth));

        Task tYearlyDue = new Task();
        tYearlyDue.setId(106L);
        tYearlyDue.setFrequency(freqYearly);
        tYearlyDue.setYearDays(Set.of(dayOfYear));

        when(taskRepository.findAllActive()).thenReturn(List.of(
                tOneTime, tDaily, tWeeklyDue, tWeeklyNotDue, tMonthlyDue, tYearlyDue));

        List<TaskResponseDTO> results = taskService.getTasksDueToday(true);

        assertEquals(5, results.size());
        assertTrue(results.stream().anyMatch(dto -> dto.getId().equals(101L)));
        assertTrue(results.stream().anyMatch(dto -> dto.getId().equals(102L)));
        assertTrue(results.stream().anyMatch(dto -> dto.getId().equals(103L)));
        assertFalse(results.stream().anyMatch(dto -> dto.getId().equals(104L)));
        assertTrue(results.stream().anyMatch(dto -> dto.getId().equals(105L)));
        assertTrue(results.stream().anyMatch(dto -> dto.getId().equals(106L)));

        // Test includeOneTimeTasks = false
        results = taskService.getTasksDueToday(false);
        assertEquals(4, results.size());
        assertFalse(results.stream().anyMatch(dto -> dto.getId().equals(101L)));
    }

    @Test
    void testGetTasksDueToday_Ordering() {
        Frequency freqDaily = new Frequency();
        freqDaily.setName("daily");

        Goal goalHigh = new Goal();
        goalHigh.setId(1L);
        goalHigh.setImportance(5);
        Goal goalLow = new Goal();
        goalLow.setId(2L);
        goalLow.setImportance(1);

        Task t1 = new Task();
        t1.setId(1L);
        t1.setGoal(goalLow);
        t1.setUrgent(false);
        t1.setFrequency(freqDaily);
        t1.setCreatedAt(LocalDateTime.now());

        Task t2 = new Task();
        t2.setId(2L);
        t2.setGoal(goalHigh);
        t2.setUrgent(false);
        t2.setFrequency(freqDaily);
        t2.setCreatedAt(LocalDateTime.now());

        Task t3 = new Task();
        t3.setId(3L);
        t3.setGoal(goalHigh);
        t3.setUrgent(false);
        t3.setFrequency(freqDaily);
        t3.setCreatedAt(LocalDateTime.now().minusDays(1));

        Task t4 = new Task();
        t4.setId(4L);
        t4.setGoal(goalLow);
        t4.setUrgent(true);
        t4.setFrequency(freqDaily);
        t4.setCreatedAt(LocalDateTime.now().minusDays(1));

        Task t5 = new Task();
        t5.setId(5L);
        t5.setGoal(null);
        t5.setUrgent(false);
        t5.setFrequency(freqDaily);
        t5.setCreatedAt(LocalDateTime.now());

        Task t6 = new Task();
        t6.setId(6L);
        t6.setGoal(goalLow);
        t6.setUrgent(false);
        t6.setFrequency(freqDaily);
        t6.setCreatedAt(LocalDateTime.now().minusDays(1));

        Task t7 = new Task();
        t7.setId(7L);
        t7.setGoal(goalHigh);
        t7.setUrgent(true);
        t7.setFrequency(freqDaily);
        t7.setCreatedAt(LocalDateTime.now());

        // We put them in a random order
        when(taskRepository.findAllActive()).thenReturn(List.of(t1, t2, t3, t4, t5, t6, t7));

        List<TaskResponseDTO> results = taskService.getTasksDueToday(true);

        assertEquals(7, results.size());
        // Rank 1: Urgent, High importance -> t7
        assertEquals(7L, results.get(0).getId());
        // Rank 2: Urgent, Low importance -> t4
        assertEquals(4L, results.get(1).getId());
        // Rank 3: High importance, newer -> t2
        assertEquals(2L, results.get(2).getId());
        // Rank 4: High importance, older -> t3
        assertEquals(3L, results.get(3).getId());
        // Rank 5: Low importance, newer -> t1
        assertEquals(1L, results.get(4).getId());
        // Rank 6: Low importance, older -> t6
        assertEquals(6L, results.get(5).getId());
        // Rank 7: No goal -> t5
        assertEquals(5L, results.get(6).getId());
    }
}
