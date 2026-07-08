package org.sopt.routee.activity.internal.controller;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.sopt.routee.activity.internal.code.SuccessCode;
import org.sopt.routee.activity.internal.controller.dto.request.CreateTimelineRequest;
import org.sopt.routee.activity.internal.exception.InvalidTimeZoneException;
import org.sopt.routee.activity.internal.service.TimelineService;
import org.sopt.routee.response.ApiResponse;
import org.sopt.routee.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
		@RequestHeader("Time-Zone") String timeZone,
		@Valid @RequestBody CreateTimelineRequest request
	) {
		timelineService.create(request.toCommand(memberId, activityId, parseTimeZone(timeZone)));

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.TIMELINE_CREATED));
	}

	private ZoneId parseTimeZone(String timeZone) {
		try {
			ZoneId zoneId = ZoneId.of(timeZone);
			if (zoneId instanceof ZoneOffset) {
				throw new InvalidTimeZoneException();
			}
			return zoneId;
		} catch (DateTimeException e) {
			throw new InvalidTimeZoneException();
		}
	}
}
