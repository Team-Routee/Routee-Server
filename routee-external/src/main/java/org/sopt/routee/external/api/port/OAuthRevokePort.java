package org.sopt.routee.external.api.port;

public interface OAuthRevokePort {

	void revoke(String refreshToken);
}
