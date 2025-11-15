package com.example.bankcards.service;

import com.example.bankcards.exception.UserAlreadyExistException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserValidatorService {
    private final UserRepository userRepository;
    private final String notExistsByIdExceptionMessage;
    private final String notExistsByUsernameExceptionMessage;
    private final String alreadyExistsByUsername;
    private final String alreadyExistsByEmail;

    public UserValidatorService(UserRepository userRepository,
                                @Value("${app.user.exception-message.not-found-by.Id}") String notExistsByIdExceptionMessage,
                                @Value("${app.user.exception-message.not-found-by.Username}") String notExistsByUsernameExceptionMessage,
                                @Value("${app.user.exception-message.already-exists.by-username}") String alreadyExistsByUsername,
                                @Value("${app.user.exception-message.already-exists.by-email}") String alreadyExistsByEmail) {
        this.userRepository = userRepository;
        this.notExistsByIdExceptionMessage = notExistsByIdExceptionMessage;
        this.notExistsByUsernameExceptionMessage = notExistsByUsernameExceptionMessage;
        this.alreadyExistsByUsername = alreadyExistsByUsername;
        this.alreadyExistsByEmail = alreadyExistsByEmail;
    }

    public void validateUserExistsById(UUID id) {
        if (!userRepository.existsById(id)){
            throw new UserNotFoundException(notExistsByIdExceptionMessage);
        }
    }
    public void validateUserExistByUsername(String username) {
        if (!userRepository.existsByUsername(username)){
            throw new UserNotFoundException(notExistsByUsernameExceptionMessage);
        }
    }
    public void ensureUserNotExistByUsername(String username) {
        if (userRepository.existsByUsername(username)){
            throw new UserAlreadyExistException(alreadyExistsByUsername);
        }
    }
    public void ensureUserNotExistByEmail(String email) {
        if (userRepository.existsByEmail(email)){
            throw new UserAlreadyExistException(alreadyExistsByEmail);
        }
    }
}
