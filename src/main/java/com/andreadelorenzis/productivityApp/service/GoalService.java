package com.andreadelorenzis.productivityApp.service;

import com.andreadelorenzis.productivityApp.dto.GoalDTO;
import com.andreadelorenzis.productivityApp.dto.GoalResponseDTO;
import com.andreadelorenzis.productivityApp.entity.Goal;
import com.andreadelorenzis.productivityApp.entity.GoalStatus;
import com.andreadelorenzis.productivityApp.entity.Unit;
import com.andreadelorenzis.productivityApp.exception.ResourceNotFoundException;
import com.andreadelorenzis.productivityApp.repository.GoalRepository;
import com.andreadelorenzis.productivityApp.repository.GoalStatusRepository;
import com.andreadelorenzis.productivityApp.repository.UnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalStatusRepository statusRepository;
    private final UnitRepository unitRepository;

    public GoalService(GoalRepository goalRepository, GoalStatusRepository statusRepository,
            UnitRepository unitRepository) {
        this.goalRepository = goalRepository;
        this.statusRepository = statusRepository;
        this.unitRepository = unitRepository;
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
        return r;
    }

}
