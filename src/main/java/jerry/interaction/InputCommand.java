package jerry.interaction;

import jerry.pojo.Input;
import jerry.pojo.InputType;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum InputCommand {
    ONE_HIGH_ALL_LOW_MOTION(InputCommand::oneHigh, StateCommandKeys.MOTION_HIGH, InputCommand::allLow,StateCommandKeys.ALL_LOW),
    IGNORE(InputCommand::oneHigh, StateCommandKeys.HEART_BEAT, InputCommand::allLow, StateCommandKeys.HEART_BEAT),
    MOTION_HIGH_MANUAL_LOW(InputCommand::oneHigh, StateCommandKeys.MOTION_HIGH, InputCommand::switchLow, StateCommandKeys.ALL_LOW);


    BiPredicate<StateArray, List<Input>> highCondition;
    String highCommand;

    BiPredicate<StateArray, List<Input>> lowCondition;
    String lowCommand;
    private boolean hasTurned;


    InputCommand(BiPredicate<StateArray, List<Input>> highCondition, String highCommand,
                 BiPredicate<StateArray, List<Input>> lowCondition, String lowCommand) {
        this.highCommand = highCommand;
        this.highCondition = highCondition;
        this.lowCommand = lowCommand;
        this.lowCondition = lowCondition;
    }

    public Optional<String> testCondition(StateArray array, InputControl control, List<Input> inputs) {
        if (this.highCondition.test(array, inputs) && control.shouldTurnHigh()) {
            return Optional.of(this.highCommand);
        }

        if (this.lowCondition.test(array, inputs) && control.shouldTUrnLow()) {
            return Optional.of(this.lowCommand);
        }
        return Optional.empty();
    }

    public void resetCondition() {
        hasTurned = false;
    }

    public static boolean oneHigh(StateArray stateArray, List<Input> inputs) {
        return stateArray.testInputState((s) -> s == 1);
    }

    public static boolean allLow(StateArray stateArray, List<Input> inputs) {
        return !stateArray.testInputState((s) -> s == 1);
    }


    private static boolean switchLow(StateArray stateArray, List<Input> inputs) {
        Set<Integer> manualIds =
                inputs.stream()
                        .filter(e -> e.getType() == InputType.MANUAL || e.getType() == InputType.DIRECT_MANUAL)
                        .map(Input::getSourceId)
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet());

        return !stateArray.testInputState((s) -> manualIds.contains(s) && s == 0);


    }


}




