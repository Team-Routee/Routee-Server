package org.sopt.routee.activity.internal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.routee.activity.internal.entity.timeline.Timeline;
import org.sopt.routee.activity.internal.event.TimelineDeletedEvent;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.exception.TimelineNotFoundException;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.repository.TimelineRepository;
import org.sopt.routee.external.api.port.FileImageAccessUrlPort;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class TimelineServiceTest {

	@Mock
	private ActivityRepository activityRepository;

	@Mock
	private TimelineRepository timelineRepository;

	@Mock
	private FileImageAccessUrlPort fileImageAccessUrlPort;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@InjectMocks
	private TimelineService timelineService;

	@Test
	void delete_타임라인을_삭제하면_이미지_키를_담은_삭제_이벤트를_발행한다() {
		Long activityId = 1L;
		Long timelineId = 10L;
		Long memberId = 100L;
		String objectKey = "timeline-image.jpg";

		Timeline timeline = Timeline.builder()
			.id(timelineId)
			.timelineImageObjectKey(objectKey)
			.build();

		when(activityRepository.existsByIdAndMemberId(activityId, memberId)).thenReturn(true);
		when(timelineRepository.findByIdAndActivityId(timelineId, activityId)).thenReturn(Optional.of(timeline));

		timelineService.delete(activityId, timelineId, memberId);

		verify(timelineRepository).delete(timeline);

		ArgumentCaptor<TimelineDeletedEvent> eventCaptor = ArgumentCaptor.forClass(TimelineDeletedEvent.class);
		verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

		TimelineDeletedEvent event = eventCaptor.getValue();
		assertThat(event.activityId()).isEqualTo(activityId);
		assertThat(event.timelineImageObjectKey()).isEqualTo(objectKey);
	}

	@Test
	void delete_활동이_없으면_예외를_던지고_이벤트를_발행하지_않는다() {
		Long activityId = 1L;
		Long timelineId = 10L;
		Long memberId = 100L;

		when(activityRepository.existsByIdAndMemberId(activityId, memberId)).thenReturn(false);

		assertThatThrownBy(() -> timelineService.delete(activityId, timelineId, memberId))
			.isInstanceOf(ActivityNotFoundException.class);

		verify(timelineRepository, never()).delete(any());
		verify(applicationEventPublisher, never()).publishEvent(any());
	}

	@Test
	void delete_타임라인이_없으면_예외를_던지고_이벤트를_발행하지_않는다() {
		Long activityId = 1L;
		Long timelineId = 10L;
		Long memberId = 100L;

		when(activityRepository.existsByIdAndMemberId(activityId, memberId)).thenReturn(true);
		when(timelineRepository.findByIdAndActivityId(timelineId, activityId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> timelineService.delete(activityId, timelineId, memberId))
			.isInstanceOf(TimelineNotFoundException.class);

		verify(timelineRepository, never()).delete(any());
		verify(applicationEventPublisher, never()).publishEvent(any());
	}
}
