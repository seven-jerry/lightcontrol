package jerry.interaction;

import jerry.service.PersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class ReadManager implements ILIfeCycleExposable {


    @Autowired
    @Qualifier("contextAwareClientStateNotifier")
    AbstractStateNotifier abstractStateNotifier;

    @Autowired
    PersistenceService service;

    @Value("${base.folder}")
    public String settingsFolder;

    @Autowired
    public AbstractInteractionManager clientInteractionManager;

    private InputControl inputControl = InputControl.AUTO;

    private InputCommand command;


    @Override
    public void startLifecycle() throws RuntimeException {
        command = service.getSetting().getInputCommand();
    }

    @Override
    public void stopLifeCycle() {
        if (command != null)
            command.resetCondition();
    }

    //    @Override
    public void handleMessage(StateArray message) {
        log.trace(message.toString());
        command.testCondition(message, inputControl)
                .ifPresent(e -> clientInteractionManager.writeToProducer(e.getCommand()));

    }

    //   @Override
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
