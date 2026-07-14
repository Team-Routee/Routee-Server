package org.sopt.routee.activity.internal.service.dto.result;

import java.time.LocalDate;
import java.util.List;

public record ActivityEditItemResult(
	Long activityId,
	String title,
	LocalDate activityDate,
	List<String> timelinePhotoUrls
) {
}
