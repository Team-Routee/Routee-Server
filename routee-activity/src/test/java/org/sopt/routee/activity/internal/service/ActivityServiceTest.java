package org.sopt.routee.activity.internal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.EnumSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.routee.activity.internal.entity.activity.Activity;
import org.sopt.routee.activity.internal.entity.activity.ActivityStatus;
import org.sopt.routee.activity.internal.entity.activity.ActivityType;
import org.sopt.routee.activity.internal.exception.AlreadyInProgressActivityException;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.service.dto.command.CreateActivityCommand;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

	private static final EnumSet<ActivityStatus> ACTIVE_STATUSES = EnumSet.of(
		ActivityStatus.ACTIVITY_IN_PROGRESS,
		ActivityStatus.ACTIVITY_PAUSED
	);

	@Mock
	private ActivityRepository activityRepository;

	@InjectMocks
	private ActivityService activityService;

	@Test
	void create_진행중인_활동이_없으면_요청한_활동_유형으로_활동을_생성한다() {
		Long memberId = 1L;
		Activity savedActivity = Activity.builder()
			.id(10L)
			.title("2026.07.06 기록")
			.activityType(ActivityType.RUNNING)
			.activityStatus(ActivityStatus.ACTIVITY_IN_PROGRESS)
			.memberId(memberId)
			.build();

		when(activityRepository.existsByMemberIdAndActivityStatusIn(memberId, ACTIVE_STATUSES))
			.thenReturn(false);
		when(activityRepository.save(any(Activity.class)))
			.thenReturn(savedActivity);

		Instant beforeCreate = Instant.now();
		Long activityId = activityService.create(new CreateActivityCommand(memberId, ActivityType.RUNNING));
		Instant afterCreate = Instant.now();

		assertThat(activityId).isEqualTo(10L);

		ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
		verify(activityRepository).save(activityCaptor.capture());
		Activity activity = activityCaptor.getValue();
		assertThat(activity.getTitle()).matches("\\d{4}\\.\\d{2}\\.\\d{2} 기록");
		assertThat(activity.getStartedAt()).isBetween(beforeCreate, afterCreate);
		assertThat(activity.getActivityType()).isEqualTo(ActivityType.RUNNING);
		assertThat(activity.getActivityStatus()).isEqualTo(ActivityStatus.ACTIVITY_IN_PROGRESS);
		assertThat(activity.getMemberId()).isEqualTo(memberId);
	}

	@Test
	void create_진행중이거나_일시정지된_활동이_있으면_예외를_던진다() {
		Long memberId = 1L;
		when(activityRepository.existsByMemberIdAndActivityStatusIn(memberId, ACTIVE_STATUSES))
			.thenReturn(true);

		assertThatThrownBy(() -> activityService.create(new CreateActivityCommand(memberId, ActivityType.HIKING)))
			.isInstanceOf(AlreadyInProgressActivityException.class);

		verify(activityRepository).existsByMemberIdAndActivityStatusIn(memberId, ACTIVE_STATUSES);
		assertThat(ACTIVE_STATUSES).containsExactlyInAnyOrder(
			ActivityStatus.ACTIVITY_IN_PROGRESS,
			ActivityStatus.ACTIVITY_PAUSED
		);

		verify(activityRepository, never()).save(any(Activity.class));
	}
}
