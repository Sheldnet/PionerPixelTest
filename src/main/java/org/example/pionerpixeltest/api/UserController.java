package org.example.pionerpixeltest.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pionerpixeltest.api.dto.UserDto;
import org.example.pionerpixeltest.service.UserFilter;
import org.example.pionerpixeltest.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping
    public Page<UserDto> search(
            @RequestParam Optional<LocalDate> dobAfter,
            @RequestParam Optional<String> name,
            @RequestParam Optional<String> email,
            @RequestParam Optional<String> phone,
            Pageable pageable) {

        UserFilter filter = new UserFilter(
                dobAfter.orElse(null),
                name.orElse(null),
                email.orElse(null),
                phone.orElse(null));

        return userService.search(filter, pageable);
    }


    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        return userService.getById(id);
    }


    @PutMapping("/{id}/emails")
    public ResponseEntity<Void> setEmails(
            @PathVariable Long id,
            @RequestBody @Valid Set<String> emails,
            @AuthenticationPrincipal UserDetails me) {

        userService.updateEmails(id, emails, me);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}/phones")
    public ResponseEntity<Void> setPhones(
            @PathVariable Long id,
            @RequestBody @Valid Set<String> phones,
            @AuthenticationPrincipal UserDetails me) {

        userService.updatePhones(id, phones, me);
        return ResponseEntity.noContent().build();
    }


}
