package org.sopt.routee.member.internal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "member.oauth-credential")
public record MemberOAuthCredentialProperty(
	String encryptionKey
) {
}
