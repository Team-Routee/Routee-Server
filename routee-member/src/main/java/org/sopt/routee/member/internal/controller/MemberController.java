package org.sopt.routee.member.internal.controller;

import org.sopt.routee.member.internal.code.SuccessCode;
import org.sopt.routee.member.internal.controller.dto.RegisterRequest;
import org.sopt.routee.member.internal.service.MemberService;
import org.sopt.routee.response.ApiResponse;
import org.sopt.routee.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
}
