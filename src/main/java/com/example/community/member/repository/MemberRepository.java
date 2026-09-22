package com.example.community.member.repository;

import com.example.community.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);

    // 멤버 ID로 조회
    Optional<Member> findByMemberId(Long memberId);

}
