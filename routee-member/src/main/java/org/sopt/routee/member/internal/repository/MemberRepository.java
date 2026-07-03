package org.sopt.routee.member.internal.repository;

import org.sopt.routee.member.internal.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
