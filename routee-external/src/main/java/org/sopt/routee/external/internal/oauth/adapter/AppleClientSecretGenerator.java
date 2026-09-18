package org.sopt.routee.external.internal.oauth.adapter;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import org.sopt.routee.external.internal.oauth.config.OAuthRevokeProperty;
import org.sopt.routee.external.internal.oauth.exception.AppleClientSecretException;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Component
class AppleClientSecretGenerator {

	private static final Duration TOKEN_EXPIRY = Duration.ofMinutes(30);
	private static final Duration CACHE_TTL = Duration.ofMinutes(25);

	private final OAuthRevokeProperty property;
	private final AtomicReference<CachedSecret> cache = new AtomicReference<>();

	AppleClientSecretGenerator(OAuthRevokeProperty property) {
		this.property = property;
	}

	String generate() {
		CachedSecret cached = cache.get();
		if (cached != null && cached.expiresAt().isAfter(Instant.now())) {
			return cached.value();
		}

		String secret = sign();
		cache.set(new CachedSecret(secret, Instant.now().plus(CACHE_TTL)));
		return secret;
	}

	private String sign() {
		try {
			Instant now = Instant.now();
			SignedJWT jwt = new SignedJWT(
				new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(property.keyId()).build(),
				new JWTClaimsSet.Builder()
					.issuer(property.teamId())
					.issueTime(Date.from(now))
					.expirationTime(Date.from(now.plus(TOKEN_EXPIRY)))
					.audience(property.audience())
					.subject(property.clientId())
					.build()
			);
			jwt.sign(new ECDSASigner(parsePrivateKey(property.privateKey())));
			return jwt.serialize();
		} catch (JOSEException | GeneralSecurityException | IllegalArgumentException e) {
			throw new AppleClientSecretException(e);
		}
	}

	private ECPrivateKey parsePrivateKey(String privateKey) throws GeneralSecurityException {
		byte[] der = Base64.getDecoder().decode(privateKey);
		KeyFactory keyFactory = KeyFactory.getInstance("EC");
		return (ECPrivateKey)keyFactory.generatePrivate(new PKCS8EncodedKeySpec(der));
	}

	private record CachedSecret(String value, Instant expiresAt) {
	}
}
