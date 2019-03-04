package jerry.interaction;


import jerry.arduino.ILIfeCycleExposable;
import jerry.arduino.StateArray;
import jerry.arduino.StateCommand;
import jerry.arduino.StateNotifier;
import jerry.service.PersistenceService;
import jerry.viewmodel.pojo.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class Controller {

    @Autowired
    StateNotifier notifier;
    @Autowired
    ReadManager readManager;

    @Value("${heartbeat.rate}")
    int heartbeatTime;

    @Value("${input.rate}")
    int inputRate;

    @Autowired
    PersistenceService persistenceService;

    private StateController controller;

    private List<ILIfeCycleExposable> lifeCycleManagedComponents = new ArrayList<>();
    private volatile boolean started;

    Controller() {
    }

    @PostConstruct
    public void init() {
        this.lifeCycleManagedComponents = new ArrayList<>();
        this.lifeCycleManagedComponents.add(notifier);
        this.lifeCycleManagedComponents.add(readManager);
        this.lifeCycleManagedComponents.add(new InputTimer());
        this.lifeCycleManagedComponents.add(new HeartBeatTimer());
    }


    public synchronized String start() throws Exception {
        if (started) return "already stared";
        try {
            controller = this.stateController();
            notifier.onWrite = s -> controller.writeToOutputSource(q -> q.offer(s));
            this.lifeCycleManagedComponents.add(controller);
            this.lifeCycleManagedComponents.forEach(ILIfeCycleExposable::startLifecycle);
            started = true;
            controller.writeToOutputSource(q -> q.offer(StateCommand.HEART_BEAT.getCommand()));
        } catch (Exception e) {
            this.stop();
            throw e;
        }
        return "started";
    }

    public synchronized void stop() {
        this.lifeCycleManagedComponents.forEach(ILIfeCycleExposable::stopLifeCycle);
        this.init();
        started = false;
    }

    public synchronized boolean hasStarted() {
        return started;
    }


    public void readInputs() {
        controller.writeToInputSource(
                q -> {
                    if(q.remainingCapacity() > 8) {
                        q.offer(StateCommand.READ_INPUTS.getCommand());
                    }
                });
    }

    public void heartBeat() {
        controller.writeToOutputSource(q -> {
            if(q.remainingCapacity() > 8) {
                q.offer(StateCommand.HEART_BEAT.getCommand());
            }
        });
    }


    public StateArray getWriterState() {
        return controller.getOutputState();
    }

    public StateArray getReaderState() {
        return this.controller.getInputState();
    }


    private StateController stateController() {
        Setting s = persistenceService.getSetting();

        return StateController.builder()
                .inputState(1, s.getInputs().size())
                .outputState(s.getRows(), s.getColumns(), s.getOutside())
                .inputSource(s.getInputSource())
                .outputSource(s.getOutputSource())
                .inputUpdateable(readManager)
                .outputUpdateable(notifier)
                .build();
    }

    private class InputTimer extends Timer implements ILIfeCycleExposable {

        @Override
        public void startLifecycle() throws RuntimeException {
            this.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Controller.this.readInputs();
                }
            }, 500, Controller.this.inputRate);
        }

        @Override
        public void stopLifeCycle() throws RuntimeException {
            this.cancel();
        }
    }

    private class HeartBeatTimer extends Timer implements ILIfeCycleExposable {
        @Override
        public void startLifecycle() throws RuntimeException {
            this.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Controller.this.heartBeat();
                }
            }, 500, Controller.this.heartbeatTime);
        }

        @Override
        public void stopLifeCycle() throws RuntimeException {
            this.cancel();
        }
    }
}
