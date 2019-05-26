package jerry.device;

import com.fazecast.jSerialComm.*;
import jerry.interaction.IResponseHandler;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Objects;

import static com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_RECEIVED;

@XmlRootElement
@Slf4j
public class SerialDevice implements ISerialSource, SerialPortDataListener {

    int baundRate;
    String port;
    SerialPort comPort;
    private String label = "";
    private boolean registerevents;

    private IResponseHandler delegate;

    SerialDevice() {
    }

    SerialDevice(String portDescription, String baud_rate, boolean registerEvents) {
        this(portDescription, Integer.valueOf(baud_rate), registerEvents);
    }

    public SerialDevice(String portDescription, int baudRate, boolean registerEvents) {
        this.port = portDescription;
        this.registerevents = registerEvents;
        this.baundRate = baudRate;
    }

    public synchronized void startLifecycle() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return;
        }

        if (comPort != null && comPort.isOpen()) {
            return;
        }

        comPort = SerialPort.getCommPort(port);
        comPort.setBaudRate(baundRate);
        boolean isOpen = comPort.openPort();

        if (registerevents) {
            comPort.addDataListener(this);
        }
        if (!comPort.isOpen()) {
            throw new IllegalStateException("the comprt was not able to be opened");
        }
    }

    public synchronized void stopLifeCycle() {
        if (this.comPort == null) return;
        this.comPort.closePort();
        this.comPort = null;
    }

    public synchronized boolean hasStarted() {
        return this.comPort.isOpen();

    }

    @Override
    public void write(String s) {
        //writes the entire string at once.
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        OutputStream stream = Objects.requireNonNull(this.comPort.getOutputStream(), "SerialDevice : was not able to get outputstream");

        PrintWriter p = new PrintWriter(stream);
        p.print(s);
        p.flush();

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
            log.error(e.toString());
        }

    }
}