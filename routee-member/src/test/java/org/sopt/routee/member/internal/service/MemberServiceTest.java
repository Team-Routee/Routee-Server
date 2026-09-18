package org.sopt.routee.member.internal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.routee.activity.api.usecase.ActivityUseCase;
import org.sopt.routee.external.api.port.FileDeletePort;
import org.sopt.routee.external.api.port.FileImageAccessUrlPort;
import org.sopt.routee.external.api.port.FileUploadPresignPort;
import org.sopt.routee.external.api.port.OAuthRevokePort;
import org.sopt.routee.external.api.port.OidcVerifyPort;
import org.sopt.routee.external.api.type.OAuthProvider;
import org.sopt.routee.member.api.event.MemberWithdrawnEvent;
import org.sopt.routee.member.internal.entity.Member;
import org.sopt.routee.member.internal.exception.MemberNotFoundException;
import org.sopt.routee.member.internal.repository.MemberAgreementRepository;
import org.sopt.routee.member.internal.repository.MemberRepository;
import org.sopt.routee.member.internal.service.dto.command.WithdrawCommand;
import org.sopt.routee.member.internal.service.validator.ProfileImageFileNameValidator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	private static final long MEMBER_ID = 1L;
	private static final String ACCESS_TOKEN_HASH = "access-hash";
	private static final String REFRESH_TOKEN_HASH = "refresh-hash";
	private static final String AUTHORIZATION_CODE = "auth-code";
	private static final WithdrawCommand WITHDRAW_COMMAND =
		new WithdrawCommand(MEMBER_ID, ACCESS_TOKEN_HASH, REFRESH_TOKEN_HASH, AUTHORIZATION_CODE);

	@Mock
	private OidcVerifyPort oidcVerifyPort;

	@Mock
	private OAuthRevokePort oAuthRevokePort;

	@Mock
	private ActivityUseCase activityUseCase;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private MemberAgreementRepository memberAgreementRepository;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@Mock
	private FileUploadPresignPort fileUploadPresignPort;

	@Mock
	private FileImageAccessUrlPort fileImageAccessUrlPort;

	@Mock
	private FileDeletePort fileDeletePort;

	@Mock
	private ProfileImageFileNameValidator profileImageFileNameValidator;

	@Mock
	private TransactionTemplate transactionTemplate;

	private MemberService memberService;

	@BeforeEach
	void setUp() {
		memberService = new MemberService(
			oidcVerifyPort,
			oAuthRevokePort,
			activityUseCase,
			memberRepository,
			memberAgreementRepository,
			applicationEventPublisher,
			fileUploadPresignPort,
			fileImageAccessUrlPort,
			fileDeletePort,
			profileImageFileNameValidator,
			transactionTemplate
		);
	}

	@SuppressWarnings("unchecked")
	private void stubTransactionTemplateToRunCallback() {
		when(transactionTemplate.execute(any(TransactionCallback.class))).thenAnswer(invocation -> {
			TransactionCallback<Object> callback = invocation.getArgument(0);
			return callback.doInTransaction(mock(TransactionStatus.class));
		});
	}

	@Test
	@DisplayName("withdraw: 회원을 탈퇴시키고 소셜 연동을 해제한 뒤 토큰 무효화 이벤트를 발행한다")
	void withdraw_success_revokesOAuthAndPublishesEvent() {
		// given
		stubTransactionTemplateToRunCallback();
		Member member = mock(Member.class);
		when(member.getOauthProvider()).thenReturn(OAuthProvider.APPLE);
		when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));

		// when
		memberService.withdraw(WITHDRAW_COMMAND);

		// then
		verify(memberAgreementRepository).deleteByMember_Id(MEMBER_ID);
		verify(memberRepository).delete(member);
		verify(activityUseCase).deleteForMemberWithdrawal(MEMBER_ID);
		verify(oAuthRevokePort).revoke(AUTHORIZATION_CODE);

		ArgumentCaptor<MemberWithdrawnEvent> eventCaptor = ArgumentCaptor.forClass(MemberWithdrawnEvent.class);
		verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
		assertThat(eventCaptor.getValue())
			.extracting(
				MemberWithdrawnEvent::memberId,
				MemberWithdrawnEvent::accessTokenHash,
				MemberWithdrawnEvent::refreshTokenHash)
			.containsExactly(MEMBER_ID, ACCESS_TOKEN_HASH, REFRESH_TOKEN_HASH);
	}

	@Test
	@DisplayName("withdraw: 소셜 연동 해제가 실패해도 회원 탈퇴와 이벤트 발행은 정상 처리된다")
	void withdraw_oauthRevokeFails_stillCompletesWithdrawal() {
		// given
		stubTransactionTemplateToRunCallback();
		Member member = mock(Member.class);
		when(member.getOauthProvider()).thenReturn(OAuthProvider.APPLE);
		when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
		// 임의의 BaseException — revoke 어댑터가 던지는 예외를 흉내낸다
		doThrow(new MemberNotFoundException()).when(oAuthRevokePort).revoke(AUTHORIZATION_CODE);

		// when
		memberService.withdraw(WITHDRAW_COMMAND);

		// then
		verify(memberRepository).delete(member);
		verify(activityUseCase).deleteForMemberWithdrawal(MEMBER_ID);
		verify(applicationEventPublisher).publishEvent(any(MemberWithdrawnEvent.class));
	}

	@Test
	@DisplayName("withdraw: Apple이 아닌 provider는 OAuth revoke를 호출하지 않는다")
	void withdraw_nonAppleProvider_doesNotCallOAuthRevoke() {
		// given
		stubTransactionTemplateToRunCallback();
		Member member = mock(Member.class);
		when(member.getOauthProvider()).thenReturn(OAuthProvider.GOOGLE);
		when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));

		// when
		memberService.withdraw(WITHDRAW_COMMAND);

		// then
		verify(memberRepository).delete(member);
		verify(activityUseCase).deleteForMemberWithdrawal(MEMBER_ID);
		verify(oAuthRevokePort, never()).revoke(any());
		verify(applicationEventPublisher).publishEvent(any(MemberWithdrawnEvent.class));
	}

	@Test
	@DisplayName("withdraw: 동시 탈퇴 요청으로 낙관적 락 예외가 발생하면 회원 없음 예외로 변환한다")
	void withdraw_concurrentWithdrawal_throwsMemberNotFoundException() {
		// given
		stubTransactionTemplateToRunCallback();
		Member member = mock(Member.class);
		when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
		doThrow(new ObjectOptimisticLockingFailureException(Member.class, MEMBER_ID))
			.when(memberAgreementRepository).deleteByMember_Id(MEMBER_ID);

		// when
		Throwable thrown = catchThrowable(
			() -> memberService.withdraw(WITHDRAW_COMMAND));

		// then
		assertThat(thrown).isInstanceOf(MemberNotFoundException.class);
		verify(activityUseCase, never()).deleteForMemberWithdrawal(anyLong());
		verify(oAuthRevokePort, never()).revoke(any());
		verify(applicationEventPublisher, never()).publishEvent(any());
	}
}
