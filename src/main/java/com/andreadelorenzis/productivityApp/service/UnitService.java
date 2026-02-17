package com.andreadelorenzis.productivityApp.service;

import com.andreadelorenzis.productivityApp.dto.UnitDTO;
import com.andreadelorenzis.productivityApp.repository.UnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnitService {

    private final UnitRepository unitRepository;

    public UnitService(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    public List<UnitDTO> getAllUnits() {
        return unitRepository.findAll().stream()
                .map(unit -> new UnitDTO(
                        unit.getId(),
                        unit.getCode(),
                        unit.getName(),
                        unit.getDescription()))
                .collect(Collectors.toList());
    }
}
