package com.redislabs.edu.redi2read.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.redislabs.edu.redi2read.models.User;
import com.redislabs.edu.redi2read.repositories.UserRepository;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor

public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public Iterable<User> getAll() {
        return userRepository.findAll();
    }

    @GetMapping("/by-email")
    public User testEmail(@RequestParam String email) {

        User user = userRepository.findFirstByEmail(email);
        if(user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found with this email");

        return user;
    }
    
}
