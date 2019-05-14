package jerry.interaction;


import jerry.device.ISerialSource;
import lombok.extern.slf4j.Slf4j;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class WriteThread extends Thread implements ILIfeCycleExposable {


    private ISerialSource source;
    public BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
    private volatile boolean running;


    public WriteThread(ISerialSource source) {
        this.source = source;
    }


    public void run() {
        while (!this.isInterrupted() && running) {
            try {
                String value = queue.take();
                log.trace(value);
                source.write("{" + value + "}");
            } catch (RuntimeException | InterruptedException e) {
                log.error(e.getMessage());
                break;
            }
        }
    }


    @Override
    public void startLifecycle() throws RuntimeException {
        this.source.startLifecycle();
        this.running = true;
        this.start();

    }

    @Override
    public void stopLifeCycle() {
        this.interrupt();
        this.running = false;
        this.source.stopLifeCycle();
    }
}
