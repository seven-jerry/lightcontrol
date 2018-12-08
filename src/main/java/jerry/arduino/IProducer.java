package jerry.arduino;

import java.util.concurrent.BlockingQueue;

public interface IProducer {
    void produceToQueue(BlockingQueue<String> writeQueue);
}
