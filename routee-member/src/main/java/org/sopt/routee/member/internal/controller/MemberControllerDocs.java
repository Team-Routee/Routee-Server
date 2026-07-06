package org.sopt.routee.member.internal.controller;

import org.sopt.routee.member.internal.controller.dto.RegisterRequest;
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
}
