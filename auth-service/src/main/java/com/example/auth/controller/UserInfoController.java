package com.example.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/userinfo")
public class UserInfoController {

    @GetMapping
    public Map<String, Object> userInfo(@AuthenticationPrincipal OidcUser principal) {
        return Map.of(
            "sub", principal.getSubject(),
            "name", principal.getName(),
            "email", principal.getEmail(),
            "email_verified", principal.getEmailVerified(),
            "picture", principal.getPicture()
        );
    }
} 