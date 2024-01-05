package partyquest.chat.tutorial.v2.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessagePub {
    private String message;

    public ChatMessagePub(String name) {
        this.message = name;
    }
}
