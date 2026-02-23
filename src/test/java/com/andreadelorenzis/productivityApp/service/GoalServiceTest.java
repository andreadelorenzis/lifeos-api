package com.andreadelorenzis.productivityApp.service;

import com.andreadelorenzis.productivityApp.dto.DecompositionRequestDTO;
import com.andreadelorenzis.productivityApp.dto.DecompositionResponseDTO;
import com.andreadelorenzis.productivityApp.entity.Frequency;
import com.andreadelorenzis.productivityApp.entity.Goal;
import com.andreadelorenzis.productivityApp.entity.GoalStatus;
import com.andreadelorenzis.productivityApp.repository.FrequencyRepository;
import com.andreadelorenzis.productivityApp.repository.GoalRepository;
import com.andreadelorenzis.productivityApp.repository.GoalStatusRepository;
import com.andreadelorenzis.productivityApp.repository.TaskRepository;
import com.andreadelorenzis.productivityApp.repository.UnitRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalStatusRepository statusRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private FrequencyRepository frequencyRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private GoalService goalService;

    private Goal mockGoal;
    private Frequency mockFrequency;
    private GoalStatus activeStatus;

    @BeforeEach
    void setUp() {
        mockGoal = new Goal();
        mockGoal.setId(1L);
        mockGoal.setTargetQuantity(new BigDecimal("100"));
        mockGoal.setCurrentProgress(new BigDecimal("0"));

        activeStatus = new GoalStatus();
        activeStatus.setId(1L);
        activeStatus.setName("Active");
        mockGoal.setStatus(activeStatus);

        mockFrequency = new Frequency();
        mockFrequency.setId(1L);
        mockFrequency.setName("daily");
    }

    @Test
    void testDecomposeGoal_NormalBehavior() {
        mockGoal.setDeadline(LocalDateTime.now().plusDays(10));

        DecompositionRequestDTO request = new DecompositionRequestDTO();
        request.setGoalId(1L);
        request.setFrequencyId(1L);
        request.setQuantity(new BigDecimal("10"));

        when(goalRepository.findById(1L)).thenReturn(Optional.of(mockGoal));
        when(frequencyRepository.findById(1L)).thenReturn(Optional.of(mockFrequency));

        DecompositionResponseDTO response = goalService.decomposeGoal(request);

        assertNotNull(response);
        // 100 / 10 days = 10
        assertEquals(new BigDecimal("10"), response.getRequiredQuantity());
        assertTrue(response.getFeasible());
        assertNotNull(response.getSuggestedDeadline());
    }

    @Test
    void testDecomposeGoal_AlreadyCompletedOrInactive() {
        mockGoal.setCurrentProgress(new BigDecimal("100")); // Completed
        mockGoal.setDeadline(LocalDateTime.now().plusDays(10));

        DecompositionRequestDTO request = new DecompositionRequestDTO();
        request.setGoalId(1L);
        request.setFrequencyId(1L);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(mockGoal));
        when(frequencyRepository.findById(1L)).thenReturn(Optional.of(mockFrequency));

        DecompositionResponseDTO response = goalService.decomposeGoal(request);

        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.getRequiredQuantity());
    }

    @Test
    void testDecomposeGoal_DeadlineInPast() {
        mockGoal.setDeadline(LocalDateTime.now().minusDays(1));

        DecompositionRequestDTO request = new DecompositionRequestDTO();
        request.setGoalId(1L);
        request.setFrequencyId(1L);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(mockGoal));
        when(frequencyRepository.findById(1L)).thenReturn(Optional.of(mockFrequency));

        DecompositionResponseDTO response = goalService.decomposeGoal(request);

        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.getRequiredQuantity());
    }

    @Test
    void testDecomposeGoal_NoOccurrencesLeft() {
        // 0 occurrences for weekly
        mockGoal.setDeadline(LocalDateTime.now().plusDays(3));

        DecompositionRequestDTO request = new DecompositionRequestDTO();
        request.setGoalId(1L);
        request.setFrequencyId(2L); // weekly

        when(goalRepository.findById(1L)).thenReturn(Optional.of(mockGoal));
        mockFrequency.setId(2L);
        mockFrequency.setName("weekly");
        when(frequencyRepository.findById(2L)).thenReturn(Optional.of(mockFrequency));

        assertThrows(IllegalArgumentException.class, () -> {
            goalService.decomposeGoal(request);
        });
    }

    @Test
    void testDecomposeGoal_WithContributionQuantityShortfall() {
        mockGoal.setDeadline(LocalDateTime.now().plusDays(5));

        DecompositionRequestDTO request = new DecompositionRequestDTO();
        request.setGoalId(1L);
        request.setFrequencyId(1L);
        request.setQuantity(new BigDecimal("10")); // 10 * 5 = 50 != 100

        when(goalRepository.findById(1L)).thenReturn(Optional.of(mockGoal));
        when(frequencyRepository.findById(1L)).thenReturn(Optional.of(mockFrequency));

        DecompositionResponseDTO response = goalService.decomposeGoal(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("20"), response.getRequiredQuantity()); // 100 / 5 = 20
        assertFalse(response.getFeasible());
        assertEquals(new BigDecimal("50"), response.getValueShortfall()); // 100 - 50 = 50
    }
}
