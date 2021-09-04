package jerry.interaction;

import jerry.service.PersistenceService;
import jerry.util.AbstractWebsocket;
import jerry.util.WebsocketImpl;
import jerry.util.WebsocketManager;
import org.eclipse.jetty.websocket.api.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class NodeUpdater extends AbstractWebsocket {

    @Autowired
    EventHandler eventHandler;


    @Autowired
    PersistenceService persistentService;


    @Autowired
    @Qualifier("contextAwareClientStateNotifier")
    AbstractStateNotifier notifier;

    @Autowired
    WebsocketManager websocketManager;

    public NodeUpdater(){
    }

    @Override
    protected String getUrl() {
        if (persistentService.getSetting().getLocalNodeRedWebsocket() == null) {
            eventHandler.pushMessage(EventHandler.Type.ERROR, "could not update home assist. setting not found");
            return null;
        }
        return persistentService.getSetting().getLocalNodeRedWebsocket();
    }

    @Override
    public void onMessageFromSocket(Session session, String message) {
        notifier.handleConsumerMessage(message);
    }

    @Override
    protected EventHandler getEventHandler() {
        return eventHandler;
    }

    @Override
    protected WebsocketManager getWebsocketManager(){
        return websocketManager;
    }
}