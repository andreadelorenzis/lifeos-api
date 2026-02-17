package com.andreadelorenzis.productivityApp.controller;

import com.andreadelorenzis.productivityApp.dto.GoalStatusDTO;
import com.andreadelorenzis.productivityApp.service.GoalStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/goal-statuses")
public class GoalStatusController {

    private final GoalStatusService goalStatusService;

    public GoalStatusController(GoalStatusService goalStatusService) {
        this.goalStatusService = goalStatusService;
    }

    @GetMapping
    public ResponseEntity<List<GoalStatusDTO>> getAllGoalStatuses() {
        return ResponseEntity.ok(goalStatusService.getAllGoalStatuses());
    }
}
