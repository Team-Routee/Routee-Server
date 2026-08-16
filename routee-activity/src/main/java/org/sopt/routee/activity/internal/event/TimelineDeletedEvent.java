package org.sopt.routee.activity.internal.event;

public record TimelineDeletedEvent(
	Long activityId,
	String timelineImageObjectKey
) {
}
