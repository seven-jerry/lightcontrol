package jerry.pojo;

import jerry.interaction.StateArray;
import jerry.interaction.StateCommand;
import jerry.persist.IIdProvider;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.function.BiConsumer;

@XmlRootElement
public class StateCommandOverwrite implements IIdProvider, Comparable<StateCommandOverwrite> {

    private Integer id;

    public StateCommand getCommand() {
        return command;
    }

    public String getOverwrite() {
        return overwrite;
    }

    public void setCommand(StateCommand command) {
        this.command = command;
    }

    StateCommand command;
    @XmlTransient
    private BiConsumer<StateCommand, StateArray> consumer;

    String overwrite = "";

    public void doOverwrite() {
        if (command == null)return;
        this.command.setConsumer(buildConsumer());
    }

    public void setOverwrite(String overwrite) {
        this.overwrite = overwrite;
    }

    private BiConsumer<StateCommand, StateArray> buildConsumer() {
        return (c, s) -> s.updateOutputState(overwrite);
    }

    @Override
    public int compareTo(@NotNull StateCommandOverwrite o) {
        return Integer.compare(id, o.getId());
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
}
