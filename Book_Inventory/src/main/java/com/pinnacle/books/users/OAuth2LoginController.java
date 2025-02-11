package com.pinnacle.books.users;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pinnacle.books.users.service.OAuth2UserService;

@RestController
@RequestMapping("/auth")
public class OAuth2LoginController {
    private final OAuth2UserService oAuth2UserService;

    public OAuth2LoginController(OAuth2UserService oAuth2UserService) {
        this.oAuth2UserService = oAuth2UserService;
    }

    @GetMapping("/google-login")
    public String loginWithGoogle(OAuth2AuthenticationToken token) {
        oAuth2UserService.processOAuthPostLogin(token.getPrincipal());
        return "Google login successful!";
    }
}

