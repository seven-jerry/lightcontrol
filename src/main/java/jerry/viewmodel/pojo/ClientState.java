package jerry.viewmodel.pojo;

import jerry.arduino.StateArray;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Collection;
import java.util.Map;

@XmlRootElement
public class ClientState {
    @XmlElement(name = "commands")
    Collection<Command> commands;
    String state;
    String inputs;
    Map<String, Integer> labeledInputs;
    @XmlTransient
    public String id;

    public String getState() {
        return state;
    }
}
