package jerry.util;

import com.google.gson.JsonObject;
import jerry.consumer.ClientState;
import jerry.consumer.IConsumer;
import jerry.interaction.EventHandler;
import jerry.interaction.ILIfeCycleExposable;
import jerry.master.IWebSocketResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;

import java.time.LocalDateTime;

@Slf4j
public abstract class AbstractWebsocket implements ILIfeCycleExposable, IWebSocketResponseHandler, IConsumer {


    protected Integer socketId;

    @Override
    public synchronized void startLifecycle() throws RuntimeException {
        if (!hasValidUrl()) return;
        connectSocket();
    }

    private void connectSocket() {
        try {
            if(socketId != null){
                getWebsocketManager().checkSessionActive();
                return;
            }
            socketId = getWebsocketManager().newSocket(getUrl(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public synchronized void stopLifeCycle() {
        this.closeConnection();
    }


    protected boolean hasValidUrl() {
        log.trace(this.getUrl());

        return this.getUrl() != null && !this.getUrl().isEmpty();
    }

    protected abstract String getUrl();

    public String pingPayload() {
        JsonObject object = new JsonObject();
        object.addProperty(ClientState.MESSAGE_TYPE, ClientState.MESSAGE_TYPE_PING);
        object.addProperty("time", LocalDateTime.now().toString());
        return object.toString();
    }

    @Override
    public abstract void onMessageFromSocket(Session session, String message);


    protected abstract EventHandler getEventHandler();


    @Override
    public void write(String message) {
        log.trace(message);
        try {
            getWebsocketManager().writeToSocket(socketId,message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void handleError(Exception e) {
        try {
            getWebsocketManager().writeToSocket(socketId,"{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e1){
            e1.printStackTrace();
        }
    }


    @Override
    public void closeConnection() {
        log.warn("");
        getWebsocketManager().disconnectSocket(socketId);
        socketId = null;
    }


    protected abstract WebsocketManager getWebsocketManager();
}

