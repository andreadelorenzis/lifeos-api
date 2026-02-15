package com.andreadelorenzis.productivityApp.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class TaskDTO {

    @NotBlank(message = "{NotBlank.taskDTO.name}")
    private String name;

    private String description;

    @NotNull(message = "{NotNull.taskDTO.isHabit}")
    private Boolean isHabit;

    // For habits: frequency is required
    private Long frequencyId;

    // Optional: link task to a goal
    private Long goalId;

    private BigDecimal overflowQuantity;

    public TaskDTO() {}

    public TaskDTO(String name, String description, Boolean isHabit) {
        this.name = name;
        this.description = description;
        this.isHabit = isHabit;
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

    public Boolean getIsHabit() {
        return isHabit;
    }

    public void setIsHabit(Boolean isHabit) {
        this.isHabit = isHabit;
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

    public void setOverflowQuantity(BigDecimal overflowQuantity) {
        this.overflowQuantity = overflowQuantity;
    }
}
