package com.andreadelorenzis.productivityApp.controller;

import com.andreadelorenzis.productivityApp.dto.FrequencyDTO;
import com.andreadelorenzis.productivityApp.service.FrequencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/frequencies")
public class FrequencyController {

    private final FrequencyService frequencyService;

    public FrequencyController(FrequencyService frequencyService) {
        this.frequencyService = frequencyService;
    }

    @GetMapping
    public ResponseEntity<List<FrequencyDTO>> getAllFrequencies() {
        return ResponseEntity.ok(frequencyService.getAllFrequencies());
    }
}
