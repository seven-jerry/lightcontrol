package jerry.interaction;

import com.google.gson.Gson;
import jerry.consumer.ClientRequestMessage;
import jerry.consumer.IConsumer;
import jerry.master.MasterUpdater;
import jerry.service.ClientStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
@Slf4j
public abstract class AbstractStateNotifier implements Runnable, ILIfeCycleExposable {
    protected BlockingQueue<IConsumer> consumers = new ArrayBlockingQueue<>(10);
    public BlockingQueue<String> messages = new ArrayBlockingQueue<>(10);
    protected volatile boolean started;
    protected Thread updateThread;

    protected IConsumer masterConsumer;

    @Autowired
    @Qualifier("contextAdjustingInteractionManager")
    AbstractInteractionManager clientInteractionManager;


    public void addConsumer(IConsumer consumer) {
        for (IConsumer c : consumers) {
            if (c.equals(consumer)) return;
        }
        this.consumers.add(consumer);
    }


    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted() && started) {
                String message = messages.take();
                for (IConsumer consumer : consumers) {
                    writeToConsumer(consumer, message);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void writeToConsumer(IConsumer consumer, String message) {
        try {
            log.trace(message);
            consumer.write(message);
        } catch (Exception e) {
            System.out.println("exception : " + e);
            consumer.handleError(e);
            consumers.remove(consumer);
        }
    }

    public void removeConsumer(IConsumer removeConsomer) {
        for (IConsumer consumer : consumers) {
            if (consumer.equals(removeConsomer)) {
                consumer.closeConnection();
                consumers.remove(consumer);
            }
        }
    }


    public void handleConsumerMessage(TextMessage message) {

    }

    @Override
    public void startLifecycle() throws RuntimeException {
        if (started) return;
        started = true;
        this.updateThread = new Thread(this);
        this.updateThread.start();
    }

    @Override
    public void stopLifeCycle() {
        if (!started) return;
        this.started = false;
        this.updateThread.interrupt();
    }


    public void setMasterConsumer(IConsumer masterUpdater) {
        this.masterConsumer = masterUpdater;
    }
}
