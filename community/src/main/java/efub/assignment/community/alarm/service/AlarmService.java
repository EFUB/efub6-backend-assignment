package efub.assignment.community.alarm.service;

import efub.assignment.community.alarm.domain.Alarm;
import efub.assignment.community.alarm.dto.response.AlarmResponse;
import efub.assignment.community.alarm.enums.AlarmType;
import efub.assignment.community.alarm.repository.AlarmRepository;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final MemberService memberService;

    // 알림 생성
    @Transactional
    public void createCommentAlarm(Member receiver, String boardName, String commentContent) {
        Alarm alarm = Alarm.builder()
                .receiver(receiver)
                .alarmType(AlarmType.BOARD)
                .boardName(boardName)
                .content("새로운 댓글이 달렸어요!: " + commentContent)
                .build();
        alarmRepository.save(alarm);
    }

    @Transactional
    public void createMessageRoomAlarm(Member receiver) {
        Alarm alarm = Alarm.builder()
                .receiver(receiver)
                .alarmType(AlarmType.MESSAGE_ROOM)
                .content("새로운 쪽지방이 생겼어요!")
                .build();
        alarmRepository.save(alarm);
    }

    // 알림 목록 조회
    public List<AlarmResponse> getAlarmList(Long receiverId) {
        Member receiver = memberService.findByMemberId(receiverId);
        List<Alarm> alarmList = alarmRepository.findByReceiverOrderByCreatedAtDesc(receiver);

        return alarmList.stream()
                .map(AlarmResponse::from)
                .collect(Collectors.toList());
    }
}
