package jerry.interaction;

import jerry.pojo.StateCommandOverwrite;
import jerry.service.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StateCommandUpdater implements ILIfeCycleExposable{

    final PersistenceService service;

    @Autowired
    public StateCommandUpdater(PersistenceService service) {
        this.service = service;
    }

    @Override
    public void startLifecycle() throws RuntimeException {
        service.getStateCommandOverwrites().forEach(StateCommandOverwrite::doOverwrite);
    }

    @Override
    public void stopLifeCycle() {
        Stream.of(StateCommand.values()).forEach(StateCommand::resetInitialConsumer);
    }
}
