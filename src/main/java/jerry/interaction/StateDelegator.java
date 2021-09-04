package jerry.interaction;

import jerry.device.*;
import jerry.pojo.SerialSource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

@Slf4j
public class StateDelegator implements ILIfeCycleExposable {

    private StateArray state;

    private IReadUpdateable sourceMessageReciever;

    private ISerialSource inputSource;
    private ISerialSource outputSource;

    private WriteThread writeThread;

    private IResponseHandler responseHandler;

    private static final Logger logger = LoggerFactory.getLogger(StateDelegator.class);


    private StateDelegator() {
    }

    public static Builder builder() {
        return new StateDelegator().new Builder();
    }

    public void writeToSource(Consumer<BlockingQueue<String>> userAction) {
        userAction.accept(writeThread.queue);
    }


    class Builder {

        Builder() {
        }

        Builder state(int output_rows, int output_columns, int input, int outside) {
            state = StateArray.empty(output_rows, output_columns, input, outside);
            return this;
        }

        Builder inputSource(SerialSource serialSources) {
            inputSource = toSource(serialSources);
            return this;
        }

        Builder outputSource(SerialSource serialSources) {
            outputSource = toSource(serialSources);
            return this;
        }

        Builder sourceMessageReciever(IReadUpdateable updateable) {
            sourceMessageReciever = updateable;
            return this;
        }

        Builder rensponseHandler(IResponseHandler handler) {
            responseHandler = handler;
            return this;
        }


        StateDelegator build() {
            writeThread = new WriteThread(inputSource);
            outputSource.setMessageDelegate(responseHandler);
            return StateDelegator.this;
        }


        private ISerialSource toSource(SerialSource serialSources) {
            SerialSources sources = Objects.requireNonNull(serialSources.getSerialSources(), "serial source was null");
            ISerialSource serialSource = sources.getSource();
            serialSource.setBaundRate(serialSources.getBaundRate());
            serialSource.setPort(serialSources.getPort());
            return serialSource;

        }

    }


    @Override
    public void startLifecycle() throws RuntimeException {
        if (!writeThread.isAlive()) {
            writeThread.startLifecycle();
        }
        outputSource.setRegisterEvents(true);
        outputSource.startLifecycle();
    }

    @Override
    public void stopLifeCycle() throws RuntimeException {
        writeThread.stopLifeCycle();
        outputSource.stopLifeCycle();

    }


}
