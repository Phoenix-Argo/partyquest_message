package partyquest.chat.tutorial.v2.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessageDto {
    private String message;
    @Builder
    public ChatMessageDto(String message) {
        this.message = message;
    }
}
