package com.hibiznet.hr.auth.token;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {

    private final StringRedisTemplate stringRedisTemplate;

    public void save(String key, String refreshToken, Duration ttl) {
        stringRedisTemplate.opsForValue().set(key, refreshToken, ttl);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(key));
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }
}
