package com.andreadelorenzis.productivityApp.repository;

import com.andreadelorenzis.productivityApp.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {

}
