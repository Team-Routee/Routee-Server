package org.sopt.routee.activity.internal.service.dto.result;

import java.util.List;

public record ActivityCreationTransactionResult(
	CreateActivityResult result,
	List<Long> deletedActivityIds
) {
}
