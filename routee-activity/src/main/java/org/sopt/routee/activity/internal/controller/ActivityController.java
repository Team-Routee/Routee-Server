package org.sopt.routee.activity.internal.controller;

import java.security.Principal;

import org.sopt.routee.activity.internal.code.SuccessCode;
import org.sopt.routee.activity.internal.controller.dto.response.ActivityCreateResponse;
import org.sopt.routee.activity.internal.service.ActivityService;
import org.sopt.routee.activity.internal.service.dto.command.CreateActivityCommand;
import org.sopt.routee.response.ApiResponse;
import org.sopt.routee.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/activity")
public class ActivityController implements ActivityControllerDocs {

	private final ActivityService activityService;

	@PostMapping
	public ResponseEntity<SuccessResponse<ActivityCreateResponse>> create(Principal principal) {
		Long activityId = activityService.create(new CreateActivityCommand(Long.valueOf(principal.getName())));

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.ACTIVITY_CREATED, ActivityCreateResponse.from(activityId)));
	}
}
