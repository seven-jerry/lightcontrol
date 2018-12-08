package jerry.viewmodel.pojo;

import jerry.persist.IIdProvider;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Client implements IIdProvider,Comparable<Client>{
    private String ipAddress;
    private int fetchCycle;
    private Integer id;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getFetchCycle() {
        return fetchCycle;
    }

    public void setFetchCycle(int fetchCycle) {
        this.fetchCycle = fetchCycle;
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
