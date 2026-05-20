package efub.assignment.community.alarm.dto;

import efub.assignment.community.alarm.domain.Alarm;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AlarmDto {
    private String boardTitle;
    private String content;
    private LocalDateTime createdAt;

    public static AlarmDto of (Alarm alarm) {
        return builder()
                .boardTitle(alarm.getBoardTitle())
                .content(alarm.getContent())
                .createdAt(alarm.getCreatedAt())
                .build();
    }
}
