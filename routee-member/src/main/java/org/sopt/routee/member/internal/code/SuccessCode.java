package org.sopt.routee.member.internal.code;

import org.sopt.routee.code.SuccessResultCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements SuccessResultCode {
	MEMBER_FOUND(HttpStatus.OK, "사용자 정보 조회에 성공하였습니다."),
	MEMBER_REGISTER(HttpStatus.CREATED, "회원가입이 완료되었습니다."),
	MEMBER_WITHDRAW(HttpStatus.OK, "회원 탈퇴가 완료되었습니다."),
	MEMBER_NICKNAME_UPDATED(HttpStatus.OK, "닉네임 변경이 완료되었습니다."),
	PROFILE_IMAGE_UPLOAD_URL_ISSUED(HttpStatus.OK, "프로필 이미지 업로드 URL 발급에 성공했습니다."),
	PROFILE_IMAGE_UPDATED(HttpStatus.OK, "프로필 이미지 변경이 완료되었습니다."),
	PROFILE_IMAGE_RESET(HttpStatus.OK, "프로필 이미지가 기본 이미지로 변경되었습니다."),
	ACTIVITY_SUMMARY_FOUND(HttpStatus.OK, "월간 활동 요약 조회에 성공하였습니다.");

	private final HttpStatus status;
	private final String message;
}