package org.sopt.routee.activity.internal.entity.summary;

import java.time.LocalDate;

import org.hibernate.annotations.ColumnDefault;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "activity_daily_summary",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_activity_daily_summary_member_date",
			columnNames = {"member_id", "activity_date"}
		)
	})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityDailySummary {
	@Id
	@Tsid
	private Long id;

	@Column(name = "activity_date", nullable = false)
	private LocalDate activityDate;

	@Column(name = "total_duration_sec", nullable = false)
	@ColumnDefault("0")
	private Integer totalDurationSec = 0;

	@Column(name = "cover_image_url")
	private String coverImageUrl;

	@Column(name = "activity_count", nullable = false)
	@ColumnDefault("0")
	private Integer activityCount = 0;

	@Column(name = "member_id", nullable = false, updatable = false)
	private Long memberId;

	@Builder
	public ActivityDailySummary(
		Long id,
		LocalDate activityDate,
		Integer totalDurationSec,
		String coverImageUrl,
		Integer activityCount,
		Long memberId
	) {
		this.id = id;
		this.activityDate = activityDate;
		this.totalDurationSec = totalDurationSec;
		this.coverImageUrl = coverImageUrl;
		this.activityCount = activityCount;
		this.memberId = memberId;
	}
}
