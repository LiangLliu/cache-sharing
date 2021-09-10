package com.edwin.cachedemo.service;

import com.edwin.cachedemo.domain.User;
import com.edwin.cachedemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUserById(Long id) {
        return (User) userRepository.findUserById(id);
    }
}
