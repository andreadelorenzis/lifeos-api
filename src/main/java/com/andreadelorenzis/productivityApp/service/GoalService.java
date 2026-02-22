package com.andreadelorenzis.productivityApp.service;

import com.andreadelorenzis.productivityApp.dto.GoalDTO;
import com.andreadelorenzis.productivityApp.dto.GoalResponseDTO;
import com.andreadelorenzis.productivityApp.dto.DecompositionRequestDTO;
import com.andreadelorenzis.productivityApp.dto.DecompositionResponseDTO;
import com.andreadelorenzis.productivityApp.entity.Frequency;
import com.andreadelorenzis.productivityApp.entity.Goal;
import com.andreadelorenzis.productivityApp.entity.GoalStatus;
import com.andreadelorenzis.productivityApp.entity.Unit;
import com.andreadelorenzis.productivityApp.exception.ResourceNotFoundException;
import com.andreadelorenzis.productivityApp.repository.FrequencyRepository;
import com.andreadelorenzis.productivityApp.repository.GoalRepository;
import com.andreadelorenzis.productivityApp.repository.GoalStatusRepository;
import com.andreadelorenzis.productivityApp.repository.UnitRepository;
import com.andreadelorenzis.productivityApp.repository.TaskRepository;
import com.andreadelorenzis.productivityApp.entity.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalStatusRepository statusRepository;
    private final UnitRepository unitRepository;
    private final FrequencyRepository frequencyRepository;
    private final TaskRepository taskRepository;

    public GoalService(GoalRepository goalRepository, GoalStatusRepository statusRepository,
            UnitRepository unitRepository, FrequencyRepository frequencyRepository, TaskRepository taskRepository) {
        this.goalRepository = goalRepository;
        this.statusRepository = statusRepository;
        this.unitRepository = unitRepository;
        this.frequencyRepository = frequencyRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public GoalResponseDTO createGoal(GoalDTO dto) {
        validateBusinessRules(dto);

        Goal goal = new Goal();
        goal.setName(dto.getName());
        goal.setDescription(dto.getDescription());

        if (dto.getUnitCode() != null) {
            Unit unit = unitRepository.findByCode(dto.getUnitCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Unit not found: " + dto.getUnitCode()));
            goal.setUnit(unit);
        }

        goal.setTargetQuantity(dto.getTargetQuantity());
        goal.setCurrentProgress(dto.getCurrentProgress() == null ? BigDecimal.ZERO : dto.getCurrentProgress());
        goal.setDeadline(dto.getDeadline());
        goal.setDifficulty(dto.getDifficulty());
        goal.setImportance(dto.getImportance());
        goal.setReason(dto.getReason());
        goal.setReward(dto.getReward());
        goal.setPunishment(dto.getPunishment());

        GoalStatus status = determineStatusForCreate(dto);
        goal.setStatus(status);

        Goal saved = goalRepository.save(goal);
        return toResponse(saved);
    }

    public List<GoalResponseDTO> listGoals() {
        return goalRepository.findAll().stream()
                .filter(g -> g.getDeletedAt() == null)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public GoalResponseDTO getGoal(Long id) {
        Goal g = goalRepository.findById(id)
                .filter(goal -> goal.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        return toResponse(g);
    }

    @Transactional
    public GoalResponseDTO updateGoal(Long id, GoalDTO dto) {
        validateBusinessRules(dto);

        Goal g = goalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        if (g.getDeletedAt() != null)
            throw new ResourceNotFoundException("Goal not found");

        g.setName(dto.getName());
        g.setDescription(dto.getDescription());

        if (dto.getUnitCode() != null) {
            Unit unit = unitRepository.findByCode(dto.getUnitCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Unit not found: " + dto.getUnitCode()));
            g.setUnit(unit);
        }

        g.setTargetQuantity(dto.getTargetQuantity());
        if (dto.getCurrentProgress() != null)
            g.setCurrentProgress(dto.getCurrentProgress());
        g.setDeadline(dto.getDeadline());
        g.setDifficulty(dto.getDifficulty());
        g.setImportance(dto.getImportance());
        g.setReason(dto.getReason());
        g.setReward(dto.getReward());
        g.setPunishment(dto.getPunishment());

        if (dto.getStatusId() != null) {
            GoalStatus status = statusRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
            g.setStatus(status);
            if ("completed".equalsIgnoreCase(status.getName())) {
                g.setCompletedAt(LocalDateTime.now());
            } else if (g.getCompletedAt() != null) {
                g.setCompletedAt(null);
            }
        }

        Goal saved = goalRepository.save(g);
        return toResponse(saved);
    }

    @Transactional
    public void deleteGoal(Long id) {
        Goal g = goalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        if (g.getDeletedAt() != null)
            return;
        g.setDeletedAt(LocalDateTime.now());
        goalRepository.save(g);
    }

    private GoalStatus determineStatusForCreate(GoalDTO dto) {
        if (dto.getStatusId() != null) {
            return statusRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
        }
        return statusRepository.findByName("active")
                .orElseThrow(() -> new ResourceNotFoundException("Default status 'active' missing"));
    }

    private void validateBusinessRules(GoalDTO dto) {
        if (dto.getTargetQuantity() != null && dto.getTargetQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("target_quantity must be positive");
        }
        if (dto.getDeadline() != null && !dto.getDeadline().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("deadline must be in the future");
        }
        if (dto.getDifficulty() != null && (dto.getDifficulty() < 1 || dto.getDifficulty() > 5)) {
            throw new IllegalArgumentException("difficulty must be between 1 and 5");
        }
        if (dto.getImportance() != null && (dto.getImportance() < 1 || dto.getImportance() > 5)) {
            throw new IllegalArgumentException("importance must be between 1 and 5");
        }
    }

    public DecompositionResponseDTO decomposeGoal(DecompositionRequestDTO request) {
        Goal goal = goalRepository.findById(request.getGoalId())
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        Frequency frequency = frequencyRepository.findById(request.getFrequencyId())
                .orElseThrow(() -> new ResourceNotFoundException("Frequency not found"));

        DecompositionResponseDTO response = new DecompositionResponseDTO();

        if (goal.getCurrentProgress().compareTo(goal.getTargetQuantity()) >= 0 ||
                !goal.getStatus().getName().equalsIgnoreCase("Active")) {
            response.setRequiredQuantity(BigDecimal.ZERO);
            return response;
        }

        BigDecimal targetRemaining = goal.getTargetQuantity().subtract(goal.getCurrentProgress());
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(goal.getDeadline())) {
            response.setRequiredQuantity(BigDecimal.ZERO);
            return response;
        }

        long occurrencesRemaining = countOccurrences(frequency, now, goal.getDeadline());

        if (occurrencesRemaining <= 0) {
            throw new IllegalArgumentException("No occurrences left for this frequency before the deadline");
        }

        BigDecimal occurrences = BigDecimal.valueOf(occurrencesRemaining);
        BigDecimal requiredQuantity = targetRemaining.divide(occurrences, 0, RoundingMode.CEILING);
        response.setRequiredQuantity(requiredQuantity);

        if (request.getQuantity() != null && request.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalContribution = request.getQuantity().multiply(occurrences);
            boolean isFeasible = totalContribution.compareTo(targetRemaining) >= 0;
            response.setFeasible(isFeasible);

            BigDecimal neededOccurrences = targetRemaining.divide(request.getQuantity(), 0, RoundingMode.CEILING);
            LocalDateTime suggestedDeadline = dateOfNthOccurrence(frequency, now, neededOccurrences.longValue());
            response.setSuggestedDeadline(suggestedDeadline);
            if (totalContribution.abs().compareTo(targetRemaining) != 0) {
                BigDecimal shortfall = targetRemaining.subtract(totalContribution);
                response.setValueShortfall(shortfall);
            }
        }

        return response;
    }

    private long countOccurrences(Frequency frequency, LocalDateTime from, LocalDateTime to) {
        String freqName = frequency.getName().toLowerCase();

        LocalDateTime start = from.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime end = to.truncatedTo(ChronoUnit.DAYS);

        long daysBetween = ChronoUnit.DAYS.between(start, end);

        if (daysBetween <= 0)
            return 0;

        switch (freqName) {
            case "daily":
                return daysBetween;
            case "weekly":
                return daysBetween / 7;
            case "monthly":
                return ChronoUnit.MONTHS.between(start, end);
            case "yearly":
                return ChronoUnit.YEARS.between(start, end);
            case "one-time":
                return 1;
            default:
                throw new UnsupportedOperationException("Unsupported frequency: " + freqName);
        }
    }

    private LocalDateTime dateOfNthOccurrence(Frequency frequency, LocalDateTime from, long n) {
        String freqName = frequency.getName().toLowerCase();
        LocalDateTime start = from.truncatedTo(ChronoUnit.DAYS);

        switch (freqName) {
            case "daily":
                return start.plusDays(n);
            case "weekly":
                return start.plusWeeks(n);
            case "monthly":
                return start.plusMonths(n);
            case "yearly":
                return start.plusYears(n);
            case "one-time":
                return start.plusDays(n);
            default:
                throw new UnsupportedOperationException("Unsupported frequency: " + freqName);
        }
    }

    private GoalResponseDTO toResponse(Goal g) {
        GoalResponseDTO r = new GoalResponseDTO();
        r.setId(g.getId());
        r.setName(g.getName());
        r.setDescription(g.getDescription());

        if (g.getUnit() != null) {
            r.setUnitCode(g.getUnit().getCode());
            r.setUnitName(g.getUnit().getName());
        }

        r.setTargetQuantity(g.getTargetQuantity());
        r.setCurrentProgress(g.getCurrentProgress());
        r.setDeadline(g.getDeadline());
        r.setDifficulty(g.getDifficulty());
        r.setImportance(g.getImportance());
        r.setReason(g.getReason());
        r.setReward(g.getReward());
        r.setPunishment(g.getPunishment());
        if (g.getStatus() != null) {
            r.setStatusId(g.getStatus().getId());
            r.setStatusName(g.getStatus().getName());
        }
        r.setCreatedAt(g.getCreatedAt());
        r.setUpdatedAt(g.getUpdatedAt());
        r.setCompletedAt(g.getCompletedAt());

        r.setIdealProgress(calculateIdealProgress(g));
        return r;
    }

    private BigDecimal calculateIdealProgress(Goal goal) {
        List<Task> tasks = taskRepository.findByGoalId(goal.getId()).stream()
                .filter(t -> t.getDeletedAt() == null)
                .collect(Collectors.toList());

        if (tasks.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Base calculation on the first active task tracking this goal
        Task task = tasks.get(0);
        if (task.getFrequency() == null) {
            return BigDecimal.ZERO;
        }

        long occurrences = countOccurrences(task.getFrequency(), goal.getCreatedAt(), goal.getDeadline());
        if (occurrences <= 0) {
            return BigDecimal.ZERO;
        }

        LocalDateTime referenceDate = LocalDateTime.now();
        if (referenceDate.isAfter(goal.getDeadline())) {
            referenceDate = goal.getDeadline();
        }

        long pastOccurrences = countOccurrences(task.getFrequency(), goal.getCreatedAt(), referenceDate);
        if (pastOccurrences < 0)
            pastOccurrences = 0;

        // idealQuantity = goal.target / occurrences
        BigDecimal idealQuantity = goal.getTargetQuantity().divide(BigDecimal.valueOf(occurrences), 4,
                RoundingMode.HALF_UP);
        BigDecimal idealProgress = idealQuantity.multiply(BigDecimal.valueOf(pastOccurrences));

        if (idealProgress.compareTo(goal.getTargetQuantity()) > 0) {
            idealProgress = goal.getTargetQuantity();
        }

        return idealProgress;
    }

}
