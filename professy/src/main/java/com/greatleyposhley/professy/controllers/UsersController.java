package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.Users;
import com.greatleyposhley.professy.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/by-email")
    public ResponseEntity<Users> getUserByEmail(@RequestParam String email) {
        Users user = usersService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }


    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = usersService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}