package org.sopt.routee.member.internal.entity;

import java.time.LocalDate;

import org.sopt.routee.entity.BaseEntity;
import org.sopt.routee.member.api.type.OAuthProvider;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member",
	indexes = {
		@Index(
			name = "idx_member_tag_name",
			columnList = "tag_name"
		)
	},
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_member_oauth_id_provider",
			columnNames = {"oauth_id", "oauth_provider"})
	})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
	@Id
	@Tsid
	private Long id;

	@Column(name = "nickname", nullable = false)
	private String nickname;

	@Column(name = "tag_name", nullable = false)
	private String tagName;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

	@Column(name = "oauth_id", nullable = false, updatable = false)
	private String oauthId;

	@Enumerated(EnumType.STRING)
	@Column(name = "oauth_provider", nullable = false, updatable = false)
	private OAuthProvider oauthProvider;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private MemberRole role;

	@Column(name = "last_activity_date")
	private LocalDate lastActivityDate;

	@Column(name = "current_streak", nullable = false)
	private Integer currentStreak;

	@Column(name = "total_avtivity_count", nullable = false)
	private Integer totalActivityCount;

	@Builder
	private Member(
		Long id,
		String nickname,
		String tagName,
		String profileImageUrl,
		String oauthId,
		OAuthProvider oauthProvider,
		MemberRole role,
		LocalDate lastActivityDate,
		Integer currentStreak,
		Integer totalActivityCount
	) {
		this.id = id;
		this.nickname = nickname;
		this.tagName = tagName;
		this.profileImageUrl = profileImageUrl;
		this.oauthId = oauthId;
		this.oauthProvider = oauthProvider;
		this.role = role;
		this.lastActivityDate = lastActivityDate;
		this.currentStreak = currentStreak;
		this.totalActivityCount = totalActivityCount;
	}
}
