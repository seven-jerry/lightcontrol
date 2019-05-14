package jerry.interaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public abstract class AbstractInteractionManager {


    @Autowired
    EventHandler eventHandler;

    protected volatile boolean started;

    protected volatile boolean auto_start = true;

    protected List<ILIfeCycleExposable> lifeCycleManagedComponents = new ArrayList<>();


    public AbstractInteractionManager(){
        System.out.println("abstract");
    }

    @PostConstruct
    public void init() {
        this.initLifeCycle();
    }

    protected abstract void initLifeCycle();

    public void writeToProducer(String argumentAsString) {
    }


    @Scheduled(initialDelay = 10_000, fixedDelay = 10_000)
    public void tryAutoStart() {

        if (!auto_start) {
            log.warn("not trying to auto start");
            return;
        }
        log.trace("trying...");
        String started = "";
        try {
            started = this.start();
            onTryAutoStart();

        } catch (Exception e) {
            log.error(e.getMessage());
            started = "";
        }

    }

    protected abstract void onTryAutoStart();

    public synchronized String start() throws Exception {
        if (started) return "already started";
        started = true;
        auto_start = true;
        try {
            onStart();
        } catch (Exception e) {
            this.stop();
            log.error("could not start. reason: "+e.getMessage());
            eventHandler.pushMessage("could not start. reason: "+e.getMessage());
            throw e;
        }
        return "started";
    }

    public synchronized boolean hasStarted() {
        return started;
    }


    protected void onStart() throws Exception{
        this.lifeCycleManagedComponents.forEach(ILIfeCycleExposable::startLifecycle);
    }

    public synchronized void stop() {
        this.lifeCycleManagedComponents.forEach(ILIfeCycleExposable::stopLifeCycle);
        this.auto_start = false;
        started = false;
    }


}
