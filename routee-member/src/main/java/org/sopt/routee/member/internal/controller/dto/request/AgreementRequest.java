package org.sopt.routee.member.internal.controller.dto.request;

import org.sopt.routee.member.internal.service.dto.command.AgreementCommand;

import jakarta.validation.constraints.NotNull;

public record AgreementRequest(
	@NotNull	// 서비스 이용 약관
	Boolean serviceTerms,
	@NotNull	// 개인정보 처리 방침
	Boolean privacyPolicy,
	@NotNull	// 위치기반 서비스 이용약관
	Boolean locationServiceTerms,
	@NotNull	// 만 14세 이상 여부
	Boolean over14,
	@NotNull	// 마케팅 활용 및 광고성 정보 수신 동의
	Boolean marketingConsent
) {
	public AgreementCommand toCommand() {
		return new AgreementCommand(serviceTerms, privacyPolicy, locationServiceTerms, over14, marketingConsent);
	}
}
