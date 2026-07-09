package org.sopt.routee.activity.internal.service.dto.result;

import java.util.List;

public record ActivitiesByDateResult(
	String date,
	List<ActivityPreviewResult> activities
) {
}
