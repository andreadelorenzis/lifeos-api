package com.andreadelorenzis.productivityApp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DecompositionResponseDTO {
    private BigDecimal requiredQuantity;
    private Boolean feasible;
    private LocalDateTime suggestedDeadline;
    private BigDecimal valueShortfall;

    public DecompositionResponseDTO() {
    }

    public BigDecimal getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(BigDecimal requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    public Boolean getFeasible() {
        return feasible;
    }

    public void setFeasible(Boolean feasible) {
        this.feasible = feasible;
    }

    public LocalDateTime getSuggestedDeadline() {
        return suggestedDeadline;
    }

    public void setSuggestedDeadline(LocalDateTime suggestedDeadline) {
        this.suggestedDeadline = suggestedDeadline;
    }

    public BigDecimal getValueShortfall() {
        return valueShortfall;
    }

    public void setValueShortfall(BigDecimal valueShortfall) {
        this.valueShortfall = valueShortfall;
    }
}
