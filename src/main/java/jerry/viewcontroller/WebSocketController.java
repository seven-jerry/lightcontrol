package jerry.viewcontroller;

import jerry.interaction.AbstractStateNotifier;
import jerry.consumer.WebSocketConsumer;
import jerry.interaction.AbstractInteractionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
@Qualifier("webSocket")
@CrossOrigin
@Slf4j
public class WebSocketController extends TextWebSocketHandler {


    @Autowired
    AbstractInteractionManager clientInteractionManager;

    @Autowired
    @Qualifier("contextAwareClientStateNotifier")
    AbstractStateNotifier notifier;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            clientInteractionManager.start();
        } catch (Exception e) {
            session.sendMessage(new TextMessage("{\"error\":\"" + e.getMessage() + "\"}"));
            System.out.println("websocket" + e.getMessage());
            return;
        }
        notifier.addConsumer(new WebSocketConsumer(session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        notifier.removeConsumer(new WebSocketConsumer(session));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws IOException {
        try {
            log.trace(textMessage.getPayload());
            notifier.handleConsumerMessage(textMessage);
        } catch (Exception e) {
            session.sendMessage(new TextMessage("{\"error\":\"" + e.getMessage() + "\"}"));
            log.error(e.getMessage());
        }
    }


}