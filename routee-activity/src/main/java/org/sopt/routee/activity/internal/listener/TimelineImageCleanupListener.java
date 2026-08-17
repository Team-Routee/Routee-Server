package org.sopt.routee.activity.internal.listener;

import org.sopt.routee.activity.internal.event.TimelineDeletedEvent;
import org.sopt.routee.exception.BaseException;
import org.sopt.routee.external.api.command.FileDeleteCommand;
import org.sopt.routee.external.api.port.FileDeletePort;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class TimelineImageCleanupListener {

	private final FileDeletePort fileDeletePort;

	@ApplicationModuleListener
	void handleTimelineDeleted(TimelineDeletedEvent event) {
		try {
			fileDeletePort.deleteImage(new FileDeleteCommand(
				FileUploadDirectory.TIMELINE,
				event.activityId().toString(),
				event.timelineImageObjectKey()
			));
		} catch (BaseException e) {
			log.warn("Timeline image delete failed. activityId={}, objectKey={}",
				event.activityId(), event.timelineImageObjectKey(), e);
			throw e;
		}
	}
}
