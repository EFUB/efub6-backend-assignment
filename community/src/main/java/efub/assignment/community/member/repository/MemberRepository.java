package efub.assignment.community.member.repository;

import efub.assignment.community.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//제네릭 타입 필수: Member 엔티티 관리, 해당 엔티티 PK 타입은 Long
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByStudentId(Long studentId);
    boolean existsByEmail(String email);

    Optional<Member> findByMemberId(Long memberId);
}
