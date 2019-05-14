package jerry.viewcontroller;

import jerry.interaction.AbstractInteractionManager;
import jerry.master.ClientStateUpdater;
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
@Qualifier("masterSocket")
@CrossOrigin
public class MasterSocketController extends TextWebSocketHandler {


    @Autowired
    @Qualifier("contextAdjustingInteractionManager")
    AbstractInteractionManager clientInteractionManager;

    @Autowired
    ClientStateUpdater updater;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            clientInteractionManager.start();
        } catch (Exception e) {
            session.sendMessage(new TextMessage("{\"error\":\"" + e.getMessage() + "\"}"));
            System.out.println("websocket" + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        updater.remove(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws IOException {
        try {
            updater.handleStateUpdate(session, textMessage);
        } catch (Exception e) {
            session.sendMessage(new TextMessage("{\"error\":\"" + e.getMessage() + "\"}"));
            System.out.println("websocket" + e);
        }
    }


}