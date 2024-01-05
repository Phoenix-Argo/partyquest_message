package partyquest.chat.tutorial.v2.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class ChatRoomsResponse {
    private Set<String> rooms;

    public ChatRoomsResponse(Set<String> rooms) {
        this.rooms = rooms;
    }
}
