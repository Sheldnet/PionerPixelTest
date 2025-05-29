package org.example.pionerpixeltest.service.iml;

import lombok.RequiredArgsConstructor;
import org.example.pionerpixeltest.dao.UserRepository;
import org.example.pionerpixeltest.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) {
        return userRepository
                .findByEmails_EmailIgnoreCaseOrPhones_Phone(login, login)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getId().toString())
                        .password(user.getPassword())
                        .authorities("ROLE_USER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
    }

    private UserDetails toPrincipal(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getId().toString())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();
    }


}
