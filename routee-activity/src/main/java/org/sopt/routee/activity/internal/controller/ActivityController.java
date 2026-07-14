package org.sopt.routee.activity.internal.controller;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.sopt.routee.activity.internal.code.SuccessCode;
import org.sopt.routee.activity.internal.controller.dto.request.ActivityCompleteRequest;
import org.sopt.routee.activity.internal.controller.dto.request.ActivityCreateRequest;
import org.sopt.routee.activity.internal.controller.dto.request.ActivityStatusUpdateRequest;
import org.sopt.routee.activity.internal.controller.dto.request.ImageUrlRequest;
import org.sopt.routee.activity.internal.controller.dto.response.ActivityCreateResponse;
import org.sopt.routee.activity.internal.controller.dto.response.ActivityEditListResponse;
import org.sopt.routee.activity.internal.controller.dto.response.ActivityRecapResponse;
import org.sopt.routee.activity.internal.controller.dto.response.ActivityStatusResponse;
import org.sopt.routee.activity.internal.controller.dto.response.ActivityStatisticsResponse;
import org.sopt.routee.activity.internal.controller.dto.response.ActivitiesByDateResponse;
import org.sopt.routee.activity.internal.controller.dto.response.ActivityTrackResponse;
import org.sopt.routee.activity.internal.controller.dto.response.ImageUrlResponse;
import org.sopt.routee.activity.internal.exception.InvalidTimeZoneException;
import org.sopt.routee.activity.internal.service.ActivityService;
import org.sopt.routee.activity.internal.service.dto.command.GetActivityRecapCommand;
import org.sopt.routee.activity.internal.service.dto.result.ActivityEditListResult;
import org.sopt.routee.activity.internal.service.dto.result.ActivityRecapResult;
import org.sopt.routee.activity.internal.service.dto.result.ActivityStatisticsResult;
import org.sopt.routee.activity.internal.service.dto.result.ActivitiesByDateResult;
import org.sopt.routee.activity.internal.service.dto.result.ActivityTrackResult;
import org.sopt.routee.activity.internal.service.dto.result.CreateActivityResult;
import org.sopt.routee.activity.internal.service.dto.result.ImageUrlResult;
import org.sopt.routee.activity.internal.service.dto.result.UpdateActivityStatusResult;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageSize;
import org.sopt.routee.response.ApiResponse;
import org.sopt.routee.response.SuccessResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ActivityController implements ActivityControllerDocs {

	private final ActivityService activityService;

	@PostMapping("/activity")
	public ResponseEntity<SuccessResponse<ActivityCreateResponse>> create(
		@AuthenticationPrincipal Long memberId,
		@RequestHeader("Time-Zone") String timeZone,
		@Valid @RequestBody ActivityCreateRequest request
	) {
		CreateActivityResult result = activityService.create(request.toCommand(memberId, parseTimeZone(timeZone)));

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.ACTIVITY_CREATED, ActivityCreateResponse.from(result)));
	}

	@PostMapping("/activity/{activityId}/image-url")
	public ResponseEntity<SuccessResponse<ImageUrlResponse>> generateImageUploadUrl(
		@AuthenticationPrincipal Long memberId,
		@PathVariable(name = "activityId") Long activityId,
		@Valid @RequestBody ImageUrlRequest request
	) {
		ImageUrlResult result = activityService.generateImageUploadUrl(
			request.toCommand(activityId, memberId, FileUploadDirectory.TIMELINE, FileUploadImageSize.ORIGINAL)
		);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.IMAGE_UPLOAD_URL_CREATED, ImageUrlResponse.of(result)));
	}

	@PostMapping("/activity/{activityId}/map-image-url")
	public ResponseEntity<SuccessResponse<ImageUrlResponse>> generateMapImageUploadUrl(
		@AuthenticationPrincipal Long memberId,
		@PathVariable(name = "activityId") Long activityId,
		@Valid @RequestBody ImageUrlRequest request
	) {
		ImageUrlResult result = activityService.generateImageUploadUrl(
			request.toCommand(activityId, memberId, FileUploadDirectory.RECAP, null)
		);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.IMAGE_UPLOAD_URL_CREATED, ImageUrlResponse.of(result)));
	}

	@PatchMapping("/activity/{activityId}/status")
	public ResponseEntity<SuccessResponse<ActivityStatusResponse>> updateStatus(
		@AuthenticationPrincipal Long memberId,
		@PathVariable(name = "activityId") Long activityId,
		@Valid @RequestBody ActivityStatusUpdateRequest request
	) {
		UpdateActivityStatusResult result = activityService.updateStatus(request.toCommand(activityId, memberId));

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.ACTIVITY_STATUS_UPDATED, ActivityStatusResponse.from(result)));
	}

	@PutMapping("/activity/{activityId}")
	public ResponseEntity<SuccessResponse<Void>> complete(
		@AuthenticationPrincipal Long memberId,
		@RequestHeader("Time-Zone") String timeZone,
		@PathVariable(name = "activityId") Long activityId,
		@Valid @RequestBody ActivityCompleteRequest request
	) {
		activityService.complete(request.toCommand(activityId, memberId, parseTimeZone(timeZone)));

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.ACTIVITY_COMPLETED));
	}

	@GetMapping("/activity/{activityId}/statistics")
	public ResponseEntity<SuccessResponse<ActivityStatisticsResponse>> getStatistics(
		@AuthenticationPrincipal Long memberId,
		@PathVariable(name = "activityId") Long activityId,
		@RequestHeader("Time-Zone") String timeZone
	) {
		ActivityStatisticsResult result = activityService.getStatistics(activityId, memberId, parseTimeZone(timeZone));

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.ACTIVITY_STATISTICS_GET_SUCCESS, ActivityStatisticsResponse.from(result)));
	}

	@GetMapping("/activity/{activityId}/track")
	public ResponseEntity<SuccessResponse<ActivityTrackResponse>> getTrack(
		@AuthenticationPrincipal Long memberId,
		@PathVariable(name = "activityId") Long activityId
	) {
		ActivityTrackResult result = activityService.getTrack(activityId, memberId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.ACTIVITY_TRACK_GET_SUCCESS, ActivityTrackResponse.from(result)));
	}

	@GetMapping("/activity/{activityId}/recap")
	public ResponseEntity<SuccessResponse<ActivityRecapResponse>> getRecap(
		@AuthenticationPrincipal Long memberId,
		@PathVariable(name = "activityId") Long activityId
	) {
		ActivityRecapResult result = activityService.getRecap(GetActivityRecapCommand.of(activityId, memberId));

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.ACTIVITY_RECAP_GET_SUCCESS, ActivityRecapResponse.from(result)));
	}

	@GetMapping("/activity")
	public ResponseEntity<SuccessResponse<ActivityEditListResponse>> getActivityEditList(
		@AuthenticationPrincipal Long memberId,
		@RequestHeader("Time-Zone") String timeZone,
		@RequestParam(name = "year", required = true) Integer year,
		@RequestParam(name = "month", required = true) Integer month
	) {
		ActivityEditListResult result = activityService.getActivityEditList(
			memberId, YearMonth.of(year, month), parseTimeZone(timeZone)
		);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.ACTIVITY_EDIT_LIST_GET_SUCCESS, ActivityEditListResponse.from(result)));
	}

	@GetMapping("/archive/activity")
	public ResponseEntity<SuccessResponse<ActivitiesByDateResponse>> getActivitiesByDate(
		@AuthenticationPrincipal Long memberId,
		@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
		@RequestHeader("Time-Zone") String timeZone
	) {
		ActivitiesByDateResult result = activityService.getActivitiesByDate(memberId, date, parseTimeZone(timeZone));

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.ARCHIVE_ACTIVITY_LIST_GET_SUCCESS, ActivitiesByDateResponse.from(result)));
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
