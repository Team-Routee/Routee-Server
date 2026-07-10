package org.sopt.routee.activity.internal.service.dto.result;

import java.time.Instant;

public record TimelineResult(
	Long timelineId,
	String title,
	String imageUrl,
	Instant createdAt
) {
}
