package com.andreadelorenzis.productivityApp.repository;

import com.andreadelorenzis.productivityApp.entity.Frequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FrequencyRepository extends JpaRepository<Frequency, Long> {

    Optional<Frequency> findByName(String name);
}
