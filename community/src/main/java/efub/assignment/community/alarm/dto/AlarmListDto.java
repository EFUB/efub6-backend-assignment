package efub.assignment.community.alarm.dto;

import efub.assignment.community.alarm.domain.Alarm;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AlarmListDto {
    private List<AlarmDto> alarmList;

    public static AlarmListDto of (List<Alarm> alarms) {
        return builder()
                .alarmList(alarms.stream().map(AlarmDto::of).toList())
                .build();
    }
}
