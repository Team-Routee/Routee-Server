package org.sopt.routee.activity.internal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.routee.activity.internal.entity.timeline.Timeline;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.activity.internal.exception.TimelineNotFoundException;
import org.sopt.routee.activity.internal.repository.ActivityRepository;
import org.sopt.routee.activity.internal.repository.TimelineRepository;
import org.sopt.routee.activity.internal.service.dto.command.UpdateTimelineTitleCommand;
import org.sopt.routee.activity.internal.service.dto.result.UpdateTimelineTitleResult;
import org.sopt.routee.external.api.command.FileDeleteCommand;
import org.sopt.routee.external.api.port.FileDeletePort;
import org.sopt.routee.external.api.port.FileImageAccessUrlPort;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class TimelineServiceTest {

	@Mock
	private ActivityRepository activityRepository;

	@Mock
	private TimelineRepository timelineRepository;

	@Mock
	private FileImageAccessUrlPort fileImageAccessUrlPort;

	@Mock
	private FileDeletePort fileDeletePort;

	@Mock
	private TransactionTemplate transactionTemplate;

	@InjectMocks
	private TimelineService timelineService;

	private void stubTransactionTemplateToRunCallback() {
		when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
			TransactionCallback<Timeline> callback = invocation.getArgument(0);
			return callback.doInTransaction(mock(TransactionStatus.class));
		});
	}

	@Test
	void delete_타임라인을_삭제하면_이미지_키로_S3_삭제를_요청한다() throws InterruptedException {
		Long activityId = 1L;
		Long timelineId = 10L;
		Long memberId = 100L;
		String objectKey = "timeline-image.jpg";

		Timeline timeline = Timeline.builder()
			.id(timelineId)
			.timelineImageObjectKey(objectKey)
			.build();

		when(timelineRepository.findByIdAndActivity_IdAndActivity_MemberId(timelineId, activityId, memberId))
			.thenReturn(Optional.of(timeline));
		stubTransactionTemplateToRunCallback();

		CountDownLatch latch = new CountDownLatch(1);
		doAnswer(invocation -> {
			latch.countDown();
			return null;
		}).when(fileDeletePort).deleteImage(any());

		timelineService.delete(activityId, timelineId, memberId);

		verify(timelineRepository).delete(timeline);
		assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();

		ArgumentCaptor<FileDeleteCommand> commandCaptor = ArgumentCaptor.forClass(FileDeleteCommand.class);
		verify(fileDeletePort).deleteImage(commandCaptor.capture());

		FileDeleteCommand command = commandCaptor.getValue();
		assertThat(command.directory()).isEqualTo(FileUploadDirectory.TIMELINE);
		assertThat(command.memberId()).isEqualTo(memberId.toString());
		assertThat(command.activityId()).isEqualTo(activityId.toString());
		assertThat(command.objectKey()).isEqualTo(objectKey);
	}

	@Test
	void delete_이미지_삭제가_실패해도_예외를_전파하지_않는다() throws InterruptedException {
		Long activityId = 1L;
		Long timelineId = 10L;
		Long memberId = 100L;
		String objectKey = "timeline-image.jpg";

		Timeline timeline = Timeline.builder()
			.id(timelineId)
			.timelineImageObjectKey(objectKey)
			.build();

		when(timelineRepository.findByIdAndActivity_IdAndActivity_MemberId(timelineId, activityId, memberId))
			.thenReturn(Optional.of(timeline));
		stubTransactionTemplateToRunCallback();

		CountDownLatch latch = new CountDownLatch(1);
		doAnswer(invocation -> {
			try {
				throw new ActivityNotFoundException();
			} finally {
				latch.countDown();
			}
		}).when(fileDeletePort).deleteImage(any());

		assertThatCode(() -> timelineService.delete(activityId, timelineId, memberId))
			.doesNotThrowAnyException();

		assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
	}

	@Test
	void delete_활동이_없으면_예외를_던지고_이미지_삭제를_요청하지_않는다() {
		Long activityId = 1L;
		Long timelineId = 10L;
		Long memberId = 100L;

		when(timelineRepository.findByIdAndActivity_IdAndActivity_MemberId(timelineId, activityId, memberId))
			.thenReturn(Optional.empty());
		when(activityRepository.existsByIdAndMemberId(activityId, memberId)).thenReturn(false);
		stubTransactionTemplateToRunCallback();

		assertThatThrownBy(() -> timelineService.delete(activityId, timelineId, memberId))
			.isInstanceOf(ActivityNotFoundException.class);

		verify(timelineRepository, never()).delete(any());
		verify(fileDeletePort, never()).deleteImage(any());
	}

	@Test
	void delete_타임라인이_없으면_예외를_던지고_이미지_삭제를_요청하지_않는다() {
		Long activityId = 1L;
		Long timelineId = 10L;
		Long memberId = 100L;

		when(timelineRepository.findByIdAndActivity_IdAndActivity_MemberId(timelineId, activityId, memberId))
			.thenReturn(Optional.empty());
		when(activityRepository.existsByIdAndMemberId(activityId, memberId)).thenReturn(true);
		stubTransactionTemplateToRunCallback();

		assertThatThrownBy(() -> timelineService.delete(activityId, timelineId, memberId))
			.isInstanceOf(TimelineNotFoundException.class);

		verify(timelineRepository, never()).delete(any());
		verify(fileDeletePort, never()).deleteImage(any());
	}

	@Test
	void updateTitle_소유한_타임라인이면_제목을_수정한다() {
		Long activityId = 1L;
		Long timelineId = 10L;
		Long memberId = 100L;
		String newTitle = "백운대 정상 도착";

		Timeline timeline = Timeline.builder()
			.id(timelineId)
			.title("이전 제목")
			.build();

		when(timelineRepository.findByIdAndActivity_IdAndActivity_MemberId(timelineId, activityId, memberId))
			.thenReturn(Optional.of(timeline));

		UpdateTimelineTitleResult result =
			timelineService.updateTitle(new UpdateTimelineTitleCommand(activityId, timelineId, memberId, newTitle));

		assertThat(timeline.getTitle()).isEqualTo(newTitle);
		assertThat(result.timelineId()).isEqualTo(timelineId);
		assertThat(result.title()).isEqualTo(newTitle);
	}

	@Test
	void updateTitle_활동이_없으면_예외를_던진다() {
		Long activityId = 1L;
		Long timelineId = 10L;
		Long memberId = 100L;

		when(timelineRepository.findByIdAndActivity_IdAndActivity_MemberId(timelineId, activityId, memberId))
			.thenReturn(Optional.empty());
		when(activityRepository.existsByIdAndMemberId(activityId, memberId)).thenReturn(false);

		assertThatThrownBy(() ->
			timelineService.updateTitle(new UpdateTimelineTitleCommand(activityId, timelineId, memberId, "제목"))
		).isInstanceOf(ActivityNotFoundException.class);
	}

	@Test
	void updateTitle_타임라인이_없으면_예외를_던진다() {
		Long activityId = 1L;
		Long timelineId = 10L;
		Long memberId = 100L;

		when(timelineRepository.findByIdAndActivity_IdAndActivity_MemberId(timelineId, activityId, memberId))
			.thenReturn(Optional.empty());
		when(activityRepository.existsByIdAndMemberId(activityId, memberId)).thenReturn(true);

		assertThatThrownBy(() ->
			timelineService.updateTitle(new UpdateTimelineTitleCommand(activityId, timelineId, memberId, "제목"))
		).isInstanceOf(TimelineNotFoundException.class);
	}
}
