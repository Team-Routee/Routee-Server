package org.sopt.routee.member.internal.controller;

import org.sopt.routee.member.internal.code.SuccessCode;
import org.sopt.routee.member.internal.controller.dto.RegisterRequest;
import org.sopt.routee.member.internal.controller.dto.WithdrawRequest;
import org.sopt.routee.member.internal.service.MemberService;
import org.sopt.routee.response.ApiResponse;
import org.sopt.routee.response.SuccessResponse;
import org.sopt.routee.util.TokenExtractor;
import org.sopt.routee.util.TokenHasher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController implements MemberControllerDocs {

	private final MemberService memberService;

	@PostMapping("/register")
	public ResponseEntity<SuccessResponse<Void>> register(
		@Valid @RequestBody RegisterRequest request
	) {
		memberService.register(request.toCommand());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.MEMBER_REGISTER));
	}

	@DeleteMapping("/withdraw")
	public ResponseEntity<SuccessResponse<Void>> withdraw(
		@AuthenticationPrincipal Long memberId,
		@RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessTokenWithBearer,
		@Valid @RequestBody WithdrawRequest request
	) {
		String accessTokenHash = TokenHasher.hash(TokenExtractor.extract(accessTokenWithBearer));
		String refreshTokenHash = TokenHasher.hash(request.refreshToken());

		memberService.withdraw(memberId, accessTokenHash, refreshTokenHash);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.MEMBER_WITHDRAW));
	}
}
