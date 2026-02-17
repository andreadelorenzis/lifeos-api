package com.andreadelorenzis.productivityApp.repository;

import com.andreadelorenzis.productivityApp.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.deletedAt IS NULL ORDER BY t.createdAt DESC")
    List<Task> findAllActive();

    @Query("SELECT t FROM Task t WHERE t.goal.id = :goalId AND t.deletedAt IS NULL")
    List<Task> findByGoalId(@Param("goalId") Long goalId);

    @Query("SELECT t FROM Task t WHERE t.frequency.name != 'one-time' AND t.deletedAt IS NULL ORDER BY t.name ASC")
    List<Task> findAllHabits();

    @Query("SELECT t FROM Task t WHERE t.frequency.name = 'one-time' AND t.deletedAt IS NULL ORDER BY t.name ASC")
    List<Task> findAllOneTimeTasks();

    @Query("SELECT t FROM Task t WHERE t.frequency.id = :frequencyId AND t.deletedAt IS NULL")
    List<Task> findHabitsByFrequency(@Param("frequencyId") Long frequencyId);

    @Query("SELECT t FROM Task t WHERE t.completedAt IS NOT NULL AND t.deletedAt IS NULL")
    List<Task> findAllCompleted();

    @Query("SELECT t FROM Task t WHERE t.name LIKE %:name% AND t.deletedAt IS NULL")
    List<Task> searchByName(@Param("name") String name);
}
