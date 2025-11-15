package com.example.bankcards.service;

import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserValidatorService {
    private final UserRepository userRepository;
    private final String exceptionMessage;

    public UserValidatorService(UserRepository userRepository,
                                @Value("${app.user.exception-message.not-found.byId}") String exceptionMessage) {
        this.userRepository = userRepository;
        this.exceptionMessage = exceptionMessage;
    }

    public boolean isUserExistById(UUID id) {
        if (userRepository.existsById(id)){
            throw  new UserNotFoundException(exceptionMessage);
        }
        return true;
    }
}
