package partyquest.chat.tutorial;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class TutorialController {
    @MessageMapping("/hello")
    @SendTo("/topic/tutorial") // '/topic/tutorial'채널에 응답한다.
    public Greeting greeting(HelloMessage message) throws InterruptedException {
        Thread.sleep(1000);
        return new Greeting(String.format("Hello %s", message.getName()));
    }
}
