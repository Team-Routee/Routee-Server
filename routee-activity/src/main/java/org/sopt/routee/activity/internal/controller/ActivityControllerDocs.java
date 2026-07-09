package org.sopt.routee.activity.internal.controller;

import org.sopt.routee.activity.internal.controller.dto.request.ActivityCompleteRequest;
import org.sopt.routee.activity.internal.controller.dto.request.ActivityCreateRequest;
import org.sopt.routee.activity.internal.controller.dto.request.ActivityStatusUpdateRequest;
import org.sopt.routee.activity.internal.controller.dto.request.ImageUrlRequest;
import org.sopt.routee.activity.internal.controller.dto.response.ActivityCreateResponse;
import org.sopt.routee.activity.internal.controller.dto.response.ActivityStatusResponse;
import org.sopt.routee.activity.internal.controller.dto.response.ActivityStatisticsResponse;
import org.sopt.routee.activity.internal.controller.dto.response.ImageUrlResponse;
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
		Long memberId,
		@Parameter(description = "IANA Time Zone ID", example = "Asia/Seoul", required = true)
		@RequestHeader("Time-Zone") String timeZone,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
			content = @Content(schema = @Schema(implementation = ActivityCreateRequest.class),
				examples = @ExampleObject(value = "{\"activityType\":\"HIKING\",\"startedAt\":\"2026-07-07T15:30:00\"}")))
		@Valid @RequestBody ActivityCreateRequest request
	);

	@Operation(summary = "활동 이미지 업로드 URL 발급", description = "활동 이미지 원본 업로드를 위한 S3 presigned URL을 발급합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "이미지 업로드 URL 발급 성공",
			content = @Content(schema = @Schema(implementation = ImageUrlResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "INVALID_INPUT_VALUE",
						value = "{\"status\":400,\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"fileName은 필수입니다.\"}"),
					@ExampleObject(name = "UNSUPPORTED_IMAGE_FILE_EXTENSION",
						value = "{\"status\":400,\"code\":\"UNSUPPORTED_IMAGE_FILE_EXTENSION\",\"message\":\"지원하지 않는 이미지 파일 확장자입니다.\"}")
				})),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "UNAUTHORIZED",
						value = "{\"status\":401,\"code\":\"UNAUTHORIZED\",\"message\":\"인증되지 않은 사용자입니다.\"}"),
					@ExampleObject(name = "INVALID_TOKEN",
						value = "{\"status\":401,\"code\":\"INVALID_TOKEN\",\"message\":\"유효하지 않은 토큰입니다.\"}")
				})),
		@ApiResponse(responseCode = "404", description = "활동 기록 없음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "ACTIVITY_NOT_FOUND",
					value = "{\"status\":404,\"code\":\"ACTIVITY_NOT_FOUND\",\"message\":\"활동 기록을 찾을 수 없습니다.\"}")))
	})
	ResponseEntity<SuccessResponse<ImageUrlResponse>> generateImageUploadUrl(
		Long memberId,
		@PathVariable(name = "activityId") Long activityId,
		@Valid @RequestBody ImageUrlRequest request
	);

	@Operation(summary = "활동 상태 변경", description = "활동 상태를 운동 중 또는 일시정지 상태로 변경합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "활동 상태 변경 성공",
			content = @Content(schema = @Schema(implementation = ActivityStatusResponse.class),
				examples = @ExampleObject(value = "{\"activityId\":1,\"status\":\"ACTIVITY_PAUSED\"}"))),
		@ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "INVALID_INPUT_VALUE",
						value = "{\"status\":400,\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"status는 필수입니다.\"}"),
					@ExampleObject(name = "INVALID_REQUEST_BODY",
						value = "{\"status\":400,\"code\":\"INVALID_REQUEST_BODY\",\"message\":\"요청 바디를 읽을 수 없습니다.\"}")
				})),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "UNAUTHORIZED",
						value = "{\"status\":401,\"code\":\"UNAUTHORIZED\",\"message\":\"인증되지 않은 사용자입니다.\"}"),
					@ExampleObject(name = "INVALID_TOKEN",
						value = "{\"status\":401,\"code\":\"INVALID_TOKEN\",\"message\":\"유효하지 않은 토큰입니다.\"}")
				})),
		@ApiResponse(responseCode = "404", description = "활동 기록 없음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "ACTIVITY_NOT_FOUND",
					value = "{\"status\":404,\"code\":\"ACTIVITY_NOT_FOUND\",\"message\":\"활동 기록이 존재하지 않습니다.\"}"))),
		@ApiResponse(responseCode = "409", description = "변경할 수 없는 활동 상태",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "INVALID_ACTIVITY_STATUS_TRANSITION",
						value = "{\"status\":409,\"code\":\"INVALID_ACTIVITY_STATUS_TRANSITION\",\"message\":\"변경할 수 없는 활동 상태입니다.\"}"),
					@ExampleObject(name = "ACTIVITY_STATUS_ALREADY_SAME",
						value = "{\"status\":409,\"code\":\"ACTIVITY_STATUS_ALREADY_SAME\",\"message\":\"이미 요청한 활동 상태입니다.\"}"),
					@ExampleObject(name = "ACTIVITY_ALREADY_COMPLETED",
						value = "{\"status\":409,\"code\":\"ACTIVITY_ALREADY_COMPLETED\",\"message\":\"이미 완료된 활동입니다.\"}")
				}))
	})
	ResponseEntity<SuccessResponse<ActivityStatusResponse>> updateStatus(
		Long memberId,
		@PathVariable(name = "activityId") Long activityId,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
			content = @Content(schema = @Schema(implementation = ActivityStatusUpdateRequest.class),
				examples = @ExampleObject(value = "{\"status\":\"ACTIVITY_PAUSED\"}")))
		@Valid @RequestBody ActivityStatusUpdateRequest request
	);

	@Operation(summary = "활동 종료 데이터 저장", description = "인증된 사용자의 활동 종료 데이터를 기존 활동 기록에 저장합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "활동 기록 저장 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "INVALID_INPUT_VALUE",
						value = "{\"status\":400,\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"title은 필수입니다.\"}"),
					@ExampleObject(name = "INVALID_TIME_ZONE",
						value = "{\"status\":400,\"code\":\"INVALID_TIME_ZONE\",\"message\":\"Time-Zone 헤더 값이 올바르지 않습니다.\"}"),
					@ExampleObject(name = "INVALID_TRACK",
						value = "{\"status\":400,\"code\":\"INVALID_TRACK\",\"message\":\"트랙 정보가 올바르지 않습니다.\"}")
				})),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = FailureResponse.class))),
		@ApiResponse(responseCode = "404", description = "활동 기록 없음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "ACTIVITY_NOT_FOUND",
					value = "{\"status\":404,\"code\":\"ACTIVITY_NOT_FOUND\",\"message\":\"활동 기록이 존재하지 않습니다.\"}")))
	})
	ResponseEntity<SuccessResponse<Void>> complete(
		Long memberId,
		@Parameter(description = "IANA Time Zone ID", example = "Asia/Seoul", required = true)
		@RequestHeader("Time-Zone") String timeZone,
		@PathVariable(name = "activityId") Long activityId,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
			content = @Content(schema = @Schema(implementation = ActivityCompleteRequest.class),
				examples = @ExampleObject(value = """
					{"title":"북한산 기록","activityType":"HIKING","status":"ACTIVITY_COMPLETED","distance":5400,"durationSec":3600,"maxElevation":836,"mapImageUrl":"https://example.com/map.png","coverImageObjectKey":"activity/1/cover.png","track":"LINESTRING ZM (126.978 37.566 20 0, 126.979 37.567 25 10)","startedAt":"2026-07-07T15:30:00","endedAt":"2026-07-07T16:30:00"}""")))
		@Valid @RequestBody ActivityCompleteRequest request
	);

	@Operation(summary = "활동 통계 기록 조회", description = "인증된 사용자의 활동 통계 기록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "활동 통계 기록 조회 성공",
			content = @Content(schema = @Schema(implementation = ActivityStatisticsResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "INVALID_TIME_ZONE",
					value = "{\"status\":400,\"code\":\"INVALID_TIME_ZONE\",\"message\":\"Time-Zone 헤더 값이 올바르지 않습니다.\"}"))),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = FailureResponse.class))),
		@ApiResponse(responseCode = "404", description = "활동 기록이 존재하지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "ACTIVITY_NOT_FOUND",
					value = "{\"status\":404,\"code\":\"ACTIVITY_NOT_FOUND\",\"message\":\"활동 기록이 존재하지 않습니다.\"}")))
	})
	ResponseEntity<SuccessResponse<ActivityStatisticsResponse>> getStatistics(
		Long memberId,
		@PathVariable(name = "activityId") Long activityId,
		@Parameter(description = "IANA Time Zone ID", example = "Asia/Seoul", required = true)
		@RequestHeader("Time-Zone") String timeZone
	);
}
