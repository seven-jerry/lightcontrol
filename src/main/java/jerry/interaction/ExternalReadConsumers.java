package jerry.interaction;

import jerry.master.IWebSocketResponseHandler;
import jerry.pojo.Setting;
import jerry.service.PersistenceService;
import jerry.util.AbstractWebsocket;
import jerry.util.WebsocketImpl;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ExternalReadConsumers implements ILIfeCycleExposable{

    @Autowired
    ReadManager readManager;

    @Autowired
    NodeUpdater nodeRedSocket;

    @Autowired
    PersistenceService persistentService;

    @Autowired
    EventHandler eventHandler;


    public synchronized void tryAutoStart() {

        if (readManager.getInputControl() == InputControl.ISLAND_MODE ||
                readManager.getInputControl() == InputControl.HOME_ASSIST) {
            log.trace("connecting");
            nodeRedSocket.startLifecycle();
        }
        log.trace("connected");
    }

    public void handleMessage(StateArray message) {
        List<NodeRedSensorState> sensorStates = transformMessage(message);

        if (readManager.getInputControl() == InputControl.ISLAND_MODE) {
            this.updateLocalNodeRed(sensorStates);
        }
        if (readManager.getInputControl() == InputControl.HOME_ASSIST) {
            this.updateLocalNodeRed(sensorStates);
            this.updateHomeAssist(sensorStates);
        }
    }

    private void updateLocalNodeRed(List<NodeRedSensorState> messages) {
            for (NodeRedSensorState message : messages)
                nodeRedSocket.write(message.toString());

    }


    private void updateHomeAssist(List<NodeRedSensorState> messages) {
        if (persistentService.getSetting().getHomeAssistHttpEndpoint() == null) {
            eventHandler.pushMessage(EventHandler.Type.ERROR, "could not update home assist. setting not found");
            return;
        }
        Setting setting = persistentService.getSetting();
        for (NodeRedSensorState message : messages) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJjYTdkZTFmYmRjNDM0NjY2YTUzMThjMzNlMzVmYWU5YyIsImlhdCI6MTU3MjExMzU3NiwiZXhwIjoxODg3NDczNTc2fQ.RbLj9ev8bQXoOpdKHs78dUCFywMctW38YDWDbShC1Es");
                HttpEntity<String> request = new HttpEntity<String>(message.toString(), headers);
                RestTemplate template = new RestTemplate();
                String url = setting.getHomeAssistHttpEndpoint() + "/api/states/sensor." + setting.getName() + "_" + message.getFriendlyName();
                eventHandler.pushMessage(EventHandler.Type.TRACE, "sending to home assist " + url);
                template.postForLocation(url, request);
            } catch (Exception e) {
                eventHandler.pushMessage(EventHandler.Type.ERROR, "error while posting to home assist " + e.getMessage());
            }
        }

    }

    private List<NodeRedSensorState> transformMessage(StateArray message) {
        List<NodeRedSensorState> sensorStates = new ArrayList<>();

        Setting setting = persistentService.getSetting();
        Map<String, Integer> state = setting.labeledInput(message);

        for (Map.Entry<String, Integer> entry : state.entrySet()) {
            NodeRedSensorState sensorState = new NodeRedSensorState(entry.getValue(), null, entry.getKey());
            sensorStates.add(sensorState);
        }

        int count = message.countTurnedOnLights();
        NodeRedSensorState sensorState = new NodeRedSensorState(count, null,"Lichter");
        sensorStates.add(sensorState);

        return sensorStates;
    }



    public void disconnect() {
        this.nodeRedSocket.stopLifeCycle();
    }

    @Override
    public void startLifecycle() throws RuntimeException {
        tryAutoStart();
    }

    @Override
    public void stopLifeCycle() {
    this.nodeRedSocket.stopLifeCycle();
    }
}
