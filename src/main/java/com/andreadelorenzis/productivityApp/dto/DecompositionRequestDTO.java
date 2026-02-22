package com.andreadelorenzis.productivityApp.dto;

import java.math.BigDecimal;

public class DecompositionRequestDTO {
    private Long goalId;
    private Long frequencyId;
    private BigDecimal quantity;

    public DecompositionRequestDTO() {
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public Long getFrequencyId() {
        return frequencyId;
    }

    public void setFrequencyId(Long frequencyId) {
        this.frequencyId = frequencyId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
