package org.sopt.routee.member.internal.controller;

import org.sopt.routee.member.internal.controller.dto.RegisterRequest;
import org.sopt.routee.member.internal.controller.dto.WithdrawRequest;
import org.sopt.routee.response.FailureResponse;
import org.sopt.routee.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Member", description = "회원 API")
public interface MemberControllerDocs {

	@Operation(
		summary = "소셜 회원가입",
		description = "OIDC ID 토큰과 닉네임으로 회원가입합니다. 완료 후 POST /auth/login으로 토큰을 발급받으세요."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "회원가입 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "INVALID_INPUT_VALUE",
						value = "{\"status\":400,\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"nickname은 필수입니다.\"}"),
					@ExampleObject(name = "UNSUPPORTED_PROVIDER",
						value = "{\"status\":400,\"code\":\"UNSUPPORTED_PROVIDER\",\"message\":\"지원하지 않는 소셜 로그인 제공자입니다.\"}")
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
		@ApiResponse(responseCode = "409", description = "이미 가입된 회원",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "ALREADY_REGISTERED_MEMBER",
					value = "{\"status\":409,\"code\":\"ALREADY_REGISTERED_MEMBER\",\"message\":\"이미 가입된 회원입니다.\"}")))
	})
	ResponseEntity<SuccessResponse<Void>> register(@Valid @RequestBody RegisterRequest request);

	@Operation(summary = "회원 탈퇴", description = "인증된 회원의 정보를 삭제하고, 보유한 액세스/리프레시 토큰을 무효화합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "탈퇴 성공"),
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
				})),
		@ApiResponse(responseCode = "404", description = "가입된 회원 없음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "MEMBER_NOT_FOUND",
					value = "{\"status\":404,\"code\":\"MEMBER_NOT_FOUND\",\"message\":\"사용자 정보가 존재하지 않습니다.\"}")))
	})
	ResponseEntity<SuccessResponse<Void>> withdraw(
		Long memberId,
		@RequestHeader(name = "Authorization") String accessTokenWithBearer,
		@Valid @RequestBody WithdrawRequest request
	);
}
