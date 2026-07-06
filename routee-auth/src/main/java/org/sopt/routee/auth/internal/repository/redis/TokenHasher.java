package org.sopt.routee.auth.internal.repository.redis;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class TokenHasher {

	private static final String HASH_ALGORITHM = "SHA-256";

	static String hash(String token) {
		try {
			MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
			byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hashed);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(
				"%s algorithm is not available".formatted(HASH_ALGORITHM),
				e
			);
		}
	}
}
