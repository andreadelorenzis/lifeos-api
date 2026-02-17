package com.andreadelorenzis.productivityApp.repository;

import com.andreadelorenzis.productivityApp.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    Optional<Unit> findByCode(String code);
}
