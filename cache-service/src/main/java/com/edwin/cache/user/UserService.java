package com.edwin.cache.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUserById(Long id) {
        return userRepository.findUserById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findUserByOptional(Long id) {
        return userRepository.findUserByOptional(id);
    }
}
