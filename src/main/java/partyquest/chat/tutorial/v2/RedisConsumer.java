package partyquest.chat.tutorial.v2;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import partyquest.chat.tutorial.TutorialRedisService;
import partyquest.chat.tutorial.v2.response.ChatMessageResponse;

import java.time.Duration;
import java.util.Map;

/**
 * TODO: StreamListener<String, ObjectRecord<String,Object>>를 사용하면 파싱하는 문제가 생기는거 해결하기
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisConsumer implements StreamListener<String, MapRecord<String,String,Object>> {
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisStreamOps redisOperator;
    private final RedisTemplate redisTemplate;
    private final TutorialRedisService redisService;
    private StreamMessageListenerContainer<String, MapRecord<String,String,Object>> listenerContainer;
    private Subscription subscription;
    private String streamKey;
    private String consumerGroupName;
    private String consumerName;

    public void onMessageV1(MapRecord<String,String,Object> message) {
        log.info("[REDIS SUBSCRIBER] start");
        messagingTemplate.convertAndSend(
                //String.format("/topic/redis/%s",message.getStream())
                "/topic/redis"
                , ChatMessageResponse.builder()
                        .message((String) message.getValue().get("message"))
                        .build()
        );
        log.info("is instance of map ? ={}", message.getValue() instanceof Map<String, Object>);
        log.info((String) message.getValue().get("message"));
        log.info("[REDIS SUBSCRIBER] end");
    }
    @Override
    public void onMessage(MapRecord<String,String,Object> message) {
        log.info("[REDIS SUBSCRIBER] start");
        messagingTemplate.convertAndSend(
                //String.format("/topic/redis/%s",message.getStream())
                String.format("/topic/redis/v2/chatroom/%s",message.getStream())
                , ChatMessageResponse.builder()
                        .message((String) message.getValue().get("message"))
                        .build()
        );
        log.info((String) message.getStream());
        log.info((String) message.getValue().get("message"));
        log.info("[REDIS SUBSCRIBER] end");
    }

    @PostConstruct
    void init() throws InterruptedException {
        // Stream 기본 정보
        this.streamKey = "dragon";
        this.consumerGroupName = "consumerGroupName";
        this.consumerName = "consumerName";

        // Consumer Group 설정
        this.redisOperator.createStreamConsumerGroup(streamKey, consumerGroupName);

        // StreamMessageListenerContainer 설정
        this.listenerContainer = this.redisOperator.createStreamMessageListenerContainer();

        //Subscription 설정
        this.subscription = this.listenerContainer.receive(
                Consumer.from(this.consumerGroupName, consumerName),
                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                this
        );
        // 2초 마다, 정보 GET
        this.subscription.await(Duration.ofSeconds(2));
        // redis listen 시작
        this.listenerContainer.start();
    }
    @PreDestroy
    void destroy() {
        if(this.subscription != null){
            this.subscription.cancel();
        }
        if(this.listenerContainer != null){
            this.listenerContainer .stop();
        }
    }

    /**
     * publisher에서 레디스 스트림을 등록하기전에 키가 있는지를 확인해야한다.
     * @param streamKey
     */
    public void createSubscription(String streamKey) {
        String groupName = String.format("%s-group", streamKey);
        log.info("[REDIS CONSUMER] search with group name : {}",groupName);
        if(redisTemplate.hasKey(streamKey) && !redisOperator.isStreamConsumerGroupExist(streamKey,groupName)) {
            log.info("create new listener group");
            // 존재 하지 않을때에만 구독을 설치해준다.
            this.redisOperator.createStreamConsumerGroup(streamKey, groupName);
            Subscription newSubs = this.listenerContainer.receive(
                    Consumer.from(groupName, "name"),
                    StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                    this
            );
        }else
            log.info("there is already listener group");
    }
}
