package jerry.util;

import jerry.interaction.EventHandler;
import jerry.master.IWebSocketResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


@Component
@Slf4j
public class WebsocketManager implements IWebsocketErrorHandler {

    @Autowired
    EventHandler eventHandler;

    private Map<Integer, WebsocketImpl> sockets = new ConcurrentHashMap<>();
    private Map<Integer, ReentrantLock> locks = new ConcurrentHashMap<>();

    private WebSocketClient webSocketClient = new WebSocketClient();
    private Random random = new Random();

    public WebsocketManager() {
        webSocketClient.getHttpClient().setConnectTimeout(300);
        webSocketClient.getHttpClient().setIdleTimeout(1000);

        try {
            webSocketClient.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Scheduled(initialDelay = 60_000, fixedDelay = 20_000)
    public synchronized void checkSessionActive() {
        log.trace("checkSessionActive");
        for (Map.Entry<Integer, WebsocketImpl> socket : sockets.entrySet()) {
            if (this.socketInBadState(socket.getValue())) {
                socket.getValue().disconnect();
                reconnectSocket(socket.getValue().getId());
                continue;
            }
            try {
                socket.getValue().writeMessage(socket.getValue().getResponseHandler().pingPayload());
            } catch (Exception e) {
                socket.getValue().disconnect();
                reconnectSocket(socket.getValue().getId());
            }

        }
    }

    private boolean socketInBadState(WebsocketImpl socket) {
        return !socket.isConnected();
    }

    public Integer newSocket(String url, IWebSocketResponseHandler responseHandler) throws Exception {
        if (url == null) {
            return null;
        }
        int id = random.nextInt(Integer.SIZE - 1);

        try {
            this.locks.put(id, new ReentrantLock());
            this.locks.get(id).lock();
            WebsocketImpl websocket = buildConnection(id, url, responseHandler);
            this.sockets.put(id, websocket);
        } finally {
            this.locks.get(id).unlock();
        }
        return id;

    }

    private WebsocketImpl buildConnection(WebsocketImpl websocket) throws Exception {
        return buildConnection(websocket.getId(), websocket.getUrl(), websocket.getResponseHandler());
    }

    public synchronized void disconnectSocket(int id) {
        locks.get(id).lock();
        WebsocketImpl websocket = sockets.get(id);
        websocket.disconnect();
        sockets.remove(id);
        locks.get(id).unlock();
    }

    public void writeToSocket(Integer id, String message) throws Exception {
        if (message == null) {
            return;
        }
        locks.get(id).lock();
        sockets.get(id).writeMessage(message);
        locks.get(id).unlock();
    }


    private WebsocketImpl buildConnection(Integer id, String url, IWebSocketResponseHandler responseHandler) throws Exception {

        WebsocketImpl websocket = new WebsocketImpl(id, this, eventHandler, responseHandler, url);
        URI echoUri = new URI(url);
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        Session session = webSocketClient.connect(websocket, echoUri, request).get(20, TimeUnit.SECONDS);
        if (!session.isOpen()) {
            throw new IllegalStateException("socket is not open after 10 seconds");
        }
        if (session != websocket.getSession()) {
            throw new IllegalStateException("socket session is not equal");
        }
        return websocket;
    }

    public void stop() {
        try {
            webSocketClient.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void socketHasClosed(Integer id, int statusCode, String reason) {
        log.warn(id + " has closed : " + reason);
        reconnectSocket(id);

    }

    @Override
    public void socketErrorReceived(Integer id, Throwable t) {
        log.warn(id + " has received error : " + t.toString());
        reconnectSocket(id);

    }

    private void reconnectSocket(Integer id) {
        locks.get(id).lock();
        try {
            WebsocketImpl websocket = sockets.get(id);
            websocket.disconnect();
            sockets.replace(id, buildConnection(websocket));
        } catch (Exception e) {
            eventHandler.pushMessage(EventHandler.Type.ERROR, "could not create socket after closed");
            sockets.get(id).disconnect();
        } finally {
            locks.get(id).unlock();
        }
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }


}
