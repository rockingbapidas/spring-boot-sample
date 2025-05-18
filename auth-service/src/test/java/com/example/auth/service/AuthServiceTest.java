package com.example.auth.service;

import com.example.auth.dto.AuthRequest;
import com.example.auth.dto.AuthResponse;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private AuthRequest authRequest;
    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        user = new User();
        user.setId("1");
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setEmail("testuser@example.com");
        user.setRole("USER");

        token = "test.jwt.token";
        
        // Lenient stubbing for JWT token generation
        lenient().when(jwtUtil.generateToken(anyString(), anyString())).thenReturn(token);
    }

    @Test
    void register_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        AuthResponse response = authService.register(authRequest);

        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getRole(), response.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UsernameExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(authRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(authRequest);

        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getRole(), response.getRole());
    }

    @Test
    void login_UserNotFound() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(authRequest));
    }
} 