package jerry.viewcontroller;

import jerry.arduino.StateNotifier;
import jerry.arduino.WebSocketComsumer;
import jerry.interaction.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
@CrossOrigin
public class WebSocketController extends TextWebSocketHandler {


    @Autowired
    Controller lifeCycleController;

    @Autowired
    StateNotifier notifier;

    @Autowired
    InteractionController controller;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            lifeCycleController.start();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("websocket"+e);
        }
    notifier.addConsumer(new WebSocketComsumer(session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        notifier.removeConsumer(new WebSocketComsumer(session));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws IOException {
            notifier.produceOnce(textMessage.getPayload());
    }
}