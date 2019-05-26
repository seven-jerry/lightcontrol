package jerry.interaction;


import jerry.master.ClientStateUpdater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;


@Slf4j
public class MasterInteractionManager extends AbstractInteractionManager {

    @Autowired
    @Qualifier("contextAwareClientStateNotifier")
    AbstractStateNotifier notifier;

    @Autowired
    ClientStateUpdater clientStateUpdater;


    public MasterInteractionManager() {
        System.out.println("master");
    }

    @Override
    protected void initLifeCycle() {
        this.lifeCycleManagedComponents.add(notifier);
    }

    public void writeToProducer(Map<String, String> map) {
        map.forEach(clientStateUpdater::writeToHost);
    }


    @Override
    protected void onTryAutoStart() {

    }
}
