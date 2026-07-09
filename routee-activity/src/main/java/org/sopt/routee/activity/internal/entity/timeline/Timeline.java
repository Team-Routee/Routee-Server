package org.sopt.routee.activity.internal.entity.timeline;

import java.time.Instant;

import org.hibernate.annotations.ColumnDefault;
import org.locationtech.jts.geom.Point;
import org.sopt.routee.activity.internal.entity.activity.Activity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "timeline",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_timeline_activity_point_index",
			columnNames = {"activity_id", "track_point_index"}
		)
	})
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Timeline {
	@Id
	@Tsid
	private Long id;

	@Column(name = "title")
	private String title;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "timeline_status", nullable = false)
	@ColumnDefault("'CREATION_IN_PROGRESS'")
	private TimelineStatus timelineStatus = TimelineStatus.CREATION_IN_PROGRESS;

	@Column(name = "timeline_image_object_key", nullable = false)
	private String timelineImageObjectKey;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "track_point_index", nullable = false, updatable = false)
	private Integer trackPointIndex;

	@Column(name = "location", columnDefinition = "geometry(POINTZM, 4326)", nullable = false, updatable = false)
	private Point location;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "activity_id", nullable = false)
	private Activity activity;
}
