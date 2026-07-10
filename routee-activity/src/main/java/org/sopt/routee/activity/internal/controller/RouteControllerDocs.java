package org.sopt.routee.activity.internal.controller;

import org.sopt.routee.activity.internal.controller.dto.request.CreateRoutesRequest;
import org.sopt.routee.activity.internal.controller.dto.response.RouteListResponse;
import org.sopt.routee.response.FailureResponse;
import org.sopt.routee.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

@Tag(name = "Route", description = "루트 API")
@SecurityRequirement(name = "bearerAuth")
public interface RouteControllerDocs {

	@Operation(summary = "루트 목록 생성", description = "활동 기록에 속하는 루트 목록을 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "루트 목록 생성 성공",
			content = @Content(schema = @Schema(implementation = RouteListResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = {
					@ExampleObject(name = "ROUTES_EMPTY",
						value = "{\"status\":400,\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"루트 목록 생성 시 최소 1개 이상의 루트를 입력해야 합니다.\"}"),
					@ExampleObject(name = "ROUTES_TOO_MANY",
						value = "{\"status\":400,\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"루트는 최대 12개까지 등록할 수 있습니다.\"}")
				})),
		@ApiResponse(responseCode = "404", description = "활동 기록이 존재하지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "ACTIVITY_NOT_FOUND",
					value = "{\"status\":404,\"code\":\"ACTIVITY_NOT_FOUND\",\"message\":\"활동 기록이 존재하지 않습니다.\"}"))),
		@ApiResponse(responseCode = "409", description = "루트 목록이 이미 존재함",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "ROUTE_ALREADY_EXISTS",
					value = "{\"status\":409,\"code\":\"ROUTE_ALREADY_EXISTS\",\"message\":\"루트 목록이 이미 존재합니다.\"}")))
	})
	ResponseEntity<SuccessResponse<RouteListResponse>> createRoutes(
		@PathVariable(name = "activityId") Long activityId,
		@Valid @RequestBody CreateRoutesRequest request
	);

	@Operation(summary = "루트 목록 조회", description = "활동 기록에 속하는 루트 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "루트 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = RouteListResponse.class))),
		@ApiResponse(responseCode = "404", description = "활동 기록이 존재하지 않음",
			content = @Content(schema = @Schema(implementation = FailureResponse.class),
				examples = @ExampleObject(name = "ACTIVITY_NOT_FOUND",
					value = "{\"status\":404,\"code\":\"ACTIVITY_NOT_FOUND\",\"message\":\"활동 기록이 존재하지 않습니다.\"}")))
	})
	ResponseEntity<SuccessResponse<RouteListResponse>> getRoutes(
		@PathVariable(name = "activityId") Long activityId
	);
}
