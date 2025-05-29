package org.example.pionerpixeltest.service;

import java.time.Duration;

public interface TokenBlacklistService {
    boolean isBlacklisted(String token);
    void blacklist(String token, Duration ttl);
}
