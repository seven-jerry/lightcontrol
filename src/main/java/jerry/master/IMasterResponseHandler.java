package jerry.master;

import org.eclipse.jetty.websocket.api.Session;

public interface IMasterResponseHandler {
    void onMessageFromMaster(Session session, String message);
}
