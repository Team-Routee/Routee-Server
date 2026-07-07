package org.sopt.routee.activity.internal.controller;

import java.security.Principal;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.sopt.routee.activity.internal.code.SuccessCode;
import org.sopt.routee.activity.internal.controller.dto.request.ActivityCreateRequest;
import org.sopt.routee.activity.internal.controller.dto.response.ActivityCreateResponse;
import org.sopt.routee.activity.internal.exception.InvalidTimeZoneException;
import org.sopt.routee.activity.internal.service.ActivityService;
import org.sopt.routee.activity.internal.service.dto.result.CreateActivityResult;
import org.sopt.routee.response.ApiResponse;
import org.sopt.routee.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class ActivityController implements ActivityControllerDocs {

	private final ActivityService activityService;

	@PostMapping
	public ResponseEntity<SuccessResponse<ActivityCreateResponse>> create(
		Principal principal,
		@RequestHeader("Time-Zone") String timeZone,
		@Valid @RequestBody ActivityCreateRequest request
	) {
		Long memberId = Long.valueOf(principal.getName());
		CreateActivityResult result = activityService.create(request.toCommand(memberId, parseTimeZone(timeZone)));

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.ACTIVITY_CREATED, ActivityCreateResponse.from(result)));
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
