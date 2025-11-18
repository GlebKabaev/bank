package com.example.bankcards.service;

import com.example.bankcards.exception.UserAlreadyExistException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserValidatorService userValidatorService;

    private static final String NOT_FOUND_BY_ID_MSG = "User not found by id";
    private static final String NOT_FOUND_BY_USERNAME_MSG = "User not found by username";
    private static final String ALREADY_EXISTS_USERNAME_MSG = "Username already exists";
    private static final String ALREADY_EXISTS_EMAIL_MSG = "Email already exists";

    @BeforeEach
    void setUp() {
        userValidatorService = new UserValidatorService(
                userRepository,
                NOT_FOUND_BY_ID_MSG,
                NOT_FOUND_BY_USERNAME_MSG,
                ALREADY_EXISTS_USERNAME_MSG,
                ALREADY_EXISTS_EMAIL_MSG
        );
    }

    @Test
    void validateUserExistsById_whenExists_shouldNotThrow() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> userValidatorService.validateUserExistsById(id));
        verify(userRepository).existsById(id);
    }

    @Test
    void validateUserExistsById_whenNotExists_shouldThrowUserNotFoundException() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(false);

        UserNotFoundException thrown = assertThrows(
                UserNotFoundException.class,
                () -> userValidatorService.validateUserExistsById(id)
        );

        assertEquals(NOT_FOUND_BY_ID_MSG, thrown.getMessage());
    }

    @Test
    void validateUserExistByUsername_whenExists_shouldNotThrow() {
        String username = "john_doe";
        when(userRepository.existsByUsername(username)).thenReturn(true);

        assertDoesNotThrow(() -> userValidatorService.validateUserExistByUsername(username));
    }

    @Test
    void validateUserExistByUsername_whenNotExists_shouldThrowUserNotFoundException() {
        String username = "unknown";
        when(userRepository.existsByUsername(username)).thenReturn(false);

        UserNotFoundException thrown = assertThrows(
                UserNotFoundException.class,
                () -> userValidatorService.validateUserExistByUsername(username)
        );

        assertEquals(NOT_FOUND_BY_USERNAME_MSG, thrown.getMessage());
    }

    @Test
    void ensureUserNotExistByUsername_whenNotExists_shouldNotThrow() {
        String username = "new_user";
        when(userRepository.existsByUsername(username)).thenReturn(false);

        assertDoesNotThrow(() -> userValidatorService.ensureUserNotExistByUsername(username));
    }

    @Test
    void ensureUserNotExistByUsername_whenExists_shouldThrowUserAlreadyExistException() {
        String username = "existing_user";
        when(userRepository.existsByUsername(username)).thenReturn(true);

        UserAlreadyExistException thrown = assertThrows(
                UserAlreadyExistException.class,
                () -> userValidatorService.ensureUserNotExistByUsername(username)
        );

        assertEquals(ALREADY_EXISTS_USERNAME_MSG, thrown.getMessage());
    }

    @Test
    void ensureUserNotExistByEmail_whenNotExists_shouldNotThrow() {
        String email = "new@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        assertDoesNotThrow(() -> userValidatorService.ensureUserNotExistByEmail(email));
    }

    @Test
    void ensureUserNotExistByEmail_whenExists_shouldThrowUserAlreadyExistException() {
        String email = "existing@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        UserAlreadyExistException thrown = assertThrows(
                UserAlreadyExistException.class,
                () -> userValidatorService.ensureUserNotExistByEmail(email)
        );

        assertEquals(ALREADY_EXISTS_EMAIL_MSG, thrown.getMessage());
    }
}