package jerry.interaction;


import jerry.master.MasterUpdater;
import jerry.service.ClientStateRepository;
import jerry.service.PersistenceService;
import jerry.pojo.Setting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

@Slf4j
public class ClientInteractionManager extends AbstractInteractionManager {

    @Autowired
    @Qualifier("contextAwareClientStateNotifier")
    AbstractStateNotifier notifier;
    @Autowired
    ReadManager readManager;

    @Autowired
    DeviceResponseHandler responseHandler;

    @Autowired
    PersistenceService persistenceService;

    @Autowired
    ClientStateRepository clientStateRepository;


    @Autowired
    StateCommandUpdater stateCommandUpdater;


    @Autowired
    MasterUpdater masterUpdater;

    @Autowired
    @Qualifier("internet")
    MasterUpdater internetUpdater;


    private StateDelegator stateDelegator;


    public ClientInteractionManager() {
        System.out.println("client");
    }


    @Override
    protected void onTryAutoStart() {
        log.debug("onTryAutoStart");
        masterUpdater.startLifecycle();
        internetUpdater.startLifecycle();
    }


    @Override
    protected void initLifeCycle() {
        this.lifeCycleManagedComponents.add(stateCommandUpdater);
        this.lifeCycleManagedComponents.add(clientStateRepository);
        this.lifeCycleManagedComponents.add(notifier);
        this.lifeCycleManagedComponents.add(readManager);
        this.lifeCycleManagedComponents.add(masterUpdater);
        this.lifeCycleManagedComponents.add(internetUpdater);
    }

    @Override
    protected void onStart() throws Exception {
        this.lifeCycleManagedComponents.remove(stateDelegator);
        stateDelegator = this.stateController();
        this.lifeCycleManagedComponents.add(stateDelegator);
        super.onStart();
        Thread.sleep(1000);
        writeToSource(q -> q.offer(StateCommand.HEART_BEAT.getCommand()));
    }


    public void writeToProducer(String userAction) {
        log.trace(userAction);
        stateDelegator.writeToSource(q -> q.offer(userAction));
    }


    private void writeToSource(Consumer<BlockingQueue<String>> userAction) {
        stateDelegator.writeToSource(userAction);
    }


    private StateDelegator stateController() {
        Setting s = persistenceService.getSetting();

        return StateDelegator.builder()
                .state(s.getRows(), s.getColumns(), s.getInputCount(), s.getOutsideCount())
                .inputSource(s.getInputSource())
                .outputSource(s.getOutputSource())
                .rensponseHandler(responseHandler)
                .build();
    }

}
