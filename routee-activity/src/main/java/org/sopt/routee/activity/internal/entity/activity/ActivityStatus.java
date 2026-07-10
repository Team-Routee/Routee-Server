package org.sopt.routee.activity.internal.entity.activity;

public enum ActivityStatus {
	ACTIVITY_IN_PROGRESS, ACTIVITY_COMPLETED, ACTIVITY_PAUSED;

	public boolean isCompleted() {

		return this == ACTIVITY_COMPLETED;
	}

	public boolean isSameAs(ActivityStatus status) {
		return this == status;
	}

	public boolean isChangeableRequestStatus() {
		return this == ACTIVITY_IN_PROGRESS
			|| this == ACTIVITY_PAUSED;
	}
}
