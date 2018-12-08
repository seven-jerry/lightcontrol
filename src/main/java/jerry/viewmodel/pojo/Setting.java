package jerry.viewmodel.pojo;

import jerry.arduino.InputCommand;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement

public class Setting {
    private Integer rows;
    private Integer columns;
    private Integer outside;
    private InputCommand inputCommand;

    public InputCommand getInputCommand() {
        return inputCommand;
    }

    public void setInputCommand(InputCommand inputCommand) {
        this.inputCommand = inputCommand;
    }

    Setting(){
        this.outputSource = new SerialSource();
        this.inputSource = new SerialSource();
    }

    @XmlTransient
    private List<Input> inputs =  new ArrayList<>();
    @XmlTransient
    private List<Command> commands =  new ArrayList<>();

    private SerialSource outputSource;

    private SerialSource inputSource;

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getColumns() {
        return columns;
    }

    public boolean isEmpty(){
        return rows == null || columns == null;
    }

    public void setColumns(Integer outputColumn) {
        this.columns = outputColumn;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public void setInputs(List<Input> inputs) {
        this.inputs = inputs;
    }
    public void addInput(Input in){
        this.inputs.add(in);
    }


    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }
    public void addIgnore(Command in){
        this.commands.add(in);
    }

    public Map<Integer,Command> getIgnoreArray(){
        Map<Integer,Command> ignoreArray = new HashMap<>();

        for(Command input: commands){
            ignoreArray.put(input.getId(),input);
        }
        return ignoreArray;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "rows=" + rows +
                ", columns=" + columns +
                ", inputs=" + inputs +
                ", commands=" + commands +
                ", outputSource=" + outputSource +
                ", inputSource=" + inputSource +
                '}';
    }

    public SerialSource getOutputSource() {
        return outputSource;
    }

    public void setOutputSource(SerialSource outputSource) {
        this.outputSource = outputSource;
    }
    public SerialSource getInputSource() {
        return inputSource;
    }

    public void setInputSource(SerialSource inputSource) {
        this.inputSource = inputSource;
    }

    public static Setting withDefaults() {
        Setting s = new Setting();
        s.rows = 0;
        s.columns = 0;
        s.outside = 0;
        return s;
    }

    public Integer getOutside() {
        return outside;
    }

    public void setOutside(Integer outside) {
        this.outside = outside;
    }
}
