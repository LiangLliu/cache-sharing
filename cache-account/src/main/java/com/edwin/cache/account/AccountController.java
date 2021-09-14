package com.edwin.cache.account;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{id}")
    public User findAccountById(@PathVariable Long id) {
        return accountService.findAccountById(id);
    }

    @GetMapping
    public List<User> findUserById() {
        return accountService.findAll();
    }

    @GetMapping("/{id}/optional")
    public Optional<User> findUserByOptional(@PathVariable Long id) {
        return accountService.findUserByOptional(id);
    }
}
