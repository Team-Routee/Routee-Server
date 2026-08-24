package org.sopt.routee.activity.internal.service.dto.result;

import java.util.List;

import org.sopt.routee.activity.internal.repository.projection.TimelineImageDeleteTargetProjection;

public record ActivityCreationTransactionResult(
	CreateActivityResult result,
	List<TimelineImageDeleteTargetProjection> imageDeleteTargets
) {
}
