package org.sopt.routee.activity.internal.entity.activity;

import java.time.Instant;

import org.hibernate.annotations.ColumnDefault;
import org.locationtech.jts.geom.LineString;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "activity",
	indexes = {
		@Index(
			name = "idx_activity_member_id_started_at",
			columnList = "member_id, started_at"
		)
	})
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Activity {
	@Id
	@Tsid
	private Long id;

	@Column(name = "title", nullable = false)
	private String title;

	@Enumerated(EnumType.STRING)
	@Column(name = "activity_type", nullable = false)
	private ActivityType activityType;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "activity_status", nullable = false)
	@ColumnDefault("'ACTIVITY_IN_PROGRESS'")
	private ActivityStatus activityStatus = ActivityStatus.ACTIVITY_IN_PROGRESS;

	@Column(name = "distance")
	private Integer distance;

	@Column(name = "duration_sec")
	private Integer durationSec;

	@Column(name = "max_elevation")
	private Integer maxElevation;

	@Column(name = "map_image_url")
	private String mapImageUrl;

	@Column(name = "track_image_url")
	private String trackImageUrl;

	@Column(name = "cover_image_object_key")
	private String coverImageObjectKey;

	@Column(name = "track", columnDefinition = "geometry(LINESTRINGZM, 4326)")
	private LineString track;

	@Column(name = "started_at")
	private Instant startedAt;

	@Column(name = "ended_at")
	private Instant endedAt;

	@Column(name = "member_id", nullable = false, updatable = false)
	private Long memberId;

	public void updateStatus(ActivityStatus status) {
		this.activityStatus = status;
	}
}
