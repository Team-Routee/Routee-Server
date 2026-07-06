package org.sopt.routee.auth.internal.repository.redis;

import java.time.Duration;

import org.sopt.routee.auth.internal.repository.RefreshTokenRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

	private static final String VALID_VALUE = "valid";

	private final RedisTemplate<String, String> redisTemplate;

	public void save(String token, Duration ttl) {
		redisTemplate.opsForValue().set(TokenHasher.hash(token), VALID_VALUE, ttl);
	}

	public boolean existsByToken(String token) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(TokenHasher.hash(token)));
	}

	public void deleteByToken(String token) {
		redisTemplate.delete(TokenHasher.hash(token));
	}
}
