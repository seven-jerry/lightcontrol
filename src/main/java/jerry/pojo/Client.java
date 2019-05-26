package jerry.pojo;

import jerry.persist.IIdProvider;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Client implements IIdProvider,Comparable<Client>{
    private String ipAddress;
    private Integer id;
    private String label = "label";

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public int compareTo(Client o) {
        return 0;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
}
