package org.example.pionerpixeltest.service.iml;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.example.pionerpixeltest.service.TokenBlacklistService;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CaffeineTokenBlacklistService implements TokenBlacklistService {

    private final Cache<String, Boolean> cache =
            Caffeine.newBuilder().expireAfterWrite(Duration.ofHours(24)).build();

    @Override public boolean isBlacklisted(String token) {
        return cache.getIfPresent(token) != null;
    }

    @Override public void blacklist(String token, Duration ttl) {
        cache.put(token, Boolean.TRUE);
        cache.policy().expireVariably().ifPresent(p -> p.put(token, Boolean.TRUE, ttl));
    }
}
