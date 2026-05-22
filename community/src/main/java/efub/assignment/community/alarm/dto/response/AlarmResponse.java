package efub.assignment.community.alarm.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import efub.assignment.community.alarm.domain.Alarm;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlarmResponse {

    private String type;
    private String boardName;
    private String content;
    private LocalDateTime createdAt;

    public static AlarmResponse from(Alarm alarm) {
        return AlarmResponse.builder()
                .type(alarm.getAlarmType().name().toLowerCase())
                .boardName(alarm.getBoardName())
                .content(alarm.getContent())
                .createdAt(alarm.getCreatedAt())
                .build();
    }
}
