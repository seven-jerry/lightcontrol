package jerry.arduino;

import jerry.service.PersistenceService;
import jerry.viewmodel.pojo.Setting;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class MockArduino implements ISerialSource, ApplicationContextAware {
    private boolean hasStarted;
    private InputStream stream ;
    private String port;
    private int baundRate;

    private StateArray mockState;
    private StateArray inputState;

    private static ApplicationContext context;

    @Autowired
    private PersistenceService service;

    MockArduino() {
    }

    @Override
    public void startLifecycle() {
        if(this.hasStarted) {
            return;
        }
        this.hasStarted = true;
        this.stream = initStream("1");
        this.mockState = new StateArray(service.getSetting().getRows(), service.getSetting().getColumns(),service.getSetting().getOutside());
        this.inputState = new StateArray(1, service.getSetting().getInputs().size(),0);
    }

    @Override
    public void stopLifeCycle() {
        this.hasStarted = false;
    }

    @Override
    public boolean hasStarted() {
        return hasStarted;
    }

    private synchronized InputStream initStream(String s) {
        return new MockStream(s);
    }

    public synchronized InputStream getInputStream() {

        return this.stream;
    }

    @Override
    public synchronized void write(String s) {
        String stateString = s.replace("{", "").replace("}", "");
        if (StateCommand.READ_INPUTS.getCommand().equals(stateString)) {
            this.stream = this.initStream(this.inputState.toString());
        }
        if (StateCommand.HEART_BEAT.getCommand().equals(stateString)) {
            this.stream = this.initStream(this.mockState.toString());
        }
        if(StateCommand.fromCommand(stateString) != null) {
            StateCommand.fromCommand(stateString).callConsumer(this.mockState);
        } else{
            this.mockState.setState(stateString);
        }
    }

    @Override
    public void setBaundRate(int baundRate) {
        this.baundRate = baundRate;
    }

    @Override
    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != this.getClass()) return false;
        MockArduino a = (MockArduino) o;
        return a.port.equals(this.port) && a.baundRate == this.baundRate;
    }

    public static MockArduino getSpringBean() {
        return (MockArduino) context.getBean("mockArduino");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @Override
    public int hashCode() {
        return 7*baundRate + 11 * port.hashCode();
    }


    private class MockStream extends InputStream {
        char[] chars;
        int index = 0;

        MockStream(String state) {
            state = "{" + state + "}";
            chars = state.toCharArray();
        }

        @Override
        public int read() throws IOException {
            int c = chars[index];
            index++;
          //  System.out.println("MockArduino:take" + (char) c);
            return c;
        }

        @Override
        public int available() throws IOException {
            return chars.length - index ;
        }
    }


}
