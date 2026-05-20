package efub.assignment.community.alarm.service;

import efub.assignment.community.alarm.domain.Alarm;
import efub.assignment.community.alarm.dto.AlarmListDto;
import efub.assignment.community.alarm.enums.AlarmType;
import efub.assignment.community.alarm.repository.AlarmRepository;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final MemberService memberService;

    @Transactional
    public void createAlarm (AlarmType type, Member receiver, String postTitle, String content) {
        Alarm alarm = Alarm.builder()
                .type(type)
                .receiver(receiver)
                .boardTitle(postTitle)
                .content(content)
                .build();

        alarmRepository.save(alarm);
    }

    public AlarmListDto getAlarmList(Long requesterId) {
        Member requester = memberService.findByMemberId(requesterId);
        List<Alarm> alarmList = alarmRepository.findAllByReceiver(requester);

        return AlarmListDto.of(alarmList);
    }
}
