package org.sopt.routee.auth.internal.controller;

import org.sopt.routee.auth.internal.service.AuthService;
import org.sopt.routee.auth.internal.service.dto.command.LoginCommand;
import org.sopt.routee.auth.internal.code.SuccessCode;
import org.sopt.routee.auth.internal.controller.dto.response.TokenResponse;
import org.sopt.routee.auth.internal.controller.dto.request.LoginRequest;
import org.sopt.routee.auth.internal.controller.dto.request.ReissueRequest;
import org.sopt.routee.auth.internal.service.dto.result.TokenResult;
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
@RequestMapping(path = "/api/v1/auth")
public class AuthController implements AuthControllerDocs {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<SuccessResponse<TokenResponse>> login(
		@Valid @RequestBody LoginRequest request
	) {
		TokenResult result = authService.login(new LoginCommand(request.provider(), request.idToken()));
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.LOGIN_SUCCESS, TokenResponse.of(result)));
	}

	@PostMapping("/reissue")
	public ResponseEntity<SuccessResponse<TokenResponse>> reissue(
		@Valid @RequestBody ReissueRequest request
	) {
		TokenResult result = authService.reissue(request.refreshToken());
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.TOKEN_REISSUE, TokenResponse.of(result)));
	}
}