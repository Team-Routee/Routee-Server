package org.sopt.routee.auth.internal.repository;

import java.time.Duration;

public interface TokenBlacklistRepository {

	void blacklist(String token, Duration ttl);

	void blacklistHash(String tokenHash, Duration ttl);

	boolean isBlacklisted(String token);

}
