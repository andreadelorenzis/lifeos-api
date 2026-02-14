package com.andreadelorenzis.productivityApp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andreadelorenzis.productivityApp.dto.GreetingDTO;
import com.andreadelorenzis.productivityApp.service.UserService;

@RestController
public class GreetingController {

  private final UserService userService;

  public GreetingController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/greeting")
  public GreetingDTO greeting(@RequestParam(defaultValue = "World") String name) {
    return userService.findUserByName(name);
  }
}