package org.sopt.routee.activity.internal.entity.route;

import java.time.Instant;

import org.sopt.routee.activity.internal.entity.activity.Activity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "route",
	indexes = {
		@Index(
			name = "idx_route_activity_id",
			columnList = "activity_id, sequence"
		)
	})
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Route {
	@Id
	@Tsid
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "sequence", nullable = false)
	private Integer sequence;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "activity_id", nullable = false)
	private Activity activity;
}
