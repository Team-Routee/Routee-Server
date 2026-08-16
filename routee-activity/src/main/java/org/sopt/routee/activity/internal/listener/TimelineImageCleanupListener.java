package org.sopt.routee.activity.internal.listener;

import org.sopt.routee.activity.internal.event.TimelineDeletedEvent;
import org.sopt.routee.external.api.command.FileDeleteCommand;
import org.sopt.routee.external.api.port.FileDeletePort;
import org.sopt.routee.external.api.type.FileUploadDirectory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class TimelineImageCleanupListener {

	private final FileDeletePort fileDeletePort;

	@ApplicationModuleListener
	void handleTimelineDeleted(TimelineDeletedEvent event) {
		fileDeletePort.deleteImage(new FileDeleteCommand(
			FileUploadDirectory.TIMELINE,
			event.activityId().toString(),
			event.timelineImageObjectKey()
		));
	}
}
