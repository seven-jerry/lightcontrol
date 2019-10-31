package jerry.master;

import org.eclipse.jetty.websocket.api.Session;

public interface IWebSocketResponseHandler {
    void onMessageFromSocket(Session session, String message);
}
