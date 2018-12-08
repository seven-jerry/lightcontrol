package jerry.arduino;

import java.io.InputStream;

/**
 * a serial interface is attached providing data to the system
 */

public interface ISerialSource extends ILIfeCycleExposable{
    String WRITE_SUCCEEDED = "1";
    boolean hasStarted();
    InputStream getInputStream();
    void write(String s);

    void setBaundRate(int baundRate);
    void setPort(String port);

}
