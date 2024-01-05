# redis
- redis-cli --raw로 실행하면 한글 안깨진다.

# spring-redis-stream
```text
listenerContainer.receive(StreamOffset.latest(streamKey), this);에서
StreamOffset.latest(streamKey) -> 최근걸로 가져온다.
StreamOffset.fromStart(streamKey) -> 해당키의 모든 스트림 데이터를 다 가져온다.
----
이런식으로 컨슈머 그룹과 컨슈머 이름으로 관리를 해주어야 중복처리 하지 않는다.
this.listenerContainer.receive(
                    Consumer.from(groupName, "name"),
                    StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                    this
            );

아래와 같이 처리하면 계속해서 중복되어 하나의 스트림 레코드가 등록되면 중복해서 2,3,4번 응답을 내려준다.
this.listenerContainer.receive(
                    StreamOffset.latest(streamKey),
                    this
            );
```