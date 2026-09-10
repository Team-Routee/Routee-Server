package org.sopt.routee.member.internal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.function.Consumer;

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
import org.sopt.routee.external.api.port.OidcVerifyPort;
import org.sopt.routee.member.api.event.MemberWithdrawnEvent;
import org.sopt.routee.member.internal.entity.Member;
import org.sopt.routee.member.internal.exception.MemberNotFoundException;
import org.sopt.routee.member.internal.repository.MemberAgreementRepository;
import org.sopt.routee.member.internal.repository.MemberRepository;
import org.sopt.routee.member.internal.service.validator.ProfileImageFileNameValidator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	private static final long MEMBER_ID = 1L;
	private static final String ACCESS_TOKEN_HASH = "access-hash";
	private static final String REFRESH_TOKEN_HASH = "refresh-hash";

	@Mock
	private OidcVerifyPort oidcVerifyPort;

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
		doAnswer(invocation -> {
			Consumer<TransactionStatus> callback = invocation.getArgument(0);
			callback.accept(mock(TransactionStatus.class));
			return null;
		}).when(transactionTemplate).executeWithoutResult(any(Consumer.class));
	}

	@Test
	@DisplayName("withdraw: 정상적으로 회원을 탈퇴시키고 토큰 무효화 이벤트를 발행한다")
	void withdraw_success_publishesMemberWithdrawnEvent() {
		// given
		stubTransactionTemplateToRunCallback();
		Member member = mock(Member.class);
		when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));

		// when
		memberService.withdraw(MEMBER_ID, ACCESS_TOKEN_HASH, REFRESH_TOKEN_HASH);

		// then
		verify(memberAgreementRepository).deleteByMember_Id(MEMBER_ID);
		verify(memberRepository).delete(member);
		verify(activityUseCase).deleteForMemberWithdrawal(MEMBER_ID);

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
			() -> memberService.withdraw(MEMBER_ID, ACCESS_TOKEN_HASH, REFRESH_TOKEN_HASH));

		// then
		assertThat(thrown).isInstanceOf(MemberNotFoundException.class);
		verify(activityUseCase, never()).deleteForMemberWithdrawal(anyLong());
		verify(applicationEventPublisher, never()).publishEvent(any());
	}
}
