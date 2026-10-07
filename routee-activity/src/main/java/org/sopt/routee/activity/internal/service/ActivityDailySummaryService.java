package org.sopt.routee.activity.internal.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.entity.summary.ActivityDailySummary;
import org.sopt.routee.activity.internal.mapper.ActivityDailySummaryMapper;
import org.sopt.routee.activity.internal.repository.ActivityDailySummaryRepository;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.service.dto.result.ActivityDailySummaryResult;
import org.sopt.routee.external.api.command.FileImageAccessUrlCommand;
import org.sopt.routee.external.api.port.FileImageAccessUrlPort;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.sopt.routee.external.api.type.FileUploadImageSize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityDailySummaryService {

	private final ActivityDailySummaryRepository activityDailySummaryRepository;
	private final ActivityRepository activityRepository;
	private final FileImageAccessUrlPort fileImageAccessUrlPort;

	@Transactional(readOnly = true)
	public List<ActivityDailySummaryResult> getMonthlySummaries(Long memberId, YearMonth yearMonth) {
		return activityDailySummaryRepository.findByMemberIdAndActivityDateBetweenOrderByActivityDateAsc(
				memberId, yearMonth.atDay(1), yearMonth.atEndOfMonth()
			)
			.stream()
			.map(summary -> ActivityDailySummaryMapper.toResult(summary, generateCoverImageUrl(memberId, summary)))
			.toList();
	}

	@Transactional
	public void recordActivity(
		Long memberId, LocalDate activityDate, Integer durationSec, Long coverActivityId, String coverImageObjectKey
	) {
		activityDailySummaryRepository.upsertDailySummary(
			TSID.Factory.getTsid().toLong(), memberId, activityDate, durationSec, coverActivityId, coverImageObjectKey
		);
	}

	@Transactional
	public void deleteActivityDailySummariesByMemberId(long memberId) {
		activityDailySummaryRepository.deleteByMemberId(memberId);
	}

	@Transactional
	public void refreshCoverAfterActivityChanged(Long memberId, LocalDate activityDate, Long previousCoverActivityId) {
		Optional<Activity> replacementCover = activityRepository
			.findFirstByMemberIdAndActivityDateWithTimezoneAndActivityStatusAndCoverImageObjectKeyIsNotNullOrderByStartedAtAsc(
				memberId, activityDate, ActivityStatus.ACTIVITY_COMPLETED);

		activityDailySummaryRepository.updateCoverImage(
			memberId, activityDate,
			replacementCover.map(Activity::getId).orElse(null),
			replacementCover.map(Activity::getCoverImageObjectKey).orElse(null),
			previousCoverActivityId);
	}

	@Transactional
	public void removeActivity(Long memberId, LocalDate activityDate, Integer durationSec) {
		activityDailySummaryRepository.decrementDailySummary(memberId, activityDate, durationSec);
		activityDailySummaryRepository.deleteIfEmpty(memberId, activityDate);
	}

	private String generateCoverImageUrl(Long memberId, ActivityDailySummary summary) {
		if (summary.getCoverImageObjectKey() == null) {
			return null;
		}

		FileImageAccessUrlCommand command = new FileImageAccessUrlCommand(
			FileUploadDirectory.TIMELINE,
			FileUploadImageSize.SMALL,
			memberId.toString(),
			summary.getCoverActivityId().toString(),
			summary.getCoverImageObjectKey()
		);
		return fileImageAccessUrlPort.generateImageUrl(command).imageUrl();
	}
}
