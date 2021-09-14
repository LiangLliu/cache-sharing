package com.edwin.cache.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public User findAccountById(Long id) {
        return accountRepository.findUserById(id);
    }

    public List<User> findAll() {
        return accountRepository.findAll();
    }

    public Optional<User> findUserByOptional(Long id) {
        return accountRepository.findUserByOptional(id);
    }
}
