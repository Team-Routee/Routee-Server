package org.sopt.routee.member.internal.repository;

import java.util.Optional;

import org.sopt.routee.member.internal.entity.MemberAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAgreementRepository extends JpaRepository<MemberAgreement, Long> {

	Optional<MemberAgreement> findByMember_Id(Long memberId);

	boolean existsByMember_Id(Long memberId);

	void deleteByMember_Id(Long memberId);

}
