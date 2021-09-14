package com.edwin.cache.user;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@CacheConfig(cacheNames = "user")
@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final UserJpaRepository userJpaRepository;

    public static final String USER_PEX = "USER_";

    @Cacheable(cacheNames = USER_PEX, key = "'user.id:'+#id")
    public User findUserById(Long id) {
        return userJpaRepository.findById(id).orElse(new User());
    }

    @Cacheable(cacheNames = USER_PEX, key = "'user.all'")
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }

    @Cacheable(cacheNames = USER_PEX, key = "'user.optional'")
    public Optional<User> findUserByOptional(Long id) {
        return userJpaRepository.findUserById(id);
    }
}
