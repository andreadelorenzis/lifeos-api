package com.andreadelorenzis.productivityApp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GoalResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String unitOfMeasure;
    private BigDecimal targetQuantity;
    private BigDecimal currentProgress;
    private LocalDateTime deadline;
    private Integer difficulty;
    private Integer importance;
    private String reason;
    private String reward;
    private String punishment;
    private Long statusId;
    private String statusName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    public GoalResponseDTO() {}

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

}
