package partyquest.chat.tutorial.v2;

import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.CommandKeyword;
import io.lettuce.core.protocol.CommandType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisStreamOps {
    private final RedisTemplate redisTemplate;
    public void ackStream(String consumerGroupName, MapRecord<String, Object, Object> message){
        this.redisTemplate.opsForStream().acknowledge(consumerGroupName, message);
    }
    public void claimStream(PendingMessage pendingMessage, String consumerName){
        RedisAsyncCommands commands = (RedisAsyncCommands) this.redisTemplate
                .getConnectionFactory().getConnection().getNativeConnection();

        CommandArgs<String, String> args = new CommandArgs<>(StringCodec.UTF8)
                .add(pendingMessage.getIdAsString())
                .add(pendingMessage.getGroupName())
                .add(consumerName)
                .add("20")
                .add(pendingMessage.getIdAsString());
        commands.dispatch(CommandType.XCLAIM, new StatusOutput(StringCodec.UTF8), args);
    }
    public List<MapRecord<String, Object, Object>> findStreamMessageByRange(String streamKey, String startId, String endId){
        return this.redisTemplate.opsForStream().range(streamKey, Range.closed(startId, endId));
    }

    // Range 조회를 활용한 message 단 건 조회
    public MapRecord<String, Object, Object> findStreamMessageById(String streamKey, String id){
        List<MapRecord<String, Object, Object>> mapRecordList = this.findStreamMessageByRange(streamKey, id, id);
        if(mapRecordList.isEmpty()) return null;
        return mapRecordList.get(0);
    }
    public void createStreamConsumerGroup(String streamKey, String consumerGroupName){
        // Stream이 존재 하지 않으면, MKSTREAM 옵션을 통해 만들고, ConsumerGroup또한 생성한다
        if (Boolean.FALSE.equals(this.redisTemplate.hasKey(streamKey))){
            RedisAsyncCommands commands = (RedisAsyncCommands) this.redisTemplate
                    .getConnectionFactory()
                    .getConnection()
                    .getNativeConnection();

            CommandArgs<String, String> args = new CommandArgs<>(StringCodec.UTF8)
                    .add(CommandKeyword.CREATE)
                    .add(streamKey)
                    .add(consumerGroupName)
                    .add("0")
                    .add("MKSTREAM");

            commands.dispatch(CommandType.XGROUP, new StatusOutput(StringCodec.UTF8), args);
        }
        // Stream 존재시, ConsumerGroup 존재 여부 확인 후 ConsumerGroupd을 생성한다
        else{
            if(!isStreamConsumerGroupExist(streamKey, consumerGroupName)){
                this.redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.from("0"), consumerGroupName);
            }
        }
    }

    // ConsumerGroup 존재 여부 확인
    public boolean isStreamConsumerGroupExist(String streamKey, String consumerGroupName){
        Iterator<StreamInfo.XInfoGroup> iterator = this.redisTemplate
                .opsForStream().groups(streamKey).stream().iterator();

        while(iterator.hasNext()){
            StreamInfo.XInfoGroup xInfoGroup = iterator.next();
            if(xInfoGroup.groupName().equals(consumerGroupName)){
                return true;
            }
        }
        return false;
    }
    public StreamMessageListenerContainer createStreamMessageListenerContainer(){
        return StreamMessageListenerContainer.create(
                this.redisTemplate.getConnectionFactory(),
                StreamMessageListenerContainer
                        .StreamMessageListenerContainerOptions.builder()
                        .hashKeySerializer(new StringRedisSerializer())
                        .hashValueSerializer(new StringRedisSerializer())
                        .pollTimeout(Duration.ofMillis(20))
                        .build()
        );
    }

    public void readStream(String keyName) {
        redisTemplate.opsForStream()
                .read(StreamOffset.latest(keyName));
    }
}
