package org.sopt.routee.member.internal.repository;

import java.util.Optional;

import org.sopt.routee.external.api.type.OAuthProvider;
import org.sopt.routee.member.internal.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByOauthIdAndOauthProvider(String oauthId, OAuthProvider oauthProvider);

	boolean existsByOauthIdAndOauthProvider(String oauthId, OAuthProvider oauthProvider);

	@Modifying
	@Query("UPDATE Member m SET m.totalActivityCount = m.totalActivityCount + 1 WHERE m.id = :memberId")
	void incrementTotalActivityCount(@Param("memberId") Long memberId);

}
