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


    protected boolean sendPing;
    protected Integer socketId;

    public void setSendPing(boolean sendPing) {
        this.sendPing = sendPing;
    }


    @Override
    public synchronized void startLifecycle() throws RuntimeException {
        if (pingIfConnected()) return;
        if (!hasValidUrl()) return;
        connectSocket();
    }

    private void connectSocket() {
        try {
            socketId = getWebsocketManager().newSocket(getUrl(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean pingIfConnected() {
        if (this.socketId != null) {
            this.ping();
            return true;
        }
        return false;
    }


    protected void ping() {
        if (!this.sendPing) {
            log.trace(getUrl() + " not pinging");
            return;
        }

        log.trace("connected - pinging");
        try {
            getWebsocketManager().writeToSocket(socketId, pingMessage());
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

    private String pingMessage() {
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
        this.stopLifeCycle();
    }


    protected abstract WebsocketManager getWebsocketManager();
}

