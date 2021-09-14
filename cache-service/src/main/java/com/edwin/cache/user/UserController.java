package com.edwin.cache.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public User findUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @GetMapping
    public List<User> findUserById() {
        return userService.findAll();
    }

    @GetMapping("/{id}/optional")
    public Optional<User> findUserByOptional(@PathVariable Long id) {
        return userService.findUserByOptional(id);
    }
}
