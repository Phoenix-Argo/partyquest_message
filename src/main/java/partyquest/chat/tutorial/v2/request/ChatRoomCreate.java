package partyquest.chat.tutorial.v2.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Data
@NoArgsConstructor
public class ChatRoomCreate {
    private String roomName;
    private final String WELCOME_MESSAGE="환영합니다";

    public String makeStreamKey() {
        return "chat-" + this.roomName;
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "message", WELCOME_MESSAGE,
                "createdAt", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
    }
}
