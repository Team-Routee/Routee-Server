package org.sopt.routee.activity.internal.service.dto.result;

import java.util.List;

import org.sopt.routee.activity.internal.service.dto.vo.TimelineImageDeleteTarget;

public record ActivityCreationTransactionResult(
	CreateActivityResult result,
	List<TimelineImageDeleteTarget> imageDeleteTargets
) {
}
