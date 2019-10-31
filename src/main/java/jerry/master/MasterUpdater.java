package jerry.master;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jerry.consumer.ClientRequestMessage;
import jerry.consumer.ClientState;
import jerry.interaction.AbstractStateNotifier;
import jerry.consumer.IConsumer;
import jerry.interaction.AbstractInteractionManager;
import jerry.interaction.EventHandler;
import jerry.interaction.ILIfeCycleExposable;
import jerry.service.ClientStateRepository;
import jerry.service.PersistenceService;
import jerry.util.AbstractWebsocket;
import jerry.util.WebsocketImpl;
import jerry.util.Sleep;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MasterUpdater extends AbstractWebsocket {

    @Autowired
    PersistenceService service;

    @Autowired
    EventHandler eventHandler;

    @Autowired
    AbstractInteractionManager clientInteractionManager;

    @Autowired
    @Qualifier("contextAwareClientStateNotifier")
    AbstractStateNotifier abstractStateNotifier;


    @Autowired
    ClientStateRepository clientStateRepository;

    public String type = "local";

    public MasterUpdater(){
        setSendPing(true);
    }

    @Override
    protected String getUrl() {
        if (type.equals("local")) {
            return service.getSetting().getMasterUrl();
        }
        if (type.equals("internet")) {
            return service.getSetting().getMasterInternetUrl();
        }
        throw new IllegalStateException("could not get url from type");
    }


    @Override
    public void onMessageFromSocket(Session session, String message) {
        ClientRequestMessage requestMessage = transformMessage(message);
        switch (requestMessage.type) {
            case FETCH:
                this.socket.writeMessage(clientStateRepository.getStateJson(requestMessage.argumentsAsArray()));
                break;
            case CHANGE:
                clientInteractionManager.writeToProducer(requestMessage.argumentAsString());
                break;
        }
    }


    private ClientRequestMessage transformMessage(String message) {
        return new Gson().fromJson(message, ClientRequestMessage.class);
    }

    @Override
    public EventHandler getEventHandler(){
        return this.eventHandler;
    }

}
