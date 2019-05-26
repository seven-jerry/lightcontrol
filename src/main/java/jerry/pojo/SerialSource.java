package jerry.pojo;

import jerry.device.SerialSources;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SerialSource{
    private String port;
    private int baundRate;
    private SerialSources serialSources;

    SerialSource(){}
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getBaundRate() {
        return baundRate;
    }

    public void setBaundRate(int baundRate) {
        this.baundRate = baundRate;
    }

    public SerialSources getSerialSources() {
        return serialSources;
    }

    public void setSerialSources(SerialSources serialSources) {
        this.serialSources = serialSources;
    }

    @Override
    public boolean equals(Object o){
        if(o == null) return false;
        if(!(o instanceof SerialSource))return  false;
        SerialSource a = (SerialSource) o;
        return this.getPort().equals(a.getPort());
    }

}
