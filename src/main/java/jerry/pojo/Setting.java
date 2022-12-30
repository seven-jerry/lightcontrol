package jerry.pojo;

import jerry.interaction.InputCommand;
import jerry.interaction.StateArray;
import jerry.interaction.InputControl;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
import java.util.stream.Collectors;

@XmlRootElement
public class Setting {
    private Integer rows;
    private Integer columns;
    private Integer outside;
    private InputCommand inputCommand;
    private String masterUrl;
    private String masterInternetUrl;
    private InputControl control;
    private String name;

    private String localNodeRedWebsocket;
    private String homeAssistHttpEndpoint;


    private List<Input> inputs = new ArrayList<>();

    private transient List<Command> commands = new ArrayList<>();

    private SerialSource outputSource;

    private SerialSource inputSource;


    public Setting() {
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

    public void setCommands(Command[] commands) {
        this.commands = Arrays.asList(commands);
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

        Map<Integer, String> nameMap = new HashMap<>();
        for (Input input : this.inputs) {
            nameMap.put(Integer.parseInt(input.getSourceId()), input.getName());
        }
        HashMap<String, Integer> buildMap = new HashMap<>();
        if (lastState == null) return buildMap;

        lastState.walkInputState((x, s) -> {
            String name = String.valueOf(nameMap.get(x));
            buildMap.put(name, s);
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Setting setting = (Setting) o;
        return Objects.equals(rows, setting.rows) &&
                Objects.equals(columns, setting.columns) &&
                Objects.equals(outside, setting.outside) &&
                inputCommand == setting.inputCommand &&
                Objects.equals(masterUrl, setting.masterUrl) &&
                control == setting.control &&
                Objects.equals(outputSource, setting.outputSource) &&
                Objects.equals(inputSource, setting.inputSource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rows, columns, outside, inputCommand, masterUrl, control, inputs, commands, outputSource, inputSource);
    }

    public int getOutsideCount() {
        return this.getOutside();
    }

    public int getOutputCount() {
        return this.columns * this.rows;
    }

    public int getInputCount() {
        return this.inputs.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getMasterInternetUrl() {
        return masterInternetUrl;
    }

    public void setMasterInternetUrl(String masterInternetUrl) {
        this.masterInternetUrl = masterInternetUrl;
    }

    public String getLocalNodeRedWebsocket() {
        return localNodeRedWebsocket;
    }

    public void setLocalNodeRedWebsocket(String localNodeRedWebsocket) {
        this.localNodeRedWebsocket = localNodeRedWebsocket;
    }

    public String getHomeAssistHttpEndpoint() {
        return homeAssistHttpEndpoint;
    }

    public void setHomeAssistHttpEndpoint(String homeAssistHttpEndpoint) {
        this.homeAssistHttpEndpoint = homeAssistHttpEndpoint;
    }

}
