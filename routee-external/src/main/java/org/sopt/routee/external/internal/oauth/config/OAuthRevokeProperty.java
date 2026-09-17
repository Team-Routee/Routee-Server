package org.sopt.routee.external.internal.oauth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Apple OAuth 연동 해제(revoke) 설정.
 * <p>
 * client secret은 정적 문자열이 아니라 {@code teamId}/{@code keyId}/{@code privateKey}로
 * client secret JWT(ES256)를 직접 서명해서 사용한다.
 */
@ConfigurationProperties(prefix = "oauth.providers.apple")
public record OAuthRevokeProperty(
	String tokenUri,
	String revokeUri,
	String audience,
	String clientId,
	String teamId,
	String keyId,
	String privateKey
) {
}
