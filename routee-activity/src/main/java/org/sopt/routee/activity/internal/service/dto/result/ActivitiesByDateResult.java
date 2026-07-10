package org.sopt.routee.activity.internal.service.dto.result;

import java.time.LocalDate;
import java.util.List;

public record ActivitiesByDateResult(
	LocalDate date,
	List<ActivityPreviewResult> activities
) {
}
