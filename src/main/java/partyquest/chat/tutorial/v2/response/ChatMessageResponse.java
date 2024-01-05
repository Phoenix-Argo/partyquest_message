package partyquest.chat.tutorial.v2.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessageResponse {
    private String message;
    @Builder
    public ChatMessageResponse(String message) {
        this.message = message;
    }
}
