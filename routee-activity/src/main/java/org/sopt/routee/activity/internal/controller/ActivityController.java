package org.sopt.routee.activity.internal.controller;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.security.Principal;

import org.sopt.routee.activity.internal.code.SuccessCode;
import org.sopt.routee.activity.internal.controller.dto.request.ActivityCreateRequest;
import org.sopt.routee.activity.internal.controller.dto.response.ActivityCreateResponse;
import org.sopt.routee.activity.internal.exception.InvalidTimeZoneException;
import org.sopt.routee.activity.internal.service.ActivityService;
import org.sopt.routee.activity.internal.service.dto.result.CreateActivityResult;
import org.sopt.routee.activity.internal.controller.dto.request.ImageUrlRequest;
import org.sopt.routee.activity.internal.controller.dto.response.ImageUrlResponse;
import org.sopt.routee.activity.internal.service.ActivityImageUrlService;
import org.sopt.routee.activity.internal.service.dto.result.ImageUrlResult;
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
public class ActivityController implements ActivityControllerDocs {

	private final ActivityService activityService;
	private final ActivityImageUrlService activityImageUrlService;

	@PostMapping("/{activityId}/image-url")
	public ResponseEntity<SuccessResponse<ImageUrlResponse>> generateImageUploadUrl(
		@PathVariable Long activityId,
		@Valid @RequestBody ImageUrlRequest request,
		Principal principal
	@PostMapping
	public ResponseEntity<SuccessResponse<ActivityCreateResponse>> create(
		@AuthenticationPrincipal Long memberId,
		@RequestHeader("Time-Zone") String timeZone,
		@Valid @RequestBody ActivityCreateRequest request
	) {
		Long memberId = Long.valueOf(principal.getName());
		ImageUrlResult result = activityImageUrlService.generateImageUploadUrl(activityId, memberId, request.fileName());
		CreateActivityResult result = activityService.create(request.toCommand(memberId, parseTimeZone(timeZone)));

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.IMAGE_UPLOAD_URL_CREATED, ImageUrlResponse.of(result)));
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
