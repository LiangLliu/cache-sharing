package com.edwin.cachedemo.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public User findAccountById(Long id) {
        return accountRepository.findUserById(id);
    }
}
