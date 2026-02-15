package com.andreadelorenzis.productivityApp.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GoalDTO {

    @NotBlank(message = "{NotBlank.goalDTO.name}")
    private String name;

    private String description;

    private String unitOfMeasure;

    @NotNull(message = "{NotNull.goalDTO.targetQuantity}")
    @DecimalMin(value = "0.0001", message = "{DecimalMin.goalDTO.targetQuantity}")
    private BigDecimal targetQuantity;

    private BigDecimal currentProgress;

    @NotNull(message = "{NotNull.goalDTO.deadline}")
    @Future(message = "{Future.goalDTO.deadline}")
    private LocalDateTime deadline;

    @Min(value = 1, message = "{Min.goalDTO.difficulty}")
    @Max(value = 5, message = "{Max.goalDTO.difficulty}")
    private Integer difficulty;

    @Min(value = 1, message = "{Min.goalDTO.difficulty}")
    @Max(value = 5, message = "{Max.goalDTO.difficulty}")
    private Integer importance;

    private String reason;

    private String reward;

    private String punishment;

    private Long statusId;

    public GoalDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public BigDecimal getTargetQuantity() { return targetQuantity; }
    public void setTargetQuantity(BigDecimal targetQuantity) { this.targetQuantity = targetQuantity; }

    public BigDecimal getCurrentProgress() { return currentProgress; }
    public void setCurrentProgress(BigDecimal currentProgress) { this.currentProgress = currentProgress; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public Integer getDifficulty() { return difficulty; }
    public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }

    public Integer getImportance() { return importance; }
    public void setImportance(Integer importance) { this.importance = importance; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getReward() { return reward; }
    public void setReward(String reward) { this.reward = reward; }

    public String getPunishment() { return punishment; }
    public void setPunishment(String punishment) { this.punishment = punishment; }

    public Long getStatusId() { return statusId; }
    public void setStatusId(Long statusId) { this.statusId = statusId; }

}
