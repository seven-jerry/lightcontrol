package jerry.master;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import jerry.util.Sleep;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MasterUpdater implements ILIfeCycleExposable, IMasterResponseHandler, IConsumer {

    @Autowired
    PersistenceService service;

    @Autowired
    EventHandler eventHandler;

    @Autowired
    AbstractInteractionManager clientInteractionManager;

    @Autowired
    @Qualifier("contextAwareClientStateNotifier")
    AbstractStateNotifier abstractStateNotifier;

    private MasterUpdateSocket socket;

    @Autowired
    ClientStateRepository clientStateRepository;

    public String type = "local";

    @Override
    public synchronized void startLifecycle() throws RuntimeException {
        checkConnection();
    }

    @Override
    public synchronized void stopLifeCycle() {

    }

    public synchronized void checkConnection() {
        log.trace("connecting");
        if (this.socket != null && this.socket.isConnected()) {
            log.trace("connected - pinging");
            socket.writeMessage(pingMessage());
            return;
        }
        if (socket == null) {
            socket = new MasterUpdateSocket(eventHandler, this);
        }
        if (!socket.isConnected()) {
            log.debug("connecting to master");
            socket.connect(this.getUrl());
            abstractStateNotifier.addConsumer(this);
            Sleep.sleep(2000);
            write(clientStateRepository.getStateJson());
            log.trace("connected to master");
            return;
        }

    }

    private String getUrl() {
        if (type.equals("local")) {
            return service.getSetting().getMasterUrl();
        }
        if(type.equals("internet")){
            return service.getSetting().getMasterInternetUrl();
        }
        throw new IllegalStateException("could not get url from type");
    }

    private String pingMessage() {
        JsonObject object = new JsonObject();
        object.addProperty(ClientState.MESSAGE_TYPE, ClientState.MESSAGE_TYPE_PING);
        object.addProperty("time", LocalDateTime.now().toString());
        return object.toString();
    }

    @Override
    public void onMessageFromMaster(Session session, String message) {
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
    public void write(String message) {
        log.trace(message);
        if (!this.socket.isConnected()) {
            log.error("not connected");
            return;
        }
        this.socket.writeMessage(message);
    }

    @Override
    public void handleError(Exception e) {
        socket.writeMessage("{\"error\":\"" + e.getMessage() + "\"}");
    }

    @Override
    public void closeConnection() {
        log.warn("");
        socket.disconnect();
    }
}
