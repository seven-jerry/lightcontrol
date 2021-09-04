package jerry.util;

import jerry.interaction.IMessageable;
import jerry.master.IWebSocketResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.util.Objects;

@WebSocket(maxTextMessageSize = 64 * 1024)
@Slf4j
public final class WebsocketImpl {

    private Integer id;
    private Session session;
    private final IMessageable messageReciever;
    private final IWebsocketErrorHandler errorHandler;

    private final IWebSocketResponseHandler handler;
    private final String url;

    private volatile boolean isDisconnecting;

    public WebsocketImpl(Integer id,IWebsocketErrorHandler errorHandler, IMessageable messageable, IWebSocketResponseHandler handler, String url) {
        this.id = id;
        this.errorHandler = errorHandler;
        this.messageReciever = messageable;
        this.handler = handler;
        this.url = Objects.requireNonNull(url);
    }

    public boolean isConnected() {
        return session.isOpen();
    }

    public void disconnect() {
        try {
            this.isDisconnecting = true;
            this.session.close();
        } catch (Exception e) {
            log.error("could not disconnect from socket." + url + "reason: " + e.toString());
            messageReciever.pushMessage("could not disconnect from socket. reason: " + e.toString());
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        if(this.isDisconnecting){
            return;
        }
        errorHandler.socketHasClosed(id,statusCode,reason);
        log.error("websocket " + url + " has been disconnected  : " + reason);
        messageReciever.pushMessage("websocket " + url + " has been disconnected  : " + reason);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        log.trace("setting session for " + url + " : ");
        this.session = session;
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        log.trace("websocket " + url + " message from session "  + " : " + msg);
        this.handler.onMessageFromSocket(this.session, msg);
    }

    @OnWebSocketError
    public void onWebSocketError(Throwable t) {
        if(this.isDisconnecting){
            return;
        }
        errorHandler.socketErrorReceived(id,t);
        log.error("websocket " + url + " error "  + " : " + t.toString());
        messageReciever.pushMessage(t.getMessage());
    }

    public synchronized void writeMessage(String message) throws Exception {
        log.trace("websocket writeMessage " + url + " " + " : " + message);
        try {
            session.getRemote().sendString(message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("websocket" + url + "error writing message  : " + e.getMessage());
            messageReciever.pushMessage("websocket" + url + "error writing message  : " + e.getMessage());
            throw e;
        }
    }

    public String getUrl() {
        return url;
    }

    public IWebSocketResponseHandler getResponseHandler() {
        return this.handler;
    }

    public Session getSession() {
        return this.session;
    }

    public Integer getId() {
        return id;
    }
}
