package com.andreadelorenzis.productivityApp.service;

import com.andreadelorenzis.productivityApp.dto.GoalStatusDTO;
import com.andreadelorenzis.productivityApp.repository.GoalStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalStatusService {

    private final GoalStatusRepository goalStatusRepository;

    public GoalStatusService(GoalStatusRepository goalStatusRepository) {
        this.goalStatusRepository = goalStatusRepository;
    }

    public List<GoalStatusDTO> getAllGoalStatuses() {
        return goalStatusRepository.findAll().stream()
                .map(status -> new GoalStatusDTO(
                        status.getId(),
                        status.getName()))
                .collect(Collectors.toList());
    }
}
