package jerry.util;

import jerry.interaction.IMessageable;
import jerry.master.IWebSocketResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@WebSocket(maxTextMessageSize = 64 * 1024)
@Slf4j
public final class WebsocketImpl {

    ReadWriteLock lock = new ReentrantReadWriteLock();

    LocalDateTime lastUpdated = LocalDateTime.now();
    private volatile Session session;
    private WebSocketClient webSocketClient = new WebSocketClient();
    private final IMessageable messageReciever;
    private final IWebSocketResponseHandler handler;
    private final String url;

    public WebsocketImpl(IMessageable messageable, IWebSocketResponseHandler handler, String url) {
        this.messageReciever = messageable;
        this.handler = handler;
        this.url = Objects.requireNonNull(url);
    }

    public boolean isConnected() {
        lock.readLock().lock();
        boolean is = session != null;
        lock.readLock().unlock();
        return is;
    }

    public void connect() throws Exception {
        try {
            lock.writeLock().lock();
            if(session != null){
                lock.writeLock().unlock();
                return;
            }
            log.trace("connecting to " + url);
            webSocketClient.getHttpClient().setConnectTimeout(300);
            webSocketClient.getHttpClient().setIdleTimeout(1000);
            webSocketClient.start();

            URI echoUri = new URI(this.url);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            this.session = webSocketClient.connect(this, echoUri, request).get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("could not connect to master socket " + url + " reason: " + e.getMessage());
            messageReciever.pushMessage("could not connect to master socket " + url + " reason: " + e.getMessage());
            throw e;
        } finally {
            lock.writeLock().unlock();

        }

    }

    public synchronized void disconnect() {
        try {
            lock.writeLock().lock();
            webSocketClient.stop();
            this.session = null;
        } catch (Exception e) {
            log.error("could not disconnect from socket." + url + "reason: " + e.toString());
            messageReciever.pushMessage("could not disconnect from socket. reason: " + e.toString());
        } finally {
            lock.writeLock().unlock();
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        this.session = null;
        log.error("websocket " + url + " has been disconnected  : " + reason);
        messageReciever.pushMessage("websocket " + url + " has been disconnected  : " + reason);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        log.trace("setting session for " + url + " : " + session);
        this.session = session;
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        log.trace("websocket " + url + " message from session " + session + " : " + msg);
        this.handler.onMessageFromSocket(this.session, msg);
    }

    @OnWebSocketError
    public void onWebSocketError(Throwable t) {
        log.error("websocket " + url + " error " + session + " : " + t.toString());
        messageReciever.pushMessage(t.getMessage());
    }

    public synchronized void writeMessage(String message) {
        log.trace("websocket writeMessage " + url + " " + session + " : " + message);
        try {
            lock.readLock().lock();
            session.getRemote().sendString(message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("websocket" + url + "error writing message  : " + e.getMessage());
            messageReciever.pushMessage("websocket" + url + "error writing message  : " + e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
    }

}
