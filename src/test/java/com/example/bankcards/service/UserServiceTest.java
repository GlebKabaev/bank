package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserValidatorService userValidatorService;

    @InjectMocks
    private UserService userService;

    private User user;
    private String username = "testuser";
    private String email = "test@example.com";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername(username);
        user.setEmail(email);
    }

    @Test
    void save_shouldSaveUser() {
        when(repository.save(any(User.class))).thenReturn(user);

        User result = userService.save(user);

        assertEquals(username, result.getUsername());
        verify(repository).save(user);
    }

    @Test
    void create_shouldCreateUserWhenValid() {
        doNothing().when(userValidatorService).ensureUserNotExistByEmail(email);
        doNothing().when(userValidatorService).ensureUserNotExistByUsername(username);
        when(repository.save(any(User.class))).thenReturn(user);

        User result = userService.create(user);

        assertEquals(username, result.getUsername());
        verify(userValidatorService).ensureUserNotExistByEmail(email);
        verify(userValidatorService).ensureUserNotExistByUsername(username);
        verify(repository).save(user);
    }

    @Test
    void getByUsername_shouldReturnUserWhenExists() {
        doNothing().when(userValidatorService).validateUserExistByUsername(username);
        when(repository.findByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.getByUsername(username);

        assertEquals(username, result.getUsername());
        verify(userValidatorService).validateUserExistByUsername(username);
        verify(repository).findByUsername(username);
    }

    @Test
    void userDetailsService_shouldReturnUserDetailsService() {
        UserDetailsService userDetailsService = userService.userDetailsService();

        assertNotNull(userDetailsService);
    }

    @Test
    void userDetailsService_shouldLoadUserByUsername() {
        doNothing().when(userValidatorService).validateUserExistByUsername(username);
        when(repository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetailsService userDetailsService = userService.userDetailsService();
        User result = (User) userDetailsService.loadUserByUsername(username);

        assertEquals(username, result.getUsername());
    }

    @Test
    void getCurrentUser_shouldReturnCurrentUser() {
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(username);

            doNothing().when(userValidatorService).validateUserExistByUsername(username);
            when(repository.findByUsername(username)).thenReturn(Optional.of(user));

            User result = userService.getCurrentUser();

            assertEquals(username, result.getUsername());
        }
    }
}