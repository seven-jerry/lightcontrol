package jerry.device;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import jerry.interaction.IResponseHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Objects;

import static com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_RECEIVED;
@Slf4j
public class MockSerialDevice extends SerialDevice {

    int baundRate;
    boolean started;

    String port;
    SerialPort comPort;
    private String label = "";
    private boolean registerevents;

    private IResponseHandler delegate;

    MockSerialDevice() {
    }

    MockSerialDevice(String portDescription, String baud_rate, boolean registerEvents) {
        this(portDescription, Integer.valueOf(baud_rate), registerEvents);
    }

    public MockSerialDevice(String portDescription, int baudRate, boolean registerEvents) {
        this.port = portDescription;
        this.registerevents = registerEvents;
        this.baundRate = baudRate;
    }

    public synchronized void startLifecycle() {
      started = true;
    }

    public synchronized void stopLifeCycle() {
       started = false;
    }

    public synchronized boolean hasStarted() {
        return started;

    }

    @Override
    public void write(String s) {
        log.debug("write : "+s);

    }

    @Override
    public void setMessageDelegate(IResponseHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setRegisterEvents(boolean registerEvents) {
        this.registerevents = registerEvents;
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
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public int hashCode() {
        return 7 * baundRate + 11 * port.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != this.getClass()) return false;
        SerialDevice a = (SerialDevice) o;
        return a.port.equals(this.port) && a.baundRate == this.baundRate;
    }


    @Override
    public String toString() {
        return "SerialDevice{" +
                "label='" + label + '\'' +
                '}';
    }

    @Override
    public int getListeningEvents() {
        return LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        try {
            String data = new String(event.getReceivedData());
            this.delegate.handleMessage(data);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }


}
