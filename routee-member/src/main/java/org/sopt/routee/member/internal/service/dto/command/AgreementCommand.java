package org.sopt.routee.member.internal.service.dto.command;

public record AgreementCommand(
	boolean serviceTerms,
	boolean privacyPolicy,
	boolean locationServiceTerms,
	boolean over14,
	boolean marketingConsent
) {
}
