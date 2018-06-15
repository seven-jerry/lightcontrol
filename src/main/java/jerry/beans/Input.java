package jerry.beans;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@XmlRootElement
public class Input {
    private String id;

    private InputType type;
    private String settingId;
    private String name;
    private Integer inputId;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private int state = 0;


    public Integer getInputId() {
        return inputId;
    }

    public void setInputId(Integer inputId) {
        this.inputId = inputId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public InputType getType() {
        return type;
    }

    public void setType(InputType type) {
        this.type = type;
    }

    public String getSettingId() {
        return settingId;
    }

    public void setSettingId(String settingId) {
        this.settingId = settingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    @Override
    public String toString() {
        return "InputEntity{" +
                "id="+ id+
                "type=" + type + '\'' +
                ", settingId='" + settingId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
