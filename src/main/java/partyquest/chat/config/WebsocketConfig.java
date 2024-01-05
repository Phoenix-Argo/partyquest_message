package partyquest.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * 클라이언트에서 서버로 소켓을 연결하는 경로이다.
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/tutorial-websocket");
    }

    /**
     * registry.enableSimpleBroker -> 구독할 채널을 의미한다.
     * registry.setApplicationDestinationPrefixes -> StompClient의 publisher가 발행 요청을 할 컨트롤러
     * @MessageMapping("/hello")과 매핑 된다.
     * {@link partyquest.chat.tutorial.TutorialController}
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/party-quest-chat");
    }
}
