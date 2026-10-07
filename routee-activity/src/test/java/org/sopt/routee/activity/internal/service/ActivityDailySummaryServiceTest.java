package org.sopt.routee.activity.internal.service;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.repository.ActivityDailySummaryRepository;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.external.api.port.FileImageAccessUrlPort;

@ExtendWith(MockitoExtension.class)
class ActivityDailySummaryServiceTest {

	private static final Long MEMBER_ID = 1L;
	private static final LocalDate ACTIVITY_DATE = LocalDate.of(2026, 7, 7);
	private static final Long DELETED_COVER_ACTIVITY_ID = 10L;

	@Mock
	private ActivityDailySummaryRepository activityDailySummaryRepository;

	@Mock
	private ActivityRepository activityRepository;

	@Mock
	private FileImageAccessUrlPort fileImageAccessUrlPort;

	private ActivityDailySummaryService activityDailySummaryService;

	@BeforeEach
	void setUp() {
		activityDailySummaryService = new ActivityDailySummaryService(
			activityDailySummaryRepository,
			activityRepository,
			fileImageAccessUrlPort
		);
	}

	@Test
	void refreshCoverAfterActivityChanged_대표_커버_삭제_후_남은_활동에_이미지가_있으면_다음_활동을_새_커버로_갱신한다() {
		Activity nextActivity = Activity.builder()
			.id(20L)
			.coverImageObjectKey("next-cover.jpg")
			.build();
		when(activityRepository
			.findFirstByMemberIdAndActivityDateWithTimezoneAndActivityStatusAndCoverImageObjectKeyIsNotNullOrderByStartedAtAsc(
				MEMBER_ID, ACTIVITY_DATE, ActivityStatus.ACTIVITY_COMPLETED))
			.thenReturn(Optional.of(nextActivity));

		activityDailySummaryService.refreshCoverAfterActivityChanged(MEMBER_ID, ACTIVITY_DATE, DELETED_COVER_ACTIVITY_ID);

		verify(activityDailySummaryRepository).updateCoverImage(
			MEMBER_ID, ACTIVITY_DATE, 20L, "next-cover.jpg", DELETED_COVER_ACTIVITY_ID);
	}

	@Test
	void refreshCoverAfterActivityChanged_대표_커버_삭제_후_남은_활동에_이미지가_없으면_커버를_null로_갱신한다() {
		when(activityRepository
			.findFirstByMemberIdAndActivityDateWithTimezoneAndActivityStatusAndCoverImageObjectKeyIsNotNullOrderByStartedAtAsc(
				MEMBER_ID, ACTIVITY_DATE, ActivityStatus.ACTIVITY_COMPLETED))
			.thenReturn(Optional.empty());

		activityDailySummaryService.refreshCoverAfterActivityChanged(MEMBER_ID, ACTIVITY_DATE, DELETED_COVER_ACTIVITY_ID);

		verify(activityDailySummaryRepository).updateCoverImage(
			MEMBER_ID, ACTIVITY_DATE, null, null, DELETED_COVER_ACTIVITY_ID);
	}
}
