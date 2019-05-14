package jerry.service;

import jerry.interaction.AbstractStateNotifier;
import jerry.interaction.ILIfeCycleExposable;
import jerry.consumer.ClientState;
import jerry.pojo.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static jerry.util.ErrrorHandling.throwIf;

@Component
public class ClientStateRepository implements ILIfeCycleExposable {


    IClientStateChangeNotifiable notifier;

    public static ClientStateRepository fromState(ClientState state) {
        ClientStateRepository repository = new ClientStateRepository();
        repository.state = state;
        return repository;
    }

    public void setNotifier(IClientStateChangeNotifiable notifier){
        this.notifier = notifier;
    }

    @Autowired
    protected PersistenceService persistenceService;

    protected ClientState state;


    @Override
    public void startLifecycle() throws RuntimeException {
        throwIf(state, Objects::nonNull);
        state = ClientState.fromSetting(persistenceService.getSetting());
    }

    @Override
    public void stopLifeCycle() {
        state = null;
    }

    public String getStateJson(String... keys) {
        return state.getJson(keys);
    }

    public ClientState getState() {
        return state;
    }


    public boolean updateInputState(String message) {
        if (state == null) return false;
        Objects.requireNonNull(message);
        boolean hasUpdated = !state.getInputState().equals(message);
        state.setInputState(message);
        this.afterUpdate(hasUpdated, ClientState.INPUT_STATE);
        return hasUpdated;
    }


    public boolean updateOutputState(String message) {
        if (state == null) return false;
        Objects.requireNonNull(message);
        boolean hasUpdated = !state.getOutputState().equals(message);
        state.setOutputState(message);
        this.afterUpdate(hasUpdated, ClientState.OUTPUT_STATE);
        return hasUpdated;
    }

    public boolean updateOutsideState(String message) {
        if (state == null) return false;
        Objects.requireNonNull(message);
        boolean hasUpdated = !state.getOutsideState().equals(message);
        state.setOutsideState(message);
        this.afterUpdate(hasUpdated, ClientState.OUTSIDE_STATE);
        return hasUpdated;
    }


    public boolean updateCommands(List<Command> commands) {
        if (state == null) return false;
        Objects.requireNonNull(commands);
        boolean hasUpdated = !commands.equals(state.getSetting().getCommands());
        state.setCommands(commands);
        this.afterUpdate(hasUpdated, ClientState.COMMANDS);
        return hasUpdated;
    }


    protected void afterUpdate(boolean hasUpdated, String... keys) {
        if (!hasUpdated) return;
        notifier.hasUpdatedClientState(keys);
    }
}
