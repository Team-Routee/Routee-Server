package org.sopt.routee.external.api.port;

import org.sopt.routee.external.api.type.OAuthProvider;

public interface OidcVerifyPort {

	String extractSubject(OAuthProvider provider, String idToken);
}