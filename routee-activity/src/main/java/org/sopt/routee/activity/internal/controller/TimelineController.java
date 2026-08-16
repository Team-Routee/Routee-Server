package org.sopt.routee.activity.internal.controller;

import java.time.ZoneId;
import java.util.List;

import org.sopt.routee.activity.internal.code.SuccessCode;
import org.sopt.routee.activity.internal.controller.dto.request.CreateTimelineRequest;
import org.sopt.routee.activity.internal.controller.dto.request.TimelineTitleUpdateRequest;
import org.sopt.routee.activity.internal.controller.dto.response.TimelineListResponse;
import org.sopt.routee.activity.internal.service.TimelineService;
import org.sopt.routee.activity.internal.service.dto.result.TimelineResult;
import org.sopt.routee.response.ApiResponse;
import org.sopt.routee.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/activity")
public class TimelineController implements TimelineControllerDocs {

	private final TimelineService timelineService;

	@PostMapping("/{activityId}/timeline")
	public ResponseEntity<SuccessResponse<Void>> create(
		@AuthenticationPrincipal Long memberId,
		@PathVariable(name = "activityId") Long activityId,
		@RequestHeader("Time-Zone") ZoneId timeZone,
		@Valid @RequestBody CreateTimelineRequest request
	) {
		timelineService.create(request.toCommand(memberId, activityId, timeZone));

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.TIMELINE_CREATED));
	}

	@GetMapping("/{activityId}/timeline")
	public ResponseEntity<SuccessResponse<TimelineListResponse>> getTimelines(
		@AuthenticationPrincipal Long memberId,
		@PathVariable(name = "activityId") Long activityId
	) {
		List<TimelineResult> results = timelineService.getTimelines(activityId, memberId);

		return ResponseEntity.ok(
			ApiResponse.success(SuccessCode.TIMELINE_LIST_GET_SUCCESS, TimelineListResponse.of(activityId, results)));
	}

	@PatchMapping("/{activityId}/timeline/{timelineId}")
	public ResponseEntity<SuccessResponse<Void>> updateTitle(
		@AuthenticationPrincipal Long memberId,
		@PathVariable(name = "activityId") Long activityId,
		@PathVariable(name = "timelineId") Long timelineId,
		@Valid @RequestBody TimelineTitleUpdateRequest request
	) {
		timelineService.updateTitle(request.toCommand(activityId, timelineId, memberId));

		return ResponseEntity.ok(ApiResponse.success(SuccessCode.TIMELINE_TITLE_UPDATED));
	}

	@DeleteMapping("/{activityId}/timeline/{timelineId}")
	public ResponseEntity<SuccessResponse<Void>> delete(
		@AuthenticationPrincipal Long memberId,
		@PathVariable(name = "activityId") Long activityId,
		@PathVariable(name = "timelineId") Long timelineId
	) {
		timelineService.delete(activityId, timelineId, memberId);

		return ResponseEntity.ok(ApiResponse.success(SuccessCode.TIMELINE_DELETED));
	}
}
