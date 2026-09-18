package org.sopt.routee.activity.internal.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.sopt.routee.activity.internal.entity.summary.ActivityDailySummary;
import org.sopt.routee.activity.internal.mapper.ActivityDailySummaryMapper;
import org.sopt.routee.activity.internal.repository.ActivityDailySummaryRepository;
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
	public void refreshCoverImage(Long memberId, LocalDate activityDate, Long coverActivityId, String coverImageObjectKey) {
		activityDailySummaryRepository.updateCoverImage(memberId, activityDate, coverActivityId, coverImageObjectKey);
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
