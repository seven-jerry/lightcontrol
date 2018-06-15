package jerry.rest;

import jerry.arduino.ArduinoController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
    ArduinoController arduinoController;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        arduinoController.addConsumer(session);
        if(arduinoController.hasStarted()== false){
            arduinoController.start();
        }
        arduinoController.heartBeat();
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        arduinoController.removeConsumer(session);
    }

        @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws IOException {
        arduinoController.handleWrite(session,textMessage);
    }
}