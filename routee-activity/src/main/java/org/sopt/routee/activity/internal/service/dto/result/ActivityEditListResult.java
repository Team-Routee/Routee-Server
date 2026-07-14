package org.sopt.routee.activity.internal.service.dto.result;

import java.util.List;

public record ActivityEditListResult(
	int year,
	int month,
	List<ActivityEditItemResult> activities
) {
}
