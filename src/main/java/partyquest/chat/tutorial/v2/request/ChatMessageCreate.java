package partyquest.chat.tutorial.v2.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Data
@NoArgsConstructor
public class ChatMessageCreate {
    private String roomName;
    private String message;

    public Map<String, Object> toMap() {
        return Map.of(
                "message", message,
                "createdAt", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
    }
}
