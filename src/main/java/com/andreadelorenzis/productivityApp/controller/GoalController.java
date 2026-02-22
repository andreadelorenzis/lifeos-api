package com.andreadelorenzis.productivityApp.controller;

import com.andreadelorenzis.productivityApp.dto.DecompositionRequestDTO;
import com.andreadelorenzis.productivityApp.dto.DecompositionResponseDTO;
import com.andreadelorenzis.productivityApp.dto.GoalDTO;
import com.andreadelorenzis.productivityApp.dto.GoalResponseDTO;
import com.andreadelorenzis.productivityApp.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@Tag(name = "Goals", description = "API endpoints for managing goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    @Operation(summary = "Create a new goal", description = "Creates a new goal with target quantity and deadline")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Goal created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GoalResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input - missing required fields or invalid values", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Status not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<GoalResponseDTO> create(@Valid @RequestBody GoalDTO dto) {
        GoalResponseDTO created = goalService.createGoal(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "List all goals", description = "Retrieve all active (non-deleted) goals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved goals", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GoalResponseDTO.class)))
    })
    public ResponseEntity<List<GoalResponseDTO>> list() {
        return ResponseEntity.ok(goalService.listGoals());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a goal by ID", description = "Retrieve a single goal by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GoalResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Goal not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<GoalResponseDTO> get(
            @Parameter(description = "Goal ID") @PathVariable Long id) {
        GoalResponseDTO r = goalService.getGoal(id);
        return ResponseEntity.ok(r);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a goal (full replacement)", description = "Update an entire goal with all fields")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GoalResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Goal or Status not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<GoalResponseDTO> update(
            @Parameter(description = "Goal ID") @PathVariable Long id,
            @Valid @RequestBody GoalDTO dto) {
        GoalResponseDTO updated = goalService.updateGoal(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a goal", description = "Update specific fields of a goal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GoalResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Goal or Status not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<GoalResponseDTO> patch(
            @Parameter(description = "Goal ID") @PathVariable Long id,
            @Valid @RequestBody GoalDTO dto) {
        GoalResponseDTO updated = goalService.updateGoal(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a goal (soft delete)", description = "Soft delete a goal by marking it as deleted without removing from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Goal deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Goal not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Goal ID") @PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/decompose")
    @Operation(summary = "Decompose a goal", description = "Calculate mathematical breakdown of a goal based on frequency to suggest quantities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Decomposition calculated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DecompositionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or frequency configuration", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Goal or Frequency not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<DecompositionResponseDTO> decomposeGoal(@Valid @RequestBody DecompositionRequestDTO request) {
        DecompositionResponseDTO response = goalService.decomposeGoal(request);
        return ResponseEntity.ok(response);
    }

}
