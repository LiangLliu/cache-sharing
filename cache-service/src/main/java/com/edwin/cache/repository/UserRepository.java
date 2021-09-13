package com.edwin.cache.repository;

import com.edwin.cache.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

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
}
