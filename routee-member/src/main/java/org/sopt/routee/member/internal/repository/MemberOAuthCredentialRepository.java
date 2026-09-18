package org.sopt.routee.member.internal.repository;

import java.util.Optional;

import org.sopt.routee.member.internal.entity.MemberOAuthCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberOAuthCredentialRepository extends JpaRepository<MemberOAuthCredential, Long> {

	Optional<MemberOAuthCredential> findByMember_Id(Long memberId);

	boolean existsByMember_Id(Long memberId);

	void deleteByMember_Id(Long memberId);

}
