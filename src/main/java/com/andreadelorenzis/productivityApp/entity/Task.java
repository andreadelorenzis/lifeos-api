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
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;

@Entity
@Table(name = "tasks", indexes = {
        @Index(name = "idx_tasks_name", columnList = "name"),
        @Index(name = "idx_tasks_goal_id", columnList = "goal_id"),
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

    @ManyToOne
    @JoinColumn(name = "frequency_id", nullable = false)
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

    @Column(name = "quantity", precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "overflow_quantity", precision = 19, scale = 4)
    private BigDecimal overflowQuantity = BigDecimal.ZERO;

    @Column(name = "progress", precision = 19, scale = 4)
    private BigDecimal progress;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "urgent", nullable = false)
    private boolean urgent = false;

    @ElementCollection
    @CollectionTable(name = "task_week_days", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "weekday")
    private Set<Integer> weekDays = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "task_month_days", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "day_of_month")
    private Set<Integer> monthDays = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "task_year_days", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "day_of_year")
    private Set<Integer> yearDays = new HashSet<>();

    // Constructors
    public Task() {
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
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

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getQuantity() {
        return quantity;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Set<Integer> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(Set<Integer> weekDays) {
        this.weekDays = weekDays;
    }

    public Set<Integer> getMonthDays() {
        return monthDays;
    }

    public void setMonthDays(Set<Integer> monthDays) {
        this.monthDays = monthDays;
    }

    public Set<Integer> getYearDays() {
        return yearDays;
    }

    public void setYearDays(Set<Integer> yearDays) {
        this.yearDays = yearDays;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }
}
