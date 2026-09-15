package org.sopt.routee.external.api.port;

import org.sopt.routee.external.api.type.OAuthProvider;

public interface OAuthRevokePort {

	void revoke(OAuthProvider provider, String authorizationCode);
}
