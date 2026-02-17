package com.andreadelorenzis.productivityApp.service;

import com.andreadelorenzis.productivityApp.dto.FrequencyDTO;
import com.andreadelorenzis.productivityApp.repository.FrequencyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FrequencyService {

    private final FrequencyRepository frequencyRepository;

    public FrequencyService(FrequencyRepository frequencyRepository) {
        this.frequencyRepository = frequencyRepository;
    }

    public List<FrequencyDTO> getAllFrequencies() {
        return frequencyRepository.findAll().stream()
                .map(frequency -> new FrequencyDTO(
                        frequency.getId(),
                        frequency.getName()))
                .collect(Collectors.toList());
    }
}
