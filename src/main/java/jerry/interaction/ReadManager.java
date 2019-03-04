package jerry.interaction;

import jerry.arduino.*;
import jerry.service.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ReadManager implements IReadUpdateable, ILIfeCycleExposable {


    @Autowired
    StateNotifier notifier;

    @Autowired
    PersistenceService service;

    @Value("${base.folder}")
    public String settingsFolder;


    private InputControl inputControl = InputControl.AUTO;

    private InputCommand command;

    private AtomicReference<StateArray> state = new AtomicReference<>(new StateArray(0, 0, 0));

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
        writeToDisk(message);
        if (currentState != nextState) {
            command.testCondition(message, notifier.lastUpdated(),inputControl).ifPresent(e -> notifier.produceOnce(e.getCommand()));
        }
    }

    private void writeToDisk(StateArray message){
        try {
            File file = new File(settingsFolder+"/analogstate.log");
            FileWriter fr = new FileWriter(file, true);
            for(String k : message.getAnalogInputs().keySet()){
                String v = message.getAnalogInputs().get(k);
                fr.append(LocalDateTime.now()+";"+k +";"+ v+";\n");
            }
            fr.close();
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public StateArray getLastState() {
        return this.state.get();
    }

    @Override
    public void handleError(String message) {
        System.out.println("Error :" + message);
        command.resetCondition();
    }


    public InputControl getInputControl() {
        return inputControl;
    }

    public void setInputControl(InputControl inputControl) {
        this.inputControl = Objects.requireNonNull(inputControl);
    }
}
