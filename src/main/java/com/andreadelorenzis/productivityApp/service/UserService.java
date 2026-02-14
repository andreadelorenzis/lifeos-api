package com.andreadelorenzis.productivityApp.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.andreadelorenzis.productivityApp.dto.GreetingDTO;
import com.andreadelorenzis.productivityApp.entity.User;
import com.andreadelorenzis.productivityApp.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    public GreetingDTO findUserByName(String name) {
        User user = userRepository.findByname(name);
        if (user != null) {
            return new GreetingDTO(counter.incrementAndGet(), template.formatted(name));
        } else {
            return new GreetingDTO(0, "Utente non trovato");
        }
    }
}

