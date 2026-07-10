package org.sopt.routee.activity.internal.controller;

import org.sopt.routee.activity.internal.controller.dto.request.CreateTimelineRequest;
import org.sopt.routee.activity.internal.controller.dto.response.TimelineListResponse;
import org.sopt.routee.response.FailureResponse;
import org.sopt.routee.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

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

@Tag(name = "Timeline", description = "타임라인 API")
@SecurityRequirement(name = "bearerAuth")
public interface TimelineControllerDocs {

	@Operation(summary = "타임라인 생성", description = "인증된 사용자의 활동 기록에 타임라인을 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "타임라인 생성 성공",
			content = @Content(
				examples = @ExampleObject(value = "{\"status\":201,\"code\":\"TIMELINE_CREATED\",\"message\":\"타임라인 생성에 성공했습니다.\",\"data\":null}"))),
		@ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "INVALID_INPUT_VALUE",
						value = "{\"status\":400,\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"title은 필수입니다.\"}"),
					@ExampleObject(name = "INVALID_TIME_ZONE",
						value = "{\"status\":400,\"code\":\"INVALID_TIME_ZONE\",\"message\":\"Time-Zone 헤더 값이 올바르지 않습니다.\"}")
				})),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = FailureResponse.class))),
		@ApiResponse(responseCode = "404", description = "활동 기록이 존재하지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "ACTIVITY_NOT_FOUND",
					value = "{\"status\":404,\"code\":\"ACTIVITY_NOT_FOUND\",\"message\":\"활동 기록이 존재하지 않습니다.\"}"))),
		@ApiResponse(responseCode = "409", description = "해당 좌표 번호의 타임라인이 이미 존재함",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "TIMELINE_ALREADY_EXISTS",
					value = "{\"status\":409,\"code\":\"TIMELINE_ALREADY_EXISTS\",\"message\":\"해당 경로 좌표에는 이미 타임라인이 존재합니다.\"}")))
	})
	ResponseEntity<SuccessResponse<Void>> create(
		@Parameter(hidden = true)
		Long memberId,
		@Parameter(description = "활동 기록 식별자", example = "1", required = true)
		@PathVariable(name = "activityId") Long activityId,
		@Parameter(description = "IANA Time Zone ID", example = "Asia/Seoul", required = true)
		@RequestHeader("Time-Zone") String timeZone,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
			content = @Content(schema = @Schema(implementation = CreateTimelineRequest.class),
				examples = @ExampleObject(value = "{\"title\":\"정상 도착\",\"timelineImageObjectKey\":\"550e8400-summit.jpg\",\"createdAt\":\"2026-07-02T10:45:00\",\"trackPointIndex\":35,\"location\":{\"longitude\":127.123456,\"latitude\":37.123456,\"elevation\":542,\"pointIndex\":35},\"status\":\"SUCCESSFUL_CREATED\"}")))
		@Valid @RequestBody CreateTimelineRequest request
	);

	@Operation(summary = "타임라인 목록 조회", description = "인증된 사용자의 활동 기록에 속한 타임라인 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "타임라인 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = TimelineListResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = FailureResponse.class))),
		@ApiResponse(responseCode = "404", description = "활동 기록이 존재하지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "ACTIVITY_NOT_FOUND",
					value = "{\"status\":404,\"code\":\"ACTIVITY_NOT_FOUND\",\"message\":\"활동 기록이 존재하지 않습니다.\"}")))
	})
	ResponseEntity<SuccessResponse<TimelineListResponse>> getTimelines(
		@Parameter(hidden = true)
		Long memberId,
		@Parameter(description = "활동 기록 식별자", example = "1", required = true)
		@PathVariable(name = "activityId") Long activityId
	);
}
