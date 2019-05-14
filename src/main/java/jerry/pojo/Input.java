package jerry.pojo;

import jerry.persist.IIdProvider;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Input implements IIdProvider,Comparable<Input> {
    private Integer id;
    private InputType type;
    private String name;
    private String sourceId;
    private int order = 0;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


    public static Input withDefaults() {
        Input input = new Input();
        input.id = null;
        input.type = InputType.MANUAL;
        input.name = "new input";
        return input;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private int state = 0;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }



    public InputType getType() {
        return type;
    }

    public void setType(InputType type) {
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }



    @Override
    public String toString() {
        return "InputEntity{" +
                "id="+ id+
                "type=" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


    @Override
    public int compareTo(Input o) {
     return Integer.compare(this.order,o.order);
    }
}
