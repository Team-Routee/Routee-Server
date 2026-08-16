package org.sopt.routee.member.internal.controller;

import java.time.ZoneId;

import org.sopt.routee.member.internal.code.SuccessCode;
import org.sopt.routee.member.internal.controller.dto.response.ActivitySummaryResponse;
import org.sopt.routee.member.internal.controller.dto.response.MemberInfoResponse;
import org.sopt.routee.member.internal.controller.dto.response.MemberProfileResponse;
import org.sopt.routee.member.internal.controller.dto.response.NicknameResponse;
import org.sopt.routee.member.internal.controller.dto.response.ProfileImageResponse;
import org.sopt.routee.member.internal.controller.dto.response.ProfileImageUploadUrlResponse;
import org.sopt.routee.member.internal.controller.dto.request.NicknameUpdateRequest;
import org.sopt.routee.member.internal.controller.dto.request.ProfileImageUpdateRequest;
import org.sopt.routee.member.internal.controller.dto.request.ProfileImageUploadUrlRequest;
import org.sopt.routee.member.internal.controller.dto.request.RegisterRequest;
import org.sopt.routee.member.internal.controller.dto.request.WithdrawRequest;
import org.sopt.routee.member.internal.service.MemberService;
import org.sopt.routee.member.internal.service.dto.result.ActivitySummaryResult;
import org.sopt.routee.member.internal.service.dto.result.MemberInfoResult;
import org.sopt.routee.member.internal.service.dto.result.MemberProfileResult;
import org.sopt.routee.member.internal.service.dto.result.ProfileImageUploadUrlResult;
import org.sopt.routee.member.internal.service.dto.result.UpdateNicknameResult;
import org.sopt.routee.member.internal.service.dto.result.UpdateProfileImageResult;
import org.sopt.routee.response.ApiResponse;
import org.sopt.routee.response.SuccessResponse;
import org.sopt.routee.util.TokenExtractor;
import org.sopt.routee.util.TokenHasher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1")
public class MemberController implements MemberControllerDocs {

	private final MemberService memberService;

	@PostMapping(path = "/member/register")
	public ResponseEntity<SuccessResponse<Void>> register(
		@Valid @RequestBody RegisterRequest request
	) {
		memberService.register(request.toCommand());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.MEMBER_REGISTER));
	}

	@DeleteMapping(path = "/member/withdraw")
	public ResponseEntity<SuccessResponse<Void>> withdraw(
		@AuthenticationPrincipal Long memberId,
		@RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessTokenWithBearer,
		@Valid @RequestBody WithdrawRequest request
	) {
		String accessTokenHash = TokenHasher.hash(TokenExtractor.extract(accessTokenWithBearer));
		String refreshTokenHash = TokenHasher.hash(request.refreshToken());

		memberService.withdraw(memberId, accessTokenHash, refreshTokenHash);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.MEMBER_WITHDRAW));
	}

	@GetMapping(path = "/member/summary")
	public ResponseEntity<SuccessResponse<MemberInfoResponse>> getMemberInfo(
		@AuthenticationPrincipal Long memberId,
		@RequestHeader(name = "Time-Zone") ZoneId timeZone
	) {
		MemberInfoResult result = memberService.getMemberInfo(memberId, timeZone);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.MEMBER_FOUND, MemberInfoResponse.from(result)));
	}

	@GetMapping(path = "/member/profile")
	public ResponseEntity<SuccessResponse<MemberProfileResponse>> getMemberProfile(
		@AuthenticationPrincipal Long memberId
	) {
		MemberProfileResult result = memberService.getMemberProfile(memberId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.MEMBER_FOUND, MemberProfileResponse.from(result)));
	}

	@PatchMapping(path = "/member/nickname")
	public ResponseEntity<SuccessResponse<NicknameResponse>> updateNickname(
		@AuthenticationPrincipal Long memberId,
		@Valid @RequestBody NicknameUpdateRequest request
	) {
		UpdateNicknameResult result = memberService.updateNickname(request.toCommand(memberId));

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.MEMBER_NICKNAME_UPDATED, NicknameResponse.from(result)));
	}

	@PostMapping(path = "/member/profile-image/upload-url")
	public ResponseEntity<SuccessResponse<ProfileImageUploadUrlResponse>> generateProfileImageUploadUrl(
		@AuthenticationPrincipal Long memberId,
		@Valid @RequestBody ProfileImageUploadUrlRequest request
	) {
		ProfileImageUploadUrlResult result = memberService.generateProfileImageUploadUrl(request.toCommand(memberId));

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.PROFILE_IMAGE_UPLOAD_URL_ISSUED, ProfileImageUploadUrlResponse.from(result)));
	}

	@PatchMapping(path = "/member/profile-image")
	public ResponseEntity<SuccessResponse<ProfileImageResponse>> updateProfileImage(
		@AuthenticationPrincipal Long memberId,
		@Valid @RequestBody ProfileImageUpdateRequest request
	) {
		UpdateProfileImageResult result = memberService.updateProfileImage(request.toCommand(memberId));

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.PROFILE_IMAGE_UPDATED, ProfileImageResponse.from(result)));
	}

	@GetMapping(path = "/archive/activity-summary")
	public ResponseEntity<SuccessResponse<ActivitySummaryResponse>> getActivitySummary(
		@AuthenticationPrincipal Long memberId,
		@RequestParam(name = "year", required = true) Integer year,
		@Min(1) @Max(12) @RequestParam(name = "month", required = true) Integer month
	) {
		ActivitySummaryResult result = memberService.getActivitySummary(memberId, year, month);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success(SuccessCode.ACTIVITY_SUMMARY_FOUND, ActivitySummaryResponse.from(result)));
	}
}
