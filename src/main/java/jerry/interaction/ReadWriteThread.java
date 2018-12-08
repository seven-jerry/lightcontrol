package jerry.interaction;


import jerry.arduino.ILIfeCycleExposable;
import jerry.arduino.IReadStateUpdateable;
import jerry.arduino.IReadUpdateable;
import jerry.arduino.ISerialSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class ReadWriteThread extends Thread implements ILIfeCycleExposable {
    private ISerialSource inputSource;
    private IReadStateUpdateable updatable;

    private ISerialSource outputSource;

    private static Map<ISerialSource, ReadWriteThread> readers = new HashMap<>();
    public BlockingQueue<String> inputQueue = new ArrayBlockingQueue<>(10);
    public BlockingQueue<String> ouputQueue = new ArrayBlockingQueue<>(10);


    public ReadWriteThread(ISerialSource inputSource,
                           ISerialSource outputSource, IReadStateUpdateable updatable) {
        this.inputSource = inputSource;
        this.updatable = updatable;
        this.outputSource = outputSource;
    }


    public void run() {
        while (!this.isInterrupted()) {
            try {
                int maxTries = 10;
                int counter = 0;
                boolean hasWritten = this.writeToSource(inputQueue, inputSource);
                while (hasWritten) {
                    Thread.sleep(500);
                    hasWritten = readFromSource(inputSource, updatable::updateInputWithString);
                    counter++;
                    if(counter == maxTries){
                        throw new IllegalStateException("Arduino : tried hard, but had no luck ");
                    }
                }
                counter = 0;
                hasWritten = this.writeToSource(ouputQueue, outputSource);
                while (hasWritten) {
                    Thread.sleep(500);
                    hasWritten = readFromSource(outputSource, updatable::updateOutputWithString);
                    counter++;
                    if(counter == maxTries) {
                        throw new IllegalStateException("Arduino : tried hard, but had no luck ");
                    }
                }
            } catch (RuntimeException e) {
                System.out.println("ReadWriteThread " + e);
                updatable.handleError(e.getMessage());

            } catch (InterruptedException | IOException e) {
                System.out.println("ReadWriteThread " + e);
                this.inputSource.stopLifeCycle();
                this.outputSource.stopLifeCycle();
                break;
            }
        }
    }

    private boolean readFromSource(ISerialSource source, Consumer<String> callback) throws IOException {
        StringBuilder builder = new StringBuilder();
        System.out.println("avail : " + source.getInputStream().available());
        while (source.getInputStream().available() > 0) {
            char readChar = (char)source.getInputStream().read();
            if (startOfMessage(readChar)) {
                builder = new StringBuilder();
                continue;
            }
            if (endOfMessage(readChar)) {
                try {
                    if (!builder.toString().equals(ISerialSource.WRITE_SUCCEEDED)) {
                        callback.accept(builder.toString());
                        System.out.println("read : "+builder.toString());
                    }
                    return false;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
           // System.out.print(readChar);
            builder.append(Character.valueOf(readChar));
        }
        return true;
    }

    private boolean writeToSource(BlockingQueue<String> queue, final ISerialSource serialSource) throws InterruptedException {
        String value = queue.poll(300, TimeUnit.MILLISECONDS);
        Optional.ofNullable(value).ifPresent((e) -> {
                    serialSource.write("{" + e + "}");
                }

        );
        if(value != null) {
            System.out.println("write : " + value);
        }
        return value != null;
    }

    private boolean startOfMessage(char readChar) {
        return readChar == '{';

    }

    private boolean endOfMessage(char readChar) {
        return readChar == '}';

    }

    @Override
    public void startLifecycle() throws RuntimeException {
        this.inputSource.startLifecycle();
        this.outputSource.startLifecycle();
        this.start();
    }

    @Override
    public void stopLifeCycle() {
        this.interrupt();
    }
}
