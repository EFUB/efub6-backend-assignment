package efub.assignment.community.alarm.controller;

import efub.assignment.community.alarm.dto.AlarmListDto;
import efub.assignment.community.alarm.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping
    public ResponseEntity<AlarmListDto> getAlarmList (@RequestHeader("Auth-id") Long requesterId) {
        AlarmListDto response = alarmService.getAlarmList(requesterId);

        return ResponseEntity.ok(response);
    }
}
