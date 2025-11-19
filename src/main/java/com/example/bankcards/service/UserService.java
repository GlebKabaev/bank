package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserValidatorService userValidatorService;

    public User save(User user) {
        return repository.save(user);
    }


    public User create(User user) {
        userValidatorService.ensureUserNotExistByEmail(user.getEmail());
        userValidatorService.ensureUserNotExistByUsername(user.getUsername());
        return save(user);
    }


    public User getByUsername(String username) {
        userValidatorService.validateUserExistByUsername(username);
        return repository.findByUsername(username).get();

    }


    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

}