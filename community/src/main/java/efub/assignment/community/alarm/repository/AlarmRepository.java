package efub.assignment.community.alarm.repository;

import efub.assignment.community.alarm.domain.Alarm;
import efub.assignment.community.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByReceiver(Member receiver);
}
