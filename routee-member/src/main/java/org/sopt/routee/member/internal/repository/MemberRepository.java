package org.sopt.routee.member.internal.repository;

import java.util.Optional;

import org.sopt.routee.external.api.type.OAuthProvider;
import org.sopt.routee.member.internal.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByOauthIdAndOauthProvider(String oauthId, OAuthProvider oauthProvider);

	boolean existsByOauthIdAndOauthProvider(String oauthId, OAuthProvider oauthProvider);

}
