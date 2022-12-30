package jerry.interaction;

import jerry.service.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public enum StateCommand {



    ALL_HIGH("sah", StateCommand::turnAllOn),
    HALF_HIGH("shh", StateCommand::turnHalfOn),
    ALL_LOW("sal", StateCommand::turnAllOff),
    MOTION_HIGH("smh", StateCommand::turnMotionOn),
    HEART_BEAT("shb", StateCommand::voidConsumer),
    READ_INPUTS("sin", StateCommand::voidConsumer),
    OUTSIDE_LOW("sol",StateCommand::outsideLow),
    OUTSIDE_HIGH("soh",StateCommand::outsideHigh);


    public static StateCommand fromCommand(String command) {
        Objects.requireNonNull(command);
        for (StateCommand stateCommand : StateCommand.values()) {
            if (stateCommand.getCommand().equals(command)) {
                return stateCommand;
            }
        }
        throw new IllegalArgumentException("the key <"+command+"> was not found");
    }

    private String command;
    private BiConsumer<StateCommand, StateArray> consumer;
    private final BiConsumer<StateCommand,StateArray> initialConsumer;

    StateCommand(String command, BiConsumer<StateCommand, StateArray> consumer) {
        this.command = command;
        this.consumer = consumer;
        this.initialConsumer = consumer;
    }

    public String getCommand() {
        return command;
    }

    public void callConsumer(StateArray array) {
        BiConsumer<StateCommand,StateArray> allLow = StateCommand::turnAllOff;
        allLow.accept(this,array);
        this.consumer.accept(this, array);
    }

    public void setConsumer(BiConsumer<StateCommand, StateArray> consumer) {
        this.consumer = consumer;
    }

    public void resetInitialConsumer(){
        consumer = initialConsumer;
    }


    private void turnAllOn(StateArray array) {
        array.changeOutputState((x, y, s) -> 1);
    }

    private void turnMotionOn(StateArray array) {

    }


    private void turnAllOff(StateArray array) {
        array.changeOutputState((x, y, s) -> 0);

    }


    private void turnHalfOn(StateArray array) {
        int[][] state = array.getOutputStateArray();
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

    private void outsideLow(StateArray stateArray){
        stateArray.changeOutside((x,s)->0);
    }
    private void outsideHigh(StateArray stateArray){
        stateArray.changeOutside((x,s)->1);
    }

    private void voidConsumer(StateArray array) {

    }
}
