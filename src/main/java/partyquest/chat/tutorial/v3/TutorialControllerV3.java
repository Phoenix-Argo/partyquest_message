package partyquest.chat.tutorial.v3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import partyquest.chat.tutorial.v2.request.ChatRoomCreate;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TutorialControllerV3 {
    @MessageMapping("/redis/v3/room/create")
    public void createRoom(ChatRoomCreate dto) {

    }
}
