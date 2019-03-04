package jerry.viewmodel.pojo;

import jerry.arduino.InputCommand;
import jerry.arduino.StateArray;
import jerry.interaction.InputControl;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.*;

@XmlRootElement
public class Setting {
    private Integer rows;
    private Integer columns;
    private Integer outside;
    private InputCommand inputCommand;
    private String masterUrl;
    private InputControl control;
    private int minBrightness;
    private int maxBrightness;
    private int userBrightness;

    @XmlTransient
    private List<Input> inputs = new ArrayList<>();
    @XmlTransient
    private List<Command> commands = new ArrayList<>();

    private SerialSource outputSource;

    private SerialSource inputSource;


    Setting() {
        this.outputSource = new SerialSource();
        this.inputSource = new SerialSource();
    }



    public void setInputCommand(InputCommand inputCommand) {
        this.inputCommand = inputCommand;
    }

    public InputCommand getInputCommand() {
        return inputCommand;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getColumns() {
        return columns;
    }

    public boolean isEmpty() {
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

    public void addInput(Input in) {
        this.inputs.add(in);
    }


    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
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
        s.minBrightness = 0;
        s.maxBrightness = 0;
        s.masterUrl = "http://localhost";
        return s;
    }

    public Integer getOutside() {
        return outside;
    }

    public void setOutside(Integer outside) {
        this.outside = outside;
    }

    public Map<String, Integer> labeledInput(StateArray lastState) {
        HashMap<String, Integer> buildMap = new HashMap<>();
        if (lastState == null) return buildMap;

        lastState.walk((x, y, s) -> {
            Optional.ofNullable(this.inputs.get(y))
                    .ifPresent(e -> buildMap.put(e.getName(), s));

        });
        return buildMap;
    }

    public String getMasterUrl() {
        return masterUrl;
    }

    public void setMasterUrl(String masterUrl) {
        this.masterUrl = masterUrl;
    }

    public InputControl getControl() {
        return control;
    }

    public void setControl(InputControl control) {
        this.control = control;
    }

    public int getMinBrightness() {
        return minBrightness;
    }

    public void setMinBrightness(int minBrightness) {
        this.minBrightness = minBrightness;
    }

    public int getMaxBrightness() {
        return maxBrightness;
    }

    public void setMaxBrightness(int maxBrightness) {
        this.maxBrightness = maxBrightness;
    }
}
