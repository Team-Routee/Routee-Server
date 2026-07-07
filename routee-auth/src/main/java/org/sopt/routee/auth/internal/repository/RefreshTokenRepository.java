package org.sopt.routee.auth.internal.repository;

import java.time.Duration;

public interface RefreshTokenRepository {

	void save(String token, Duration ttl);

	boolean deleteIfExists(String token);

	void deleteByToken(String token);

}