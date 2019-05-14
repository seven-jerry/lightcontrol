package jerry.master;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jerry.consumer.ClientRequestMessage;
import jerry.consumer.ClientState;
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
public class ClientStateUpdater implements IClientStateChangeNotifiable {

    private ConcurrentHashMap<String, ClientStateRepository> clientStates = new ConcurrentHashMap<>();

    @Override
    public void hasUpdatedClientState(String... keys) {

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
            clientStates.put(session.getRemoteAddress().getHostName(), repository);
        }

    }

    public void remove(WebSocketSession session) {
        this.clientStates.remove(session.getRemoteAddress().getHostName());
    }

    private ClientStateRepository get(WebSocketSession session) {

        String host = session.getRemoteAddress().getHostName();

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
        ClientState state = new Gson().fromJson(message, ClientState.class);
        return ClientStateRepository.fromState(state);
    }

    public Map<String, ClientState> getClientStates() {
        Map<String, ClientState> map = new HashMap<>();
        this.clientStates.forEach((k, v) -> {
                    map.put(k, v.getState());
                }
        );
        return map;
    }
}

