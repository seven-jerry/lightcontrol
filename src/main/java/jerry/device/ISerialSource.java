package jerry.device;

import jerry.interaction.ILIfeCycleExposable;
import jerry.interaction.IResponseHandler;

/**
 * a serial interface is attached providing pojo to the system
 */

public interface ISerialSource extends ILIfeCycleExposable {
    String WRITE_SUCCEEDED = "1";
    boolean hasStarted();
    void setBaundRate(int baundRate);
    void setPort(String port);
    void setLabel(String label);
    String getLabel();
    void write(String mesage);
    void setMessageDelegate(IResponseHandler delegate);
    void setRegisterEvents(boolean registerEvents);
}
