package org.sopt.routee.external.api.port;

import org.sopt.routee.external.api.type.OAuthProvider;

public interface OAuthRefreshTokenExchangePort {

	String exchangeForRefreshToken(OAuthProvider provider, String authorizationCode);
}
