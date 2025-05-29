package org.example.pionerpixeltest.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pionerpixeltest.api.dto.AuthRequest;
import org.example.pionerpixeltest.api.dto.AuthResponse;
import org.example.pionerpixeltest.api.dto.RegisterRequest;
import org.example.pionerpixeltest.config.security.JwtTokenProvider;
import org.example.pionerpixeltest.service.TokenBlacklistService;
import org.example.pionerpixeltest.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider      jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getLogin(), req.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long userId = Long.parseLong(authentication.getName());

        String token = jwtTokenProvider.generateToken(userId);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            long ttl = jwtTokenProvider.timeToLive(token);
            tokenBlacklistService.blacklist(token, Duration.ofMillis(ttl));
        }

        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}