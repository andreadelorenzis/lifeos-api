package com.andreadelorenzis.productivityApp.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class TaskDTO {

    @NotBlank(message = "{NotBlank.taskDTO.name}")
    private String name;

    private String description;

    @NotNull(message = "Frequency is required")
    private Long frequencyId;

    private Long goalId;

    private BigDecimal quantity;

    private BigDecimal overflowQuantity;

    private BigDecimal progress;

    private List<Integer> selectedDays;

    private boolean urgent = false;

    public TaskDTO() {
    }

    public TaskDTO(String name, String description, Long frequencyId) {
        this.name = name;
        this.description = description;
        this.frequencyId = frequencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getFrequencyId() {
        return frequencyId;
    }

    public void setFrequencyId(Long frequencyId) {
        this.frequencyId = frequencyId;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public BigDecimal getOverflowQuantity() {
        return overflowQuantity;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setOverflowQuantity(BigDecimal overflowQuantity) {
        this.overflowQuantity = overflowQuantity;
    }

    public BigDecimal getProgress() {
        return progress;
    }

    public void setProgress(BigDecimal progress) {
        this.progress = progress;
    }

    public List<Integer> getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(List<Integer> selectedDays) {
        this.selectedDays = selectedDays;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }
}
