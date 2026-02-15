package com.andreadelorenzis.productivityApp.repository;

import com.andreadelorenzis.productivityApp.entity.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalStatusRepository extends JpaRepository<GoalStatus, Long> {

	java.util.Optional<GoalStatus> findByName(String name);

}
