package jerry.master;

import jerry.interaction.IMessageable;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.time.LocalDateTime;

@WebSocket(maxTextMessageSize = 64 * 1024)
@Slf4j
public final class MasterUpdateSocket {


    LocalDateTime lastUpdated = LocalDateTime.now();
    private Session session;
    private WebSocketClient webSocketClient = new WebSocketClient();
    private final IMessageable messageReciever;
    private final IMasterResponseHandler handler;

    public MasterUpdateSocket(IMessageable messageable, IMasterResponseHandler handler) {
        this.messageReciever = messageable;
        this.handler = handler;
    }

    boolean isConnected() {
        return session != null;
    }


    public void connect(String destination) {
        if(isConnected()) return;
        try {
            webSocketClient.getHttpClient().setConnectTimeout(300);
            webSocketClient.start();

            URI echoUri = new URI(destination);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            webSocketClient.connect(this, echoUri, request);
        } catch (Exception e) {
            log.error("could not connect to master socket " + destination + " reason: " + e.getMessage());
            messageReciever.pushMessage("could not connect to master socket " + destination + " reason: " + e.getMessage());
        }

    }
    public void disconnect(){
        try {
            webSocketClient.stop();
        } catch (Exception e){
            log.error("could not disconnect from socket. reason: " + e.getMessage());
            messageReciever.pushMessage("could not disconnect from socket. reason: " + e.getMessage());

        }
    }
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        this.session = null;
        log.error("client has been disconnected  : " + reason);
        messageReciever.pushMessage("client has been disconnected  : " + reason);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        this.handler.onMessageFromMaster(msg);
    }

    @OnWebSocketError
    public void onWebSocketError(Throwable t){
        messageReciever.pushMessage(t.getMessage());
    }
    public void writeMessage(String message) {
        try {
            session.getRemote().sendString(message);
        } catch (Exception e) {
            log.error("error writing message  : " + e.getMessage());
            messageReciever.pushMessage("error writing message  : " + e.getMessage());
        }
    }

}
