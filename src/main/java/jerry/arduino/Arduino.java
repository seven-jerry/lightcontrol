package jerry.arduino;

import com.fazecast.jSerialComm.*;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;

@XmlRootElement
public class Arduino implements ISerialSource {

    int baundRate;
    String port;
    SerialPort comPort;

    Arduino() {
    }

    Arduino(String portDescription, String baud_rate) {
        this(portDescription, Integer.valueOf(baud_rate));
    }

    Arduino(String portDescription, int baudRate) {
        this.port = portDescription;
        this.baundRate = baudRate;
    }

    public void startLifecycle() {


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
        if (isOpen) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        if (!comPort.isOpen()) {
            throw new IllegalStateException("the comprt was not able to be opened");
        }
    }

    public InputStream getInputStream() {
        return Objects.requireNonNull(this.comPort.getInputStream(), "Arduino : was not able to get inputstream");
    }

    public void stopLifeCycle() {
        this.comPort.closePort();
        this.comPort = null;
    }

    public boolean hasStarted() {
        return this.comPort.isOpen();

    }

    public void write(String s) {
        //writes the entire string at once.
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        OutputStream stream = Objects.requireNonNull(this.comPort.getOutputStream(), "Arduino : was not able to get outputstream");

        PrintWriter p = new PrintWriter(stream);
        p.print(s);
        p.flush();

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
    public int hashCode() {
        return 7 * baundRate + 11 * port.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != this.getClass()) return false;
        Arduino a = (Arduino) o;
        return a.port.equals(this.port) && a.baundRate == this.baundRate;
    }
}