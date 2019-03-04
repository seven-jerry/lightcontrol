package jerry.arduino;

import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Component
public class StateNotifier extends Thread implements IReadUpdateable, ILIfeCycleExposable {
    private BlockingQueue<IConsumer> queueConsumer = new ArrayBlockingQueue<>(10);
    private AtomicReference<String> lastRead = new AtomicReference<>("");
    private AtomicInteger lastUpdated = new AtomicInteger(0);

    private boolean started;

    public void addConsumer(IConsumer consumer) {
        for (IConsumer c : queueConsumer) {
            if (c.equals(consumer)) return;
        }
        this.queueConsumer.add(consumer);
        writeToConsumer(consumer, this.lastRead.get());
    }

    public BlockingQueue<StateArray> readQueue = new ArrayBlockingQueue<>(10);
    public Consumer<String> onWrite;


    @Override
    public void start() {
        super.start();
        started = true;
    }

    @Override
    public void run() {
        try {

            while (true) {
                StateArray array = null;
                while ((array = readQueue.poll()) == null) {
                    Thread.sleep(100);

                }

                lastRead.set(array.toString());
                for (IConsumer consumer : queueConsumer) {
                    writeToConsumer(consumer, array);
                }
            }
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    public void removeConsumer(IConsumer removeConsomer) {
        for (IConsumer consumer : queueConsumer) {
            if (consumer.equals(removeConsomer)) {
                queueConsumer.remove(consumer);
            }
        }
    }
    public Integer lastUpdated(){
        return lastUpdated.get();
    }

    public Integer produceOnce(String payload) {
        this.onWrite.accept(payload);
        return lastUpdated.incrementAndGet();
    }

    @Override
    public void startLifecycle() throws RuntimeException {
        if (!started) {
            this.start();
        }
    }

    @Override
    public void stopLifeCycle() {
        this.interrupt();
    }

    private void writeToConsumer(IConsumer consumer, StateArray message) {
        writeToConsumer(consumer, message.toString());
    }

    private void writeToConsumer(IConsumer consumer, String message) {
        try {
            consumer.write(message);
        } catch (Exception e) {
            System.out.println("exception : " + e);
            consumer.handleError(e);
            queueConsumer.remove(consumer);
        }
    }

    public String getLastState() {
        return this.lastRead.get();
    }


    @Override
    public void handleMessage(StateArray message) {
        readQueue.offer(message);
    }

    @Override
    public void handleError(String message) {
        for (IConsumer consumer : queueConsumer) {
            writeToConsumer(consumer, "Error " + message);
        }
    }
}
