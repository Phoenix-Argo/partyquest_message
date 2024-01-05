package partyquest.chat.tutorial.v2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ChatRoom {
    private LocalDate createdDate;
    private String roomName;

    public ChatRoom( String roomName) {
        this.createdDate = LocalDate.now();
        this.roomName = roomName;
    }
}
