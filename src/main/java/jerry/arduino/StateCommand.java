package jerry.arduino;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public enum StateCommand {
    ALL_HIGH("sah",StateCommand::turnAllOn),
    HALF_HIGH("shh",StateCommand::turnHalfOn),
    ALL_LOW("sal",StateCommand::turnAllOff),
    MOTION_HIGH("smh",StateCommand::turnMotionOn),
    HEART_BEAT("shb",StateCommand::voidConsumer),
    READ_INPUTS("sin",StateCommand::voidConsumer);

    public static StateCommand fromCommand(String command){
        Objects.requireNonNull(command);
        for(StateCommand stateCommand : StateCommand.values()){
            if(stateCommand.getCommand().equals(command)){
                return stateCommand;
            }
        }
        return null;
        //throw new IllegalArgumentException("the key <"+command+"> was not found");
    }

    private String command;
    private BiConsumer<StateCommand,StateArray> consumer;

    StateCommand(String command, BiConsumer<StateCommand,StateArray> consumer){
        this.command = command;
        this.consumer = consumer;
    }

    public String getCommand() {
        return command;
    }

    public void callConsumer(StateArray array){
        this.consumer.accept(this,array);
    }

    public void setConsumer(BiConsumer<StateCommand,StateArray> consumer){
        this.consumer = consumer;
    }


    private void turnAllOn(StateArray array){
        int[][] state = array.getState();
        for (int i = 0; i < state.length; i = i + 1) {
            for (int j = 0; j < state[i].length; j++) {
                state[i][j] = 1;
            }
        }
    }

    private void turnMotionOn(StateArray array){
        array.getState()[4][3] = 1;

    }


    private void turnAllOff(StateArray array){
        int[][] state = array.getState();
        for (int i = 0; i < state.length; i = i + 1) {
            for (int j = 0; j < state[i].length; j++) {
                state[i][j] = 0;
            }
        }
    }


    private void turnHalfOn(StateArray array){
        int[][] state = array.getState();
        int  on = 1;
        for (int i = 0; i < state.length; i = i + 1) {
            for (int j = 0; j < state[i].length; j++) {
                state[i][j] = on;
                if (on == 1) {
                    on = 0;
                } else{
                    on = 1;
                }
            }
        }
    }

    private void voidConsumer(StateArray array){

    }
}
