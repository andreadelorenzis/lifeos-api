package com.andreadelorenzis.productivityApp.dto;

import java.math.BigDecimal;

public class TaskProgressUpdateDTO {

    private BigDecimal quantity;

    public TaskProgressUpdateDTO() {
    }

    public TaskProgressUpdateDTO(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
