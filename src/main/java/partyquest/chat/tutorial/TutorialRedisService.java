package partyquest.chat.tutorial;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import partyquest.chat.tutorial.v2.RedisStreamOps;
import partyquest.chat.tutorial.v2.response.ChatRoomsResponse;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TutorialRedisService {
    private final RedisTemplate redisTemplate;
    private final RedisStreamOps redisStreamOps;
    private ValueOperations valueOperations;
    private final

    @PostConstruct
    void init() {
        this.valueOperations = redisTemplate.opsForValue();
    }

    public ChatRoomsResponse getRedisKey() {
        Set<String> keys = redisTemplate.keys("chat*");
        return new ChatRoomsResponse(keys);
    }

    public boolean isRoomExist(String streamKey) {
        return valueOperations.get(parseStreamKey(streamKey))==null ? false : true;
    }

    public void putRoomKey(String streamKey) {
        valueOperations.set(parseStreamKey(streamKey), "room", Duration.ofSeconds(60));
    }

    public boolean isKeyExist(String key) {
        return valueOperations.get(key)==null ? false : true;
    }

    public void putKey(String key) {
        valueOperations.set(key, "room", Duration.ofSeconds(60));
    }

    public String parseStreamKey(String rawKey) {
        return String.format("room-%s", rawKey);
    }

    public void readWholeStream(String streamKey) {
        List<MapRecord<String,String,Object>> result = redisTemplate.opsForStream().read(StreamOffset.fromStart(streamKey));
        for (MapRecord<String,String,Object> record: result
        ){
            log.info("record key: {}, value: {}",record.getStream(),record.getValue().get("message"));
        }
    }
}
