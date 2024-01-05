package partyquest.chat.tutorial;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import partyquest.chat.tutorial.v2.response.ChatRoomsResponse;

import java.time.Duration;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TutorialRedisService {
    private final RedisTemplate redisTemplate;
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

    public boolean isStreamKeyExist(String streamKey) {
        return valueOperations.get(parseStreamKey(streamKey))==null ? false : true;
    }

    public void putStreamKey(String streamKey) {
        valueOperations.set(parseStreamKey(streamKey), "room", Duration.ofSeconds(60));
    }

    public String parseStreamKey(String rawKey) {
        return String.format("room-%s", rawKey);
    }
}
