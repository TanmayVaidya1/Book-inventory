package com.pinnacle.books.users.service;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.pinnacle.books.users.Users;
import com.pinnacle.books.users.UsersRepository;

@Service
public class OAuth2UserService {
    private final UsersRepository usersRepository;

    public OAuth2UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public Users processOAuthPostLogin(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        return usersRepository.findByEmail(email)
            .orElseGet(() -> {
                Users newUser = new Users();
                newUser.setEmail(email);
                newUser.setName(oAuth2User.getAttribute("name"));
                newUser.setRole("USER");
                return usersRepository.save(newUser);
            });
    }
}
