package org.sopt.routee.auth.internal.repository.redis;

import java.time.Duration;

import org.sopt.routee.auth.internal.repository.TokenBlacklistRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenBlacklistRepositoryImpl implements TokenBlacklistRepository {

	private static final String BLACKLISTED_VALUE = "blacklisted";

	private final RedisTemplate<String, String> redisTemplate;

	public void blacklist(String token, Duration ttl) {
		redisTemplate.opsForValue().set(TokenHasher.hash(token), BLACKLISTED_VALUE, ttl);
	}

	public boolean isBlacklisted(String token) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(TokenHasher.hash(token)));
	}
}
