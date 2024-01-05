package partyquest.chat.tutorial;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import partyquest.chat.tutorial.v1.Greeting;
import partyquest.chat.tutorial.v1.HelloMessage;
import partyquest.chat.tutorial.v2.RedisConsumer;
import partyquest.chat.tutorial.v2.RedisPublisher;
import partyquest.chat.tutorial.v2.request.ChatMessageCreate;
import partyquest.chat.tutorial.v2.request.ChatMessagePub;
import partyquest.chat.tutorial.v2.request.ChatRoomCreate;
import partyquest.chat.tutorial.v2.response.ChatRoomsResponse;

@Slf4j
@RequiredArgsConstructor
@Controller
public class TutorialController {
    private final RedisPublisher redisPublisher;
    private final RedisConsumer redisConsumer;
    private final TutorialRedisService redisService;

    @MessageMapping("/hello")
    @SendTo("/topic/tutorial") // '/topic/tutorial'채널에 응답한다.
    public Greeting greeting(HelloMessage message) throws InterruptedException {
        //Thread.sleep(1000);
        return new Greeting(String.format("Hello %s", message.getName()));
    }

    @MessageMapping("/redis")
    public void doJobRedis(ChatMessagePub req) {
        log.info("[redis controller] called");
        //todo: 생성된 방이 없다면 컨테이너에 리스너를 등록한다.
        log.info("[redis controller] ChatMessagePub = {}", req);
        redisConsumer.createSubscription(req.getMessage());
        redisPublisher.publish(req);
    }

    @MessageMapping("/redis/v2/create")
    public void createNewRoom(ChatRoomCreate req) {
        log.info("[REDIS STREAM CONTROLLER] req = {}", req);
        redisPublisher.publishChatRoom(req);
    }

    @MessageMapping("/redis/v2/send")
    public void sendMessage(ChatMessageCreate req) {
        log.info("[REDIS STREAM CONTROLLER] req = {}", req);
        if(!redisConsumer.createSubscription(req.getRoomName()))
            redisService.readWholeStream(req.getRoomName());
        redisPublisher.publishChat(req);
    }

    @ResponseBody
    @GetMapping("/tutorial/stream-keys")
    public ResponseEntity<ChatRoomsResponse> getStreamKeys() {
        return ResponseEntity.ok(redisService.getRedisKey());
    }


    @RequestMapping(value = "/tutorial")
    @GetMapping
    public String index() {
        return "tutorial/index";
    }

    @RequestMapping(value = "/tutorial/{version}")
    @GetMapping
    public String versionIndex(@PathVariable("version") String version) {
        return String.format("tutorial/%s", version);
    }
}
