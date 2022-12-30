package jerry.pojo;

import jerry.persist.IIdProvider;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Command implements IIdProvider ,Comparable<Command>{
    private Integer id;
    private String payload;
    private String command;
    private String label;
    private Integer order = 0;

    public static Command withDefaults() {
        Command c = new Command();
        c.id = 0;
        c.command = "";
        c.label = "new command";
        c.payload = "";
        return c;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }


    @Override
    public String toString() {
        return "Command{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", command='" + command + '\'' +
                '}';
    }


    @Override
    public int compareTo(Command o) {
        return Integer.compare(this.order,o.order);
    }

    public String getPayload() {
        if(payload == null){
            return "";
        }
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
