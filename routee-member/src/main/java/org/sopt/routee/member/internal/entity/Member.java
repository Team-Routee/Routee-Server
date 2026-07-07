package org.sopt.routee.member.internal.entity;

import org.hibernate.annotations.ColumnDefault;
import org.sopt.routee.entity.BaseEntity;
import org.sopt.routee.external.api.type.OAuthProvider;
import org.sopt.routee.member.api.type.MemberRole;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
@Table(name = "member",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_member_oauth_id_provider",
			columnNames = {"oauth_id", "oauth_provider"})
	})
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
	@Id
	@Tsid
	private Long id;

	@Column(name = "nickname", nullable = false)
	private String nickname;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

	@Column(name = "oauth_id", nullable = false, updatable = false)
	private String oauthId;

	@Enumerated(EnumType.STRING)
	@Column(name = "oauth_provider", nullable = false, updatable = false)
	private OAuthProvider oauthProvider;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	@ColumnDefault("'ROLE_USER'")
	private MemberRole role = MemberRole.ROLE_USER;

	@Column(name = "total_activity_count", nullable = false)
	@Builder.Default
	@ColumnDefault("0")
	private Integer totalActivityCount = 0;
}
