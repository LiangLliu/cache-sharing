package com.edwin.cache.account;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return buildUser(account);
    }

    private User buildUser(Account account) {
        return User.builder()
                .id(account.getId())
                .username(account.getUsername())
                .password(account.getPassword())
                .build();
    }

    @Cacheable(cacheNames = USER_PEX, key = "'user.all'")
    public List<User> findAll() {
        return accountJpaRepository
                .findAll()
                .stream()
                .map(this::buildUser)
                .collect(Collectors.toList());

    }

    @Cacheable(cacheNames = USER_PEX, key = "'user.optional'")
    public Optional<User> findUserByOptional(Long id) {

        Optional<Account> optionalAccount = accountJpaRepository.findById(id);
        if (optionalAccount.isEmpty()) {
            return Optional.empty();
        }
        User user = buildUser(optionalAccount.get());
        return Optional.of(user);
    }
}
