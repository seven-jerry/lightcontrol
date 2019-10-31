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


    protected WebsocketImpl socket;
    protected boolean sendPing;


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
        if (socket == null)
            socket = new WebsocketImpl(getEventHandler(), this, this.getUrl());
        if (socket.isConnected()) return;

        try {
            socket.connect();
        } catch (Exception e) {
            log.error("could not connect to " + this.getUrl() + " : " + e.toString());
        }
    }

    private boolean pingIfConnected() {
        if (this.socket != null && this.socket.isConnected()) {
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
        socket.writeMessage(pingMessage());
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
        this.socket.writeMessage(message);
    }

    @Override
    public void handleError(Exception e) {
        socket.writeMessage("{\"error\":\"" + e.getMessage() + "\"}");
    }


    @Override
    public void closeConnection() {
        log.warn("");
        this.stopLifeCycle();
    }
}

