package org.sopt.routee.activity.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

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

	@Mock
	private ActivityRepository activityRepository;

	@InjectMocks
	private ActivityService activityService;

	@Test
	void create_진행중인_활동이_없으면_기본값으로_활동을_생성한다() {
		Long memberId = 1L;
		Activity savedActivity = Activity.builder()
			.id(10L)
			.title("2026.07.06 기록")
			.activityType(ActivityType.HIKING)
			.activityStatus(ActivityStatus.ACTIVITY_IN_PROGRESS)
			.memberId(memberId)
			.build();

		when(activityRepository.existsByMemberIdAndActivityStatus(memberId, ActivityStatus.ACTIVITY_IN_PROGRESS))
			.thenReturn(false);
		when(activityRepository.save(any(Activity.class)))
			.thenReturn(savedActivity);

		Instant beforeCreate = Instant.now();
		Long activityId = activityService.create(new CreateActivityCommand(memberId));
		Instant afterCreate = Instant.now();

		assertThat(activityId).isEqualTo(10L);

		ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
		verify(activityRepository).save(activityCaptor.capture());
		Activity activity = activityCaptor.getValue();
		assertThat(activity.getTitle()).matches("\\d{4}\\.\\d{2}\\.\\d{2} 기록");
		assertThat(activity.getStartedAt()).isBetween(beforeCreate, afterCreate);
		assertThat(activity.getActivityType()).isEqualTo(ActivityType.HIKING);
		assertThat(activity.getActivityStatus()).isEqualTo(ActivityStatus.ACTIVITY_IN_PROGRESS);
		assertThat(activity.getMemberId()).isEqualTo(memberId);
	}

	@Test
	void create_진행중인_활동이_있으면_예외를_던진다() {
		Long memberId = 1L;
		when(activityRepository.existsByMemberIdAndActivityStatus(memberId, ActivityStatus.ACTIVITY_IN_PROGRESS))
			.thenReturn(true);

		assertThatThrownBy(() -> activityService.create(new CreateActivityCommand(memberId)))
			.isInstanceOf(AlreadyInProgressActivityException.class);

		verify(activityRepository, never()).save(any(Activity.class));
	}
}
