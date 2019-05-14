package jerry.master;


import jerry.interaction.AbstractStateNotifier;
import jerry.consumer.IConsumer;
import jerry.interaction.AbstractInteractionManager;
import jerry.interaction.EventHandler;
import jerry.interaction.ILIfeCycleExposable;
import jerry.service.ClientStateRepository;
import jerry.service.PersistenceService;
import jerry.util.Sleep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
            log.trace("connected");
            return;
        }
        if (socket == null) {
            socket = new MasterUpdateSocket(eventHandler, this);
        }
        if (!socket.isConnected()) {
            log.debug("connecting to master");
            socket.connect(service.getSetting().getMasterUrl());
            abstractStateNotifier.setMasterConsumer(this);
            Sleep.sleep(2000);
            write(clientStateRepository.getStateJson());
            log.trace("connected to master");

        }
    }


    @Override
    public void onMessageFromMaster(String message) {
        clientInteractionManager.writeToProducer(message);
    }

    @Override
    public void write(String message) {
        log.trace(message);
        if(!this.socket.isConnected()){
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
