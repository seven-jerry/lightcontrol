package jerry.util;

import jerry.interaction.EventHandler;
import jerry.master.IWebSocketResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class WebsocketManager implements IWebsocketErrorHandler {

    @Autowired
    EventHandler eventHandler;

    private Map<String, WebsocketImpl> sockets = new ConcurrentHashMap<>();
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


    @Scheduled(initialDelay = 60_000, fixedDelay = 5_000)
    public synchronized void checkSessionActive() {
        for (Map.Entry<String, WebsocketImpl> socket : sockets.entrySet()) {
            if (this.socketInBadState(socket.getValue())) {
                socket.getValue().disconnect();
                reconnectSocket(socket.getValue().getId());
            }
        }
    }

    private boolean socketInBadState(WebsocketImpl socket) {
        return !socket.isConnected();
    }

    public String newSocket(String url, IWebSocketResponseHandler responseHandler) throws Exception {
        int id = random.nextInt(Integer.SIZE - 1);
        WebsocketImpl websocket = buildConnection(id, url, responseHandler);
        this.sockets.put(url, websocket);
        return url;
    }

    private WebsocketImpl buildConnection(WebsocketImpl websocket) throws Exception {
       return buildConnection(websocket.getId(), websocket.getUrl(), websocket.getResponseHandler());
    }

    public synchronized  void disconnectSocket(int id) {
        WebsocketImpl websocket = sockets.get(id);
        websocket.disconnect();
        sockets.remove(id);
    }

    public void writeToSocket(String url, String message) throws Exception {
        sockets.get(url).writeMessage(message);
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
        log.warn(id + " has closed : "+reason);
       reconnectSocket(id);

    }

    @Override
    public void socketErrorReceived(Integer id, Throwable t) {
        log.warn(id + " has received error : "+t.toString());
    }

    private void reconnectSocket(Integer id){
        try {
            WebsocketImpl websocket = sockets.get(id);
            sockets.replace(websocket.getUrl(), buildConnection(websocket));
        } catch (Exception e){
            eventHandler.pushMessage(EventHandler.Type.ERROR,"could not create socket after closed");
        }
    }


}
