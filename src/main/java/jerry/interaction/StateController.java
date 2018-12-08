package jerry.interaction;

import jerry.arduino.*;
import jerry.viewmodel.pojo.SerialSource;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class StateController implements ILIfeCycleExposable, IReadStateUpdateable {

    private StateArray inputState;
    private StateArray outputState;

    private IReadUpdateable inputUpdatable;
    private IReadUpdateable outputUpdatable;

    private ISerialSource inputSource;
    private ISerialSource outputSource;

    private ReadWriteThread readWriteThread;


    private StateController() {
    }

    public static Builder builder() {
        return new StateController().new Builder();
    }

    public void writeToInputSource(Consumer<BlockingQueue<String>> userAction) {
        userAction.accept(readWriteThread.inputQueue);
    }
    public void writeToOutputSource(Consumer<BlockingQueue<String>> userAction) {
        userAction.accept(readWriteThread.ouputQueue);
    }


    class Builder {


        Builder() {
        }

        Builder inputState(int x, int y) {
            inputState = new StateArray(x, y, 0);
            return this;
        }

        Builder outputState(int x, int y, int o) {
            outputState = new StateArray(x, y, o);
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

        Builder inputUpdateable(IReadUpdateable updateable) {
            inputUpdatable = updateable;
            return this;
        }

        Builder outputUpdateable(IReadUpdateable updateable) {
            outputUpdatable = updateable;
            return this;
        }


        StateController build() {
            if(outputSource.equals(inputSource)){
                inputSource = outputSource;
            }
            readWriteThread = new ReadWriteThread(inputSource, outputSource, StateController.this);
            return StateController.this;
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
        if (!readWriteThread.isAlive()) {
            readWriteThread.startLifecycle();
        }
    }

    @Override
    public void stopLifeCycle() throws RuntimeException {
        readWriteThread.stopLifeCycle();
    }

    @Override
    public  synchronized void updateInputWithString(String state) {

        this.inputState.setState(state);

        this.inputUpdatable.handleMessage(new StateArray(this.inputState));
    }

    @Override
    public synchronized void updateOutputWithString(String state) {
          this.outputState.setState(state);
          this.outputUpdatable.handleMessage(new StateArray(this.outputState));
    }

    @Override
    public void handleError(String message) {
        this.outputUpdatable.handleError(message);
    }

    public synchronized StateArray getInputState() {
        return new StateArray(this.inputState);
    }

    public synchronized StateArray getOutputState() {
        return new StateArray(this.outputState);
    }


}
