package com.andreadelorenzis.productivityApp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_tasks_name", columnList = "name"),
    @Index(name = "idx_tasks_goal_id", columnList = "goal_id"),
    @Index(name = "idx_tasks_is_habit", columnList = "is_habit"),
    @Index(name = "idx_tasks_deleted_at", columnList = "deleted_at")
})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_habit", nullable = false)
    private Boolean isHabit = false;

    @ManyToOne
    @JoinColumn(name = "frequency_id")
    private Frequency frequency;

    @ManyToOne
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "overflow_quantity", precision = 19, scale = 4)
    private BigDecimal overflowQuantity = BigDecimal.ZERO;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Constructors
    public Task() {
    }

    public Task(String name, String description, Boolean isHabit) {
        this.name = name;
        this.description = description;
        this.isHabit = isHabit;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public BigDecimal getOverflowQuantity() {
        return overflowQuantity;
    }

    public void setOverflowQuantity(BigDecimal overflowQuantity) {
        this.overflowQuantity = overflowQuantity;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
