package jerry.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jerry.interaction.StateArray;
import jerry.pojo.Command;
import jerry.pojo.Setting;

import java.time.LocalDateTime;
import java.util.*;


public class ClientState {
    public static final String OUTPUT_STATE = "output_state";
    public static final String OUTSIDE_STATE = "outside_state";
    public static final String COMMANDS = "commands";
    public static final String INPUT_STATE = "input_state";
    public static final String SETTINGS = "setting";
    public static final String MESSAGE_TYPE = "message.type";

    public static final String MESSAGE_TYPE_FULL = "full";
    public static final String MESSAGE_TYPE_PARTIAL = "partial";

    Setting setting;

    StateArray state;


    public static ClientState fromJson(JsonObject jsonObject) {
        ClientState state = new ClientState();
        state.setting = new Gson().fromJson(jsonObject.get(ClientState.SETTINGS),Setting.class);
        state.setting.setCommands(new Gson().fromJson(jsonObject.get(ClientState.COMMANDS),Command[].class));



        jobj.add(ClientState.OUTPUT_STATE, new Gson().toJsonTree(this.state.outputStateString(false)));

        jobj.add(ClientState.OUTSIDE_STATE, new Gson().toJsonTree(this.state.outSideStateString(false)));

        jobj.add(ClientState.INPUT_STATE, new Gson().toJsonTree(this.setting.labeledInput(this.state)));

        jobj.add("time", new Gson().toJsonTree(LocalDateTime.now().toString()));

        jobj.add(MESSAGE_TYPE, new Gson().toJsonTree(keySet.isEmpty() ? MESSAGE_TYPE_FULL : MESSAGE_TYPE_PARTIAL));

    }

    public static ClientState fromClientState(ClientState source) {
        ClientState clientState = new ClientState();
        clientState.setting = source.setting;
        clientState.state = StateArray.parseString(source.state.toString());
        return clientState;
    }

    public static ClientState empty() {
        ClientState state = new ClientState();
        state.setting = new Setting();
        state.state = StateArray.empty(0, 0, 0, 0);
        return state;
    }

    public static ClientState withSize(int output_x, int output_y, int input_count, int outside_count) {
        ClientState state = new ClientState();
        state.setting = new Setting();
        state.state = StateArray.empty(output_x, output_y, input_count, outside_count);
        return state;
    }

    public static ClientState fromSetting(Setting setting) {
        ClientState clientState = new ClientState();
        clientState.setting = setting;
        clientState.state = StateArray.empty(setting.getRows(), setting.getColumns(), setting.getInputCount(), setting.getOutside());
        return clientState;
    }

    private ClientState() {
    }

    public Setting getSetting() {
        return setting;
    }

    public StateArray getState() {
        return state;
    }

    public String getJson(String... keys) {
        JsonObject jobj = new JsonObject();

        Set<String> keySet = new HashSet<String>(Arrays.asList(keys));

        if (keySet.isEmpty() || keySet.contains(ClientState.SETTINGS)) {
            jobj.add(ClientState.SETTINGS, new Gson().toJsonTree(setting));
        }

        if (keySet.isEmpty() || keySet.contains(ClientState.COMMANDS)) {
            jobj.add(ClientState.COMMANDS, new Gson().toJsonTree(setting.getCommands()));
        }

        if (keySet.isEmpty() || keySet.contains(ClientState.OUTPUT_STATE)) {
            jobj.add(ClientState.OUTPUT_STATE, new Gson().toJsonTree(this.state.outputStateString(false)));
        }
        if (keySet.isEmpty() || keySet.contains(ClientState.OUTSIDE_STATE)) {
            jobj.add(ClientState.OUTSIDE_STATE, new Gson().toJsonTree(this.state.outSideStateString(false)));
        }
        if (keySet.isEmpty() || keySet.contains(ClientState.INPUT_STATE)) {
            jobj.add(ClientState.INPUT_STATE, new Gson().toJsonTree(this.setting.labeledInput(this.state)));
        }

        jobj.add("time", new Gson().toJsonTree(LocalDateTime.now().toString()));

        jobj.add(MESSAGE_TYPE, new Gson().toJsonTree(keySet.isEmpty() ? MESSAGE_TYPE_FULL : MESSAGE_TYPE_PARTIAL));

        return jobj.toString();
    }

    public void setCommands(List<Command> commands) {
        this.setting.setCommands(commands);
    }


    public String getInputState() {
        return this.state.inputStateString(false);
    }

    public String getOutputState() {
        return this.state.outputStateString(false);
    }

    public String getOutsideState() {
        return this.state.outSideStateString(false);
    }

    public void setInputState(String state) {
        this.state.updateInputState(state);
    }

    public void setOutputState(String state) {
        this.state.updateOutputState(state);
    }

    public void setOutsideState(String state) {
        this.state.updateOutsideState(state);
    }

    public String toString() {
        return this.getOutputState();
    }
}
