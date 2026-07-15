package org.sopt.routee.auth.internal.controller;

import org.sopt.routee.auth.internal.controller.dto.response.TokenResponse;
import org.sopt.routee.auth.internal.controller.dto.request.LoginRequest;
import org.sopt.routee.auth.internal.controller.dto.request.LogoutRequest;
import org.sopt.routee.auth.internal.controller.dto.request.ReissueRequest;
import org.sopt.routee.response.FailureResponse;
import org.sopt.routee.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Auth", description = "인증 API")
public interface AuthControllerDocs {

	@Operation(summary = "소셜 로그인", description = "OIDC ID 토큰으로 로그인하고 액세스/리프레시 토큰을 발급합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그인 성공",
			content = @Content(schema = @Schema(implementation = TokenResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "INVALID_INPUT_VALUE",
						value = "{\"status\":400,\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"provider는 필수입니다.\"}"),
					@ExampleObject(name = "INVALID_REQUEST_BODY",
						value = "{\"status\":400,\"code\":\"INVALID_REQUEST_BODY\",\"message\":\"요청 바디를 읽을 수 없습니다.\"}")
				})),
		@ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 id_token",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "INVALID_ID_TOKEN",
						value = "{\"status\":401,\"code\":\"INVALID_ID_TOKEN\",\"message\":\"유효하지 않은 id_token입니다.\"}"),
					@ExampleObject(name = "ID_TOKEN_EXPIRED",
						value = "{\"status\":401,\"code\":\"ID_TOKEN_EXPIRED\",\"message\":\"만료된 id_token입니다.\"}"),
					@ExampleObject(name = "INVALID_TOKEN_CLAIMS",
						value = "{\"status\":401,\"code\":\"INVALID_TOKEN_CLAIMS\",\"message\":\"id_token 클레임이 유효하지 않습니다.\"}")
				})),
		@ApiResponse(responseCode = "404", description = "가입된 회원 없음 - 회원가입 필요",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "MEMBER_NOT_FOUND",
					value = "{\"status\":404,\"code\":\"MEMBER_NOT_FOUND\",\"message\":\"사용자 정보가 존재하지 않습니다.\"}")))
	})
	ResponseEntity<SuccessResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request);

	@Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 액세스/리프레시 토큰을 재발급합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "재발급 성공",
			content = @Content(schema = @Schema(implementation = TokenResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "INVALID_INPUT_VALUE",
					value = "{\"status\":400,\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"refresh_token은 필수입니다.\"}"))),
		@ApiResponse(responseCode = "401", description = "만료되었거나 유효하지 않은 리프레시 토큰",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "INVALID_TOKEN",
						value = "{\"status\":401,\"code\":\"INVALID_TOKEN\",\"message\":\"유효하지 않은 토큰입니다.\"}"),
					@ExampleObject(name = "TOKEN_EXPIRED",
						value = "{\"status\":401,\"code\":\"TOKEN_EXPIRED\",\"message\":\"만료된 토큰입니다.\"}")
				}))
	})
	ResponseEntity<SuccessResponse<TokenResponse>> reissue(@Valid @RequestBody ReissueRequest request);

	@Operation(summary = "로그아웃", description = "액세스 토큰을 블랙리스트에 등록하고 리프레시 토큰을 무효화합니다.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그아웃 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "INVALID_INPUT_VALUE",
					value = "{\"status\":400,\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"refresh_token은 필수입니다.\"}"))),
		@ApiResponse(responseCode = "401", description = "만료되었거나 유효하지 않은 액세스 토큰",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "INVALID_TOKEN",
						value = "{\"status\":401,\"code\":\"INVALID_TOKEN\",\"message\":\"유효하지 않은 토큰입니다.\"}"),
					@ExampleObject(name = "TOKEN_EXPIRED",
						value = "{\"status\":401,\"code\":\"TOKEN_EXPIRED\",\"message\":\"만료된 토큰입니다.\"}")
				}))
	})
	ResponseEntity<SuccessResponse<Void>> logout(String accessTokenWithBearer, @Valid @RequestBody LogoutRequest logoutRequest);
}
