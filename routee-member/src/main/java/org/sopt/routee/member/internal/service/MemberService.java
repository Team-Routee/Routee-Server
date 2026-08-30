package org.sopt.routee.member.internal.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import org.sopt.routee.activity.api.result.MonthlyActivityDailySummaryResult;
import org.sopt.routee.activity.api.usecase.ActivityUseCase;
import org.sopt.routee.exception.BaseException;
import org.sopt.routee.external.api.command.FileDeleteCommand;
import org.sopt.routee.external.api.command.FileDeleteDirectoryCommand;
import org.sopt.routee.external.api.command.FileImageAccessUrlCommand;
import org.sopt.routee.external.api.command.FileUploadPresignCommand;
import org.sopt.routee.external.api.port.FileDeletePort;
import org.sopt.routee.external.api.port.FileImageAccessUrlPort;
import org.sopt.routee.external.api.port.FileUploadPresignPort;
import org.sopt.routee.external.api.result.FileUploadPresignResult;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.OAuthProvider;
import org.sopt.routee.external.api.port.OidcVerifyPort;
import org.sopt.routee.member.api.event.MemberWithdrawnEvent;
import org.sopt.routee.member.internal.service.dto.command.AgreementCommand;
import org.sopt.routee.member.internal.service.dto.command.ProfileImageUploadUrlCommand;
import org.sopt.routee.member.internal.service.dto.command.RegisterCommand;
import org.sopt.routee.member.internal.service.dto.command.UpdateNicknameCommand;
import org.sopt.routee.member.internal.service.dto.command.UpdateProfileImageCommand;
import org.sopt.routee.member.internal.service.dto.result.ActivitySummaryResult;
import org.sopt.routee.member.internal.service.dto.result.MemberInfoResult;
import org.sopt.routee.member.internal.service.dto.result.MemberProfileResult;
import org.sopt.routee.member.internal.service.dto.result.ProfileImageUploadUrlResult;
import org.sopt.routee.member.internal.service.dto.result.UpdateNicknameResult;
import org.sopt.routee.member.internal.service.dto.result.UpdateProfileImageResult;
import org.sopt.routee.member.api.result.TokenClaimsResult;
import org.sopt.routee.member.internal.entity.Member;
import org.sopt.routee.member.internal.exception.AlreadyRegisteredMemberException;
import org.sopt.routee.member.internal.exception.MemberNotFoundException;
import org.sopt.routee.member.internal.exception.RequiredAgreementNotAcceptedException;
import org.sopt.routee.member.internal.exception.UnsupportedImageFileExtensionException;
import org.sopt.routee.member.internal.mapper.MemberMapper;
import org.sopt.routee.member.internal.repository.MemberAgreementRepository;
import org.sopt.routee.member.internal.repository.MemberRepository;
import org.sopt.routee.member.internal.service.validator.ProfileImageFileNameValidator;
import org.sopt.routee.util.TimeZoneUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final OidcVerifyPort oidcVerifyPort;
	private final ActivityUseCase activityUseCase;
	private final MemberRepository memberRepository;
	private final MemberAgreementRepository memberAgreementRepository;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final FileUploadPresignPort fileUploadPresignPort;
	private final FileImageAccessUrlPort fileImageAccessUrlPort;
	private final FileDeletePort fileDeletePort;
	private final ProfileImageFileNameValidator profileImageFileNameValidator;
	private final TransactionTemplate transactionTemplate;

	@Transactional(readOnly = true)
	public TokenClaimsResult getTokenResult(String oauthId, OAuthProvider oauthProvider) {
		Member member = memberRepository.findByOauthIdAndOauthProvider(oauthId, oauthProvider)
			.orElseThrow(MemberNotFoundException::new);

		return MemberMapper.toTokenClaimsResult(member);
	}

	@Transactional(readOnly = true)
	public boolean existsById(long memberId) {
		return memberRepository.existsById(memberId);
	}

	@Transactional
	public void register(RegisterCommand command) {
		validateRequiredAgreements(command.agreement());

		String oauthId = oidcVerifyPort.extractSubject(command.provider(), command.idToken());

		if (memberRepository.existsByOauthIdAndOauthProvider(oauthId, command.provider())) {
			throw new AlreadyRegisteredMemberException();
		}

		Member savedMember = memberRepository.save(MemberMapper.toEntity(command, oauthId));

		Instant agreedAt = TimeZoneUtils.toUtcInstantTime(LocalDateTime.now(command.timeZone()), command.timeZone());
		memberAgreementRepository.save(MemberMapper.toAgreementEntity(savedMember, command.agreement(), agreedAt));
	}

	private void validateRequiredAgreements(AgreementCommand agreement) {
		boolean allRequiredAccepted = agreement.serviceTerms()
			&& agreement.privacyPolicy()
			&& agreement.locationServiceTerms()
			&& agreement.over14();

		if (!allRequiredAccepted) {
			throw new RequiredAgreementNotAcceptedException();
		}
	}

	public void withdraw(long memberId, String accessTokenHash, String refreshTokenHash) {
		transactionTemplate.executeWithoutResult(status -> {
			Member member = memberRepository.findById(memberId)
				.orElseThrow(MemberNotFoundException::new);

			memberAgreementRepository.deleteByMember_Id(memberId);
			memberRepository.delete(member);

			activityUseCase.deleteForMemberWithdrawal(memberId);

			applicationEventPublisher.publishEvent(new MemberWithdrawnEvent(memberId, accessTokenHash, refreshTokenHash));
		});

		Thread.startVirtualThread(() -> deleteMemberImages(memberId));
	}

	private void deleteMemberImages(long memberId) {
		try {
			fileDeletePort.deleteDirectory(new FileDeleteDirectoryCommand(Long.toString(memberId)));
		} catch (BaseException e) {
			log.warn("Member image directory delete failed. memberId={}", memberId, e);
		}
	}

	@Transactional(readOnly = true)
	public MemberInfoResult getMemberInfo(long memberId, ZoneId zoneId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(MemberNotFoundException::new);

		return MemberMapper.toMemberInfoResult(
			member, generateProfileImageUrl(member.getId(), member.getProfileImageObjectKey()), zoneId);
	}

	@Transactional(readOnly = true)
	public MemberProfileResult getMemberProfile(long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(MemberNotFoundException::new);

		return MemberMapper.toMemberProfileResult(
			member, generateProfileImageUrl(member.getId(), member.getProfileImageObjectKey()));
	}

	@Transactional
	public UpdateNicknameResult updateNickname(UpdateNicknameCommand command) {
		Member member = memberRepository.findById(command.memberId())
			.orElseThrow(MemberNotFoundException::new);

		member.updateNickname(command.nickname());
		return MemberMapper.toUpdateNicknameResult(member);
	}

	public ProfileImageUploadUrlResult generateProfileImageUploadUrl(ProfileImageUploadUrlCommand command) {
		if (!profileImageFileNameValidator.validate(command.fileName())) {
			throw new UnsupportedImageFileExtensionException();
		}

		FileUploadPresignCommand presignCommand = new FileUploadPresignCommand(
			FileUploadDirectory.PROFILE,
			null,
			command.memberId().toString(),
			command.fileName()
		);
		FileUploadPresignResult result = fileUploadPresignPort.generatePutPresignedUrl(presignCommand);

		return new ProfileImageUploadUrlResult(result.presignedUrl(), result.objectKey());
	}

	public UpdateProfileImageResult updateProfileImage(UpdateProfileImageCommand command) {
		String previousObjectKey = transactionTemplate.execute(status -> {
			Member member = memberRepository.findById(command.memberId())
				.orElseThrow(MemberNotFoundException::new);

			String objectKey = member.getProfileImageObjectKey();
			member.updateProfileImageObjectKey(command.objectKey());
			return objectKey;
		});

		if (previousObjectKey != null && !previousObjectKey.equals(command.objectKey())) {
			Thread.startVirtualThread(() -> deleteProfileImage(command.memberId(), previousObjectKey));
		}

		return MemberMapper.toUpdateProfileImageResult(generateProfileImageUrl(command.memberId(), command.objectKey()));
	}

	private void deleteProfileImage(Long memberId, String objectKey) {
		try {
			fileDeletePort.deleteImage(new FileDeleteCommand(FileUploadDirectory.PROFILE, memberId.toString(), objectKey));
		} catch (BaseException e) {
			log.warn("Profile image delete failed. memberId={}, objectKey={}", memberId, objectKey, e);
		}
	}

	private String generateProfileImageUrl(Long memberId, String profileImageObjectKey) {
		if (profileImageObjectKey == null) {
			return null;
		}

		FileImageAccessUrlCommand accessUrlCommand = new FileImageAccessUrlCommand(
			FileUploadDirectory.PROFILE,
			null,
			memberId.toString(),
			profileImageObjectKey
		);
		return fileImageAccessUrlPort.generateImageUrl(accessUrlCommand).imageUrl();
	}

	@Transactional
	public void incrementTotalActivityCount(long memberId) {
		memberRepository.incrementTotalActivityCount(memberId);
	}

	@Transactional(readOnly = true)
	public ActivitySummaryResult getActivitySummary(long memberId, int year, int month) {
		if (!memberRepository.existsById(memberId)) {
			throw new MemberNotFoundException();
		}

		List<MonthlyActivityDailySummaryResult> summaries =
			activityUseCase.getMonthlySummaries(memberId, YearMonth.of(year, month));

		return MemberMapper.toActivitySummaryResult(summaries, year, month);
	}
}
