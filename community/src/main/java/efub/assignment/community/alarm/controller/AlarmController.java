package efub.assignment.community.alarm.controller;

import efub.assignment.community.alarm.dto.response.AlarmResponse;
import efub.assignment.community.alarm.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    // 알림 조회
    @GetMapping
    public ResponseEntity<List<AlarmResponse>> getAlarmList(@RequestHeader("Auth-Id") Long requesterId) {
        return ResponseEntity.ok(alarmService.getAlarmList(requesterId));
    }

}
