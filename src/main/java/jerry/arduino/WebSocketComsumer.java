package jerry.arduino;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class WebSocketComsumer implements IConsumer {

    private WebSocketSession webSocket;

    public WebSocketComsumer(WebSocketSession session){
        this.webSocket = session;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj.getClass() != WebSocketComsumer.class) return false;
        return ((WebSocketComsumer)obj).webSocket.equals(this.webSocket);
    }

    public void handleError(Exception error) {
        try {
            webSocket.sendMessage(new TextMessage(error.getMessage()));
            webSocket.close();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }


    public void write(String content) throws Exception{
        webSocket.sendMessage(new TextMessage(content));
    }


}
