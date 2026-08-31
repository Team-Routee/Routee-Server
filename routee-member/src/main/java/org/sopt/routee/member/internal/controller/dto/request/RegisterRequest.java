package org.sopt.routee.member.internal.controller.dto.request;

import java.time.ZoneId;

import org.sopt.routee.external.api.type.OAuthProvider;
import org.sopt.routee.member.internal.service.dto.command.RegisterCommand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
	@NotNull
	OAuthProvider provider,
	@NotBlank
	String idToken,
	@NotBlank
	@Pattern(regexp = "^(?=.{1,12}$)[가-힣a-zA-Z0-9]+(?: [가-힣a-zA-Z0-9]+)*$", message = "닉네임은 한글, 영어, 숫자와 공백을 사용하여 1자 이상 12자 이하로 입력해야 하며, 공백은 연속될 수 없습니다.")
	String nickname,
	@NotNull
	@Valid
	AgreementRequest agreements
) {
	public RegisterCommand toCommand(ZoneId timeZone) {
		return new RegisterCommand(provider, idToken, nickname, agreements.toCommand(), timeZone);
	}
}
