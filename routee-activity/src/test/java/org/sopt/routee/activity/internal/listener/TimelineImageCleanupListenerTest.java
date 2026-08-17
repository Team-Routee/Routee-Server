package org.sopt.routee.activity.internal.listener;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.routee.activity.internal.event.TimelineDeletedEvent;
import org.sopt.routee.activity.internal.exception.ActivityNotFoundException;
import org.sopt.routee.exception.BaseException;
import org.sopt.routee.external.api.command.FileDeleteCommand;
import org.sopt.routee.external.api.port.FileDeletePort;
import org.sopt.routee.external.api.type.FileUploadDirectory;

@ExtendWith(MockitoExtension.class)
class TimelineImageCleanupListenerTest {

	@Mock
	private FileDeletePort fileDeletePort;

	@InjectMocks
	private TimelineImageCleanupListener timelineImageCleanupListener;

	@Test
	void handleTimelineDeleted_타임라인_삭제_이벤트를_받으면_TIMELINE_디렉토리_기준으로_이미지_삭제를_요청한다() {
		Long activityId = 1L;
		String objectKey = "timeline-image.jpg";

		timelineImageCleanupListener.handleTimelineDeleted(new TimelineDeletedEvent(activityId, objectKey));

		ArgumentCaptor<FileDeleteCommand> commandCaptor = ArgumentCaptor.forClass(FileDeleteCommand.class);
		verify(fileDeletePort).deleteImage(commandCaptor.capture());

		FileDeleteCommand command = commandCaptor.getValue();
		assertThat(command.directory()).isEqualTo(FileUploadDirectory.TIMELINE);
		assertThat(command.activityId()).isEqualTo(activityId.toString());
		assertThat(command.objectKey()).isEqualTo(objectKey);
	}

	@Test
	void handleTimelineDeleted_이미지_삭제가_실패하면_예외를_그대로_전파한다() {
		Long activityId = 1L;
		String objectKey = "timeline-image.jpg";
		doThrow(new ActivityNotFoundException()).when(fileDeletePort).deleteImage(any());

		assertThatThrownBy(() ->
			timelineImageCleanupListener.handleTimelineDeleted(new TimelineDeletedEvent(activityId, objectKey)))
			.isInstanceOf(BaseException.class);
	}
}
