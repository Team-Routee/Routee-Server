package org.sopt.routee.auth.internal.jwt;

public enum TokenType {

	ACCESS, REFRESH;

	public String value() {
		return name().toLowerCase();
	}
}
