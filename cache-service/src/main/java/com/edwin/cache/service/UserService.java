package com.edwin.cache.service;

import com.edwin.cache.domain.User;
import com.edwin.cache.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUserById(Long id) {
        return userRepository.findUserById(id);
    }
}
