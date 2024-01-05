# redis
- redis-cli --raw로 실행하면 한글 안깨진다.

# spring-redis-stream
```text
listenerContainer.receive(StreamOffset.latest(streamKey), this);에서
StreamOffset.latest(streamKey) -> 최근걸로 가져온다.
StreamOffset.fromStart(streamKey) -> 해당키의 모든 스트림 데이터를 다 가져온다.
```