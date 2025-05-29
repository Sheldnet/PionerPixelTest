package org.example.pionerpixeltest.service;

import jakarta.validation.Valid;
import org.example.pionerpixeltest.api.dto.RegisterRequest;
import org.example.pionerpixeltest.api.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface UserService {
    Page<UserDto> search(UserFilter filter, Pageable pageable);

    UserDto getById(Long id);

    void updateEmails(Long userId, Set<String> newEmails, org.springframework.security.core.userdetails.UserDetails me);

    void updatePhones(Long userId, Set<String> newPhones, org.springframework.security.core.userdetails.UserDetails me);

    void register(@Valid RegisterRequest request);
}
