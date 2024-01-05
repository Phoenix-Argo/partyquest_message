package partyquest.chat.tutorial.v2;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * TODO: StreamListener에 ObjectRecord 타입 등록안돼는 이유 찾기
 */
@Slf4j
//@Service
@RequiredArgsConstructor
public class RedisObjectConsumer implements StreamListener<String, ObjectRecord<String,Object>> {
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate redisTemplate;
    private final RedisStreamOps redisStreamOps;
    private StreamMessageListenerContainer<String, ObjectRecord<String, Object>> streamListenerContainer;
    @Override
    public void onMessage(ObjectRecord<String, Object> message) {
        log.info("[CONSUMER] value = {}", message.getValue());
    }

    @PostConstruct
    public void init() {
        this.streamListenerContainer = redisStreamOps.createStreamMessageListenerContainer();
    }

    @PreDestroy
    void destroy() {
        this.streamListenerContainer.stop();
    }
    public void createSubs(String streamKey) {
        Subscription newSubs = this.streamListenerContainer.receive(StreamOffset.fromStart(streamKey), this);
    }
}
