package partyquest.chat.tutorial.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import partyquest.chat.tutorial.TutorialRedisService;
import partyquest.chat.tutorial.v2.request.ChatMessageCreate;
import partyquest.chat.tutorial.v2.request.ChatMessagePub;
import partyquest.chat.tutorial.v2.request.ChatRoomCreate;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final TutorialRedisService redisService;

    /**
     * ChatMessagePub와 같은 Dto를 Map으로 전환해야한다
     * 왜냐하면 Listener가 무슨 이유인지 MapRecord만 인식이 가능하다.
     * @param req
     */
    public void  publish(ChatMessagePub req) {
        log.info("[REDIS PUBLISHER] start");
// TODO: object record 넘어가면 Consumer의 Listener가 반응하지 못 한다.
//        StringRecord record = StreamRecords
//                .newRecord()
//                .in(req.getName())
//                .ofStrings(Map.of("topic", req.getName()));
        MapRecord<String, String, String> record = StreamRecords.newRecord()
                .in(req.getMessage())
                .ofMap(Map.of("message", req.getMessage()));
        redisTemplate.opsForStream().add(record);
        log.info("[REDIS PUBLISHER] message = {}",req.getMessage());
        log.info("[REDIS PUBLISHER] record message = {}",(String) record.getValue().get("message"));
        //todo: ttl 설정해주기
        log.info("[REDIS PUBLISHER] end");
    }

    public void publishChatRoom(ChatRoomCreate req) {
        MapRecord<String, String, Object> record = StreamRecords.newRecord()
                .in(req.makeStreamKey())
                .ofMap(req.toMap());
        redisTemplate.opsForStream().add(record);
    }

    public void publishChat(ChatMessageCreate req) {
        MapRecord<String, String, Object> record = StreamRecords.newRecord()
                .in(req.getRoomName())
                .ofMap(req.toMap());
        redisTemplate.opsForStream().add(record);

        //메모리에 캐시로 등록하고 해당 캐시가 없을때에만 구독을 설정한다.
        redisService.putStreamKey(record.getStream());
    }
}
