package jerry.master;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jerry.consumer.ClientRequestMessage;
import jerry.consumer.ClientState;
import jerry.pojo.Command;
import jerry.service.ClientStateRepository;
import jerry.service.IClientStateChangeNotifiable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ClientStateUpdater {

    private ConcurrentHashMap<WebSocketSession, ClientStateRepository> clientStates = new ConcurrentHashMap<>();
    private IMasterChangeNotifiable notifiable;

    public String getClientStateJson(String... keys) {
        Map<String, String> map = new HashMap<>();
        clientStates.forEach((k, v) -> {
            map.put(k.getRemoteAddress().getHostName(), v.getStateJson(keys));
        });
        return new Gson().toJson(map);
    }

    public void setNotifier(IMasterChangeNotifiable notifer) {
        this.notifiable = notifer;
    }


    public void hasUpdatedClientState(WebSocketSession host, String... keys) {
        Map<String, String> map = new HashMap<>();
        String state = clientStates.get(host).getStateJson(keys);
        map.put(host.getRemoteAddress().getHostName(), state);
        notifiable.hasUpdatedClientState(new Gson().toJson(map));
    }

    public void handleStateUpdate(WebSocketSession session, TextMessage textMessage) {
        ClientStateRepository repository = this.get(session);
        if (repository == null) {
            repository = this.initFromMessage(textMessage.getPayload());
            if (repository == null) {
                try {
                    session.sendMessage(new TextMessage(ClientRequestMessage.fetchString()));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
                return;
            }
            repository.setNotifier(new HostAwareChangeNotifiable(session));
            clientStates.put(session, repository);
            this.hasUpdatedClientState(session);
            return;
        }
        this.partialUpdate(repository, textMessage);
    }

    private void partialUpdate(ClientStateRepository repository, TextMessage textMessage) {
        JsonObject jsonObject = new Gson().fromJson(textMessage.getPayload(), JsonObject.class);

        if (jsonObject.has(ClientState.COMMANDS)) {
            repository.updateCommands(new Gson().fromJson(jsonObject.get(ClientState.COMMANDS), Command[].class));
        }
        if (jsonObject.has(ClientState.INPUT_STATE)) {
            repository.updateInputState(jsonObject.get(ClientState.INPUT_STATE).getAsString());
        }
        if (jsonObject.has(ClientState.OUTSIDE_STATE)) {
            repository.updateOutsideState(jsonObject.get(ClientState.OUTSIDE_STATE).getAsString());
        }
        if (jsonObject.has(ClientState.OUTPUT_STATE)) {
            repository.updateOutputState(jsonObject.get(ClientState.OUTPUT_STATE).getAsString());
        }


    }

    public void remove(WebSocketSession session) {
        this.clientStates.remove(session);
    }

    private ClientStateRepository get(WebSocketSession host) {
        if (clientStates.containsKey(host)) {
            return clientStates.get(host);
        }

        return null;

    }

    private ClientStateRepository initFromMessage(String message) {
        JsonObject stateEl = new Gson().fromJson(message, JsonObject.class);

        if (!ClientState.MESSAGE_TYPE_FULL.equals(stateEl.get(ClientState.MESSAGE_TYPE).getAsString())) {
            return null;
        }
        ClientState state = ClientState.fromJson(stateEl);
        return ClientStateRepository.fromState(state);
    }

    public Map<String, ClientState> getClientStateMap() {
        Map<String, ClientState> map = new HashMap<>();
        this.clientStates.forEach((k, v) -> {
                    map.put(k.getRemoteAddress().getHostName(), v.getState());
                }
        );
        return map;
    }

    public void writeToHost(String host, String value) {
        clientStates.keySet().stream()
                .filter(s -> s.getRemoteAddress().getHostName().equals(host))
                .forEach(session -> {
                    try {
                        session.sendMessage(new TextMessage(ClientRequestMessage.changeString(value)));
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                });
    }


    public class HostAwareChangeNotifiable implements IClientStateChangeNotifiable {

        WebSocketSession host;

        HostAwareChangeNotifiable(WebSocketSession host) {
            this.host = host;
        }

        @Override
        public void hasUpdatedClientState(String... keys) {
            ClientStateUpdater.this.hasUpdatedClientState(host, keys);
        }
    }
}

