package partyquest.chat.domain.redis;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("chatRoom")
@Getter
@NoArgsConstructor
public class ChatRoom {
    @Id
    String id;
    String roomName;
    @Builder
    public ChatRoom(String roomName) {
        this.roomName = roomName;
    }
}
