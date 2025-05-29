package org.example.pionerpixeltest.service.iml;

import org.example.pionerpixeltest.service.TokenBlacklistService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Primary
public class NoopTokenBlacklistService implements TokenBlacklistService {
    @Override public boolean isBlacklisted(String token) { return false; }
    @Override public void blacklist(String token, Duration ttl) {}
}

