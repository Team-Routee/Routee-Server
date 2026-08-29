package org.sopt.routee.member.internal.entity;

import java.time.Instant;

import org.sopt.routee.entity.BaseEntity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "member_agreement",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_member_agreement_member_id",
			columnNames = {"member_id"})
	})
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAgreement extends BaseEntity {
	@Id
	@Tsid
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "member_id", nullable = false, updatable = false)
	private Member member;

	@Column(name = "service_terms_agreed_at")
	private Instant serviceTermsAgreedAt;

	@Column(name = "privacy_policy_agreed_at")
	private Instant privacyPolicyAgreedAt;

	@Column(name = "location_service_terms_agreed_at")
	private Instant locationServiceTermsAgreedAt;

	@Column(name = "over14_confirmed_at")
	private Instant over14ConfirmedAt;

	@Column(name = "marketing_consent_agreed_at")
	private Instant marketingConsentAgreedAt;
}
