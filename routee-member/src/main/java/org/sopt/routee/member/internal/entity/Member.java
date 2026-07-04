package org.sopt.routee.member.internal.entity;

import java.time.LocalDate;

import org.hibernate.annotations.ColumnDefault;
import org.sopt.routee.entity.BaseEntity;
import org.sopt.routee.member.api.type.MemberRole;
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

	@Column(name = "tag_name", nullable = false, unique = true)
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
	@ColumnDefault("ROLE_USER")
	private MemberRole role = MemberRole.ROLE_USER;

	@Column(name = "last_activity_date")
	private LocalDate lastActivityDate;

	@Column(name = "current_streak", nullable = false)
	@ColumnDefault("0")
	private Integer currentStreak = 0;

	@Column(name = "total_activity_count", nullable = false)
	@ColumnDefault("0")
	private Integer totalActivityCount = 0;

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
