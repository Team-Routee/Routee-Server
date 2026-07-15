package org.sopt.routee.member.internal.controller;

import java.time.ZoneId;

import org.sopt.routee.member.internal.controller.dto.response.ActivitySummaryResponse;
import org.sopt.routee.member.internal.controller.dto.response.MemberInfoResponse;
import org.sopt.routee.member.internal.controller.dto.request.RegisterRequest;
import org.sopt.routee.member.internal.controller.dto.request.WithdrawRequest;
import org.sopt.routee.response.FailureResponse;
import org.sopt.routee.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

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
					@ExampleObject(name = "INVALID_NICKNAME_FORMAT",
						value = "{\"status\":400,\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"닉네임은 한글, 영어, 숫자만 사용하여 2자 이상 12자 이하로 입력해야 합니다.\"}"),
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
		@ApiResponse(responseCode = "409", description = "이미 가입된 회원",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "ALREADY_REGISTERED_MEMBER",
					value = "{\"status\":409,\"code\":\"ALREADY_REGISTERED_MEMBER\",\"message\":\"이미 가입된 회원입니다.\"}")))
	})
	ResponseEntity<SuccessResponse<Void>> register(@Valid @RequestBody RegisterRequest request);

	@Operation(summary = "회원 탈퇴", description = "인증된 회원의 정보를 삭제하고, 보유한 액세스/리프레시 토큰을 무효화합니다.")
	@SecurityRequirement(name = "bearerAuth")
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

	@Operation(
		summary = "내 정보 조회",
		description = "인증된 회원의 닉네임, 프로필 이미지 URL, 가입일, 가입 후 경과일, 누적 활동 횟수를 조회합니다."
	)
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = MemberInfoResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "MISSING_REQUEST_HEADER",
						value = "{\"status\":400,\"code\":\"MISSING_REQUEST_HEADER\",\"message\":\"필수 요청 헤더가 누락되었습니다.\"}"),
					@ExampleObject(name = "INVALID_HEADER",
						value = "{\"status\":400,\"code\":\"INVALID_HEADER\",\"message\":\"헤더값이 올바르지 않습니다.\"}")
				})),
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
	ResponseEntity<SuccessResponse<MemberInfoResponse>> getMemberInfo(
		Long memberId,
		@Parameter(description = "IANA Time Zone ID", example = "Asia/Seoul", required = true)
		@RequestHeader("Time-Zone") ZoneId timeZone
	);

	@Operation(
		summary = "월별 활동 요약 조회",
		description = "인증된 회원의 특정 연/월에 대한 일자별 활동 요약(총 활동 시간, 활동 횟수, 커버 이미지)을 조회합니다."
	)
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = ActivitySummaryResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "MISSING_REQUEST_PARAMETER",
					value = "{\"status\":400,\"code\":\"MISSING_REQUEST_PARAMETER\",\"message\":\"필수 요청 파라미터가 누락되었습니다.\"}"))),
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
	ResponseEntity<SuccessResponse<ActivitySummaryResponse>> getActivitySummary(
		Long memberId,
		@Parameter(description = "조회할 연도", example = "2026", required = true)
		@RequestParam("year") Integer year,
		@Parameter(description = "조회할 월", example = "7", required = true)
		@Min(1) @Max(12) @RequestParam("month") Integer month
	);
}
