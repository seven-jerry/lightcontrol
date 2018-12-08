package jerry.interaction;

import jerry.arduino.*;
import jerry.service.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class ReadManager implements IReadUpdateable, ILIfeCycleExposable {


    @Autowired
    StateNotifier notifier;

    @Autowired
    PersistenceService service;

    private InputCommand command;

    private AtomicReference<StateArray> state = new AtomicReference<>(new StateArray(0,0,0));

    @Override
    public void startLifecycle() throws RuntimeException {
        command = service.getSetting().getInputCommand();
    }

    @Override
    public void stopLifeCycle() {
        command.resetCondition();
    }

    @Override
    public synchronized void handleMessage(StateArray message) {
        int currentState = state.get().stateHashCode();
        int nextState = message.stateHashCode();
        state.set(message);
        System.out.println("currentState : "+currentState + " nextState : "+nextState);
        if(currentState != nextState) {
            command.testCondition(message).ifPresent(e -> notifier.produceOnce(e.getCommand()));
        }
    }

    public StateArray getLastState(){
        return this.state.get();
    }
    @Override
    public void handleError(String message) {
        System.out.println("Error :"+message);
        command.resetCondition();
    }
}
