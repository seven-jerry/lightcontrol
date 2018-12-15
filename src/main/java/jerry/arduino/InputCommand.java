package jerry.arduino;

import jerry.interaction.InputControl;
import jerry.interaction.StateController;

import java.util.Optional;
import java.util.function.Predicate;

public enum InputCommand {
    ONE_HIGH_ALL_LOW_MOTION(InputCommand::oneHigh, StateCommand.MOTION_HIGH, InputCommand::allLow, StateCommand.ALL_LOW),
    IGNORE(InputCommand::oneHigh, StateCommand.HEART_BEAT, InputCommand::allLow, StateCommand.HEART_BEAT);
    Predicate<StateArray> highCondition;
    StateCommand highCommand;

    Predicate<StateArray> lowCondition;
    StateCommand lowCommand;
    private boolean hasTurned;

    private Integer lastUpdated;

    InputCommand(Predicate<StateArray> highCondition, StateCommand highCommand,
                 Predicate<StateArray> lowCondition, StateCommand lowCommand) {
        this.highCommand = highCommand;
        this.highCondition = highCondition;
        this.lowCommand = lowCommand;
        this.lowCondition = lowCondition;
    }

    public Optional<StateCommand> testCondition(StateArray array, Integer lastUpdated, InputControl control) {
        System.out.println("current update : " + this.lastUpdated + " next Updated : " + lastUpdated);
        if (this.highCondition.test(array) && control.shouldTurnHigh()) {
            this.lastUpdated = lastUpdated;
            return Optional.of(this.highCommand);
        }

        if (this.lowCondition.test(array) && control.shouldTUrnLow()) {
            return Optional.of(this.lowCommand);
        }
        return Optional.empty();
    }

    public void resetCondition() {
        hasTurned = false;
    }

    public static boolean oneHigh(StateArray stateArray) {
        return stateArray.hasAny((x, y, s) -> s == 1);
    }

    public static boolean allLow(StateArray stateArray) {
        return !stateArray.hasAny((x, y, s) -> s == 1);
    }

}




