package jerry.interaction;

import jerry.pojo.Setting;
import jerry.service.PersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
    EventHandler eventHandler;


    @Autowired
    public AbstractInteractionManager clientInteractionManager;

    @Autowired
    ExternalReadConsumers externalReadConsumers;

    private InputControl inputControl = InputControl.HOME_ASSIST;

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


    public void handleMessage(StateArray message) {
        log.trace(message.toString());

        if(shouldCallExternalConsumers()){
            externalReadConsumers.handleMessage(message);
            return;
        }
        localReadHandler(message);

    }

    private boolean shouldCallExternalConsumers() {
        return inputControl == InputControl.ISLAND_MODE || inputControl == InputControl.HOME_ASSIST;
    }

    private void localReadHandler(StateArray message) {
        command.testCondition(message, inputControl)
                .ifPresent(e -> clientInteractionManager.writeToProducer(e.getCommand()));
    }


    public InputControl getInputControl() {
        return inputControl;
    }

    public void setInputControl(InputControl inputControl) {
        if(this.shouldCallExternalConsumers()){
            externalReadConsumers.disconnect();
        }
        this.inputControl = Objects.requireNonNull(inputControl);
        externalReadConsumers.tryAutoStart();
    }
}
