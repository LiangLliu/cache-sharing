package com.edwin.cache.account;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@CacheConfig(cacheNames = "user")
@Repository
@RequiredArgsConstructor
public class AccountRepository {
    private final AccountJpaRepository accountJpaRepository;

    public static final String USER_PEX = "USER_";

    @Cacheable(cacheNames = USER_PEX, key = "'user.id:'+#id")
    public User findUserById(Long id) {
        Optional<Account> optionalAccount = accountJpaRepository.findById(id);
        if (optionalAccount.isEmpty()) {
            return new User();
        }
        Account account = optionalAccount.get();
        return User.builder()
                .id(account.getId())
                .username(account.getUsername())
                .password(account.getPassword())
                .build();
    }
}
