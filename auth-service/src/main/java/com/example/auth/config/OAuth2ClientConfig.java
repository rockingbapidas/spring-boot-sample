package com.example.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

@Configuration
public class OAuth2ClientConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(
            clientRegistration()
        );
    }

    private ClientRegistration clientRegistration() {
        return ClientRegistration.withRegistrationId("auth-service")
            .clientId("client")
            .clientSecret("{noop}secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope("openid", "profile", "email", "read", "write")
            .authorizationUri("http://localhost:8083/oauth2/authorize")
            .tokenUri("http://localhost:8083/oauth2/token")
            .userInfoUri("http://localhost:8083/userinfo")
            .userNameAttributeName(IdTokenClaimNames.SUB)
            .jwkSetUri("http://localhost:8083/oauth2/jwks")
            .clientName("Auth Service")
            .build();
    }
} 