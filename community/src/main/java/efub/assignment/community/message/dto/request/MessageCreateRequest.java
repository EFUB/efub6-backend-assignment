package efub.assignment.community.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageCreateRequest {

    @NotBlank(message = "쪽지 내용을 입력해주세요.")
    @Size(max = 500, message = "쪽지 내용은 500자 이하로 입력해주세요.")
    private String content;
}