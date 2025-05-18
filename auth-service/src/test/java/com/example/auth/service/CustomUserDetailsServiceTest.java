package com.example.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsServiceTest {
    private CustomUserDetailsService service;
    private PasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new BCryptPasswordEncoder();
        service = new CustomUserDetailsService(encoder);
    }

    @Test
    void loadUserByUsername_userExists_returnsUserDetails() {
        UserDetails user = service.loadUserByUsername("user@example.com");
        assertNotNull(user);
        assertEquals("user@example.com", user.getUsername());
        assertTrue(encoder.matches("password", user.getPassword()));
    }

    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("notfound@example.com");
        });
    }
} 