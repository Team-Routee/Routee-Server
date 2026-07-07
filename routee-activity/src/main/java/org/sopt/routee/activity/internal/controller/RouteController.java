package org.sopt.routee.activity.internal.controller;

import java.util.List;

import org.sopt.routee.activity.internal.code.SuccessCode;
import org.sopt.routee.activity.internal.controller.dto.request.CreateRoutesRequest;
import org.sopt.routee.activity.internal.controller.dto.response.RouteListResponse;
import org.sopt.routee.activity.internal.service.RouteService;
import org.sopt.routee.activity.internal.service.dto.result.RouteResult;
import org.sopt.routee.response.ApiResponse;
import org.sopt.routee.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/activity")
public class RouteController implements RouteControllerDocs {

	private final RouteService routeService;

	@PostMapping("/{activityId}/route")
	public ResponseEntity<SuccessResponse<RouteListResponse>> createRoutes(
		@PathVariable(name = "activityId") Long activityId,
		@Valid @RequestBody CreateRoutesRequest request
	) {
		List<RouteResult> results = routeService.createRoutes(activityId, request.toCommands());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.ROUTE_LIST_CREATE_SUCCESS, RouteListResponse.of(activityId, results)));
	}

	@GetMapping("/{activityId}/route")
	public ResponseEntity<SuccessResponse<RouteListResponse>> getRoutes(
		@PathVariable(name = "activityId") Long activityId
	) {
		List<RouteResult> results = routeService.getRoutes(activityId);

		return ResponseEntity.ok(ApiResponse.success(SuccessCode.ROUTE_LIST_GET_SUCCESS, RouteListResponse.of(activityId, results)));
	}
}
