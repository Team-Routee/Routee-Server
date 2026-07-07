package org.sopt.routee.activity.internal.controller;

import java.security.Principal;

import org.sopt.routee.activity.internal.controller.dto.request.ActivityCreateRequest;
import org.sopt.routee.activity.internal.controller.dto.response.ActivityCreateResponse;
import org.sopt.routee.response.FailureResponse;
import org.sopt.routee.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Activity", description = "활동 API")
public interface ActivityControllerDocs {

	@Operation(summary = "활동 기록 생성", description = "인증된 사용자의 진행 중 활동 기록을 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "활동 기록 생성 성공",
			content = @Content(schema = @Schema(implementation = ActivityCreateResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "INVALID_INPUT_VALUE",
						value = "{\"status\":400,\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"activityType은 필수입니다.\"}"),
					@ExampleObject(name = "MISSING_STARTED_AT",
						value = "{\"status\":400,\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"startedAt은 필수입니다.\"}"),
					@ExampleObject(name = "INVALID_TIME_ZONE",
						value = "{\"status\":400,\"code\":\"INVALID_TIME_ZONE\",\"message\":\"Time-Zone 헤더 값이 올바르지 않습니다.\"}")
				})),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = FailureResponse.class))),
		@ApiResponse(responseCode = "409", description = "이미 진행 중인 활동이 있음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "ALREADY_IN_PROGRESS_ACTIVITY",
					value = "{\"status\":409,\"code\":\"ALREADY_IN_PROGRESS_ACTIVITY\",\"message\":\"이미 진행 중이거나 일시정지된 활동이 있습니다.\"}")))
	})
	ResponseEntity<SuccessResponse<ActivityCreateResponse>> create(
		Principal principal,
		@Parameter(description = "IANA Time Zone ID", example = "Asia/Seoul", required = true)
		@RequestHeader("Time-Zone") String timeZone,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
			content = @Content(schema = @Schema(implementation = ActivityCreateRequest.class),
				examples = @ExampleObject(value = "{\"activityType\":\"HIKING\",\"startedAt\":\"2026-07-07T15:30:00\"}")))
		@Valid @RequestBody ActivityCreateRequest request
	);
}
