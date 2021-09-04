package jerry.interaction;


import jerry.pojo.Input;
import jerry.util.ThriConsumer;
import jerry.util.ThriIntFunction;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Slf4j
public class StateArray {
    private static final int DISABLED_STATE = 7;
    private static final int INDEX_LENGTH_OFFSET = 1;

    public static final char OUTPUT_CHAR = 'o';
    public static final char INPUT_CHAR = 'i';
    public static final char OUTSIDE_CHAR = 'u';

    private int[][] outputState;
    private int[] inputState;
    private int[] outsideState;

    private int output_x;
    private int output_y;


    public static StateArray empty(int output_x, int output_y, int input_count, int outside_count) {
        return new StateArray(output_x, output_y, input_count, outside_count);
    }

    public static StateArray parseString(String input) {
        StateArray stateArray = StateArray.empty(0, 0, 0, 0);
        log.trace(input);
        Objects.requireNonNull(input);
        input = input.trim();
        requireBraces(input);
        input = input.replace("{", "").replace("}", "");
        initStateWithString(stateArray, input);
        return stateArray;
    }

    private static void initStateWithString(StateArray stateArray, String input) {
        String[] components = input.split("(?=[a-z])");
        for (String component : components) {
            char type_char = component.charAt(0);
            component = component.substring(1);
            switch (type_char) {
                case OUTPUT_CHAR:
                    stateArray.initOutputState(component);
                    stateArray.updateOutputState(component);
                    break;
                case INPUT_CHAR:
                    stateArray.initInputState(component);
                    stateArray.updateInputState(component);
                    break;
                case OUTSIDE_CHAR:
                    stateArray.initOutSideState(component);
                    stateArray.updateOutsideState(component);
                    break;
                default:
                    throw new IllegalArgumentException("could not parse character ");
            }
        }
    }

    private void initOutputState(String state) {
        output_x = Character.getNumericValue(state.charAt(state.length() - 3)) + INDEX_LENGTH_OFFSET;
        output_y = Character.getNumericValue(state.charAt(state.length() - 2)) + INDEX_LENGTH_OFFSET;
        outputState = new int[output_x][output_y];
    }

    private void initInputState(String state) {
        if (state.length() <= 1) {
            inputState = new int[0];
            return;
        }
        inputState = new int[Character.getNumericValue(state.charAt(state.length() - 2)) + INDEX_LENGTH_OFFSET];
    }

    private void initOutSideState(String state) {
        if (state.length() <= 1) {
            outsideState = new int[0];
            return;
        }
        outsideState = new int[Character.getNumericValue(state.charAt(state.length() - 2)) + INDEX_LENGTH_OFFSET];
    }

    private static void requireBraces(String input) {
        if (!input.contains("{") && !input.contains("}")) {
            return;
        }
        if (input.indexOf('{') != input.lastIndexOf('{')) {
            throw new IllegalArgumentException("more than one curly brace found");
        }
        if (input.indexOf('}') != input.lastIndexOf('}')) {
            throw new IllegalArgumentException("more than one curly brace found");
        }

    }


    private StateArray(int x, int y, int i, int o) {
        this.output_x = x;
        this.output_y = y;

        this.outputState = new int[x][y];
        this.inputState = new int[i];
        this.outsideState = new int[o];
    }


    public boolean testInputState(Predicate<Integer> predicate) {
        for (int i : inputState) {
            if (predicate.test(i)) {
                return true;
            }

        }
        return false;
    }


    public void updateOutputState(String state) {
        state = removeBulk(state);
        char[] stateChars = state.toCharArray();
        for (int i = 0; i < stateChars.length; i += 3) {
            int x = Character.getNumericValue(stateChars[i]);
            int y = Character.getNumericValue(stateChars[i + 1]);
            int s = Character.getNumericValue(stateChars[i + 2]);
            if(outputState[x][y] != StateArray.DISABLED_STATE) {
                outputState[x][y] = s;
            }
        }
    }

    public void updateInputState(String state) {
        state = removeBulk(state);
        updateArray(state, inputState);
    }


    public void updateOutsideState(String state) {
        state = removeBulk(state);
        updateArray(state, outsideState);
    }

    private void updateArray(String state, int[] array) {

        if (state.length() > array.length * 2) {
            log.error("state {} too long for {}", state, array);
            throw new IllegalArgumentException("the state string is too long");
        }

        char[] stateChars = state.toCharArray();
        for (int i = 0; i < stateChars.length; i += 2) {
            int x = Character.getNumericValue(stateChars[i]);
            int s = Character.getNumericValue(stateChars[i + 1]);
            array[x] = s;
        }
    }

    public void walkInputState(BiConsumer<Integer, Integer> consumer) {
        for (int i = 0; i < inputState.length; i = i + 1) {
            consumer.accept(i, inputState[i]);
        }
    }
    public int countTurnedOnLights() {
        int count = 0;
        for (int i = 0; i < output_x; i++) {
            for (int j = 0; j < output_y; j++) {
              if(outputState[i][j] == 1){
                  count++;
              }
            }
        }
        return count;
    }

    public void changeOutputState(ThriIntFunction function) {
        for (int i = 0; i < output_x; i++) {
            for (int j = 0; j < output_y; j++) {
                int new_state = function.apply(i, j, outputState[i][j]);
                if (outputState[i][j] != StateArray.DISABLED_STATE) {
                    outputState[i][j] = new_state;
                }
            }
        }
    }

    public int[][] getOutputStateArray() {
        return this.outputState;
    }

    public synchronized String outputStateString(boolean addBraces) {
        StringBuilder builder = startString(addBraces);
        builder.append(OUTPUT_CHAR);
        for (int i = 0; i < output_x; i++) {
            for (int j = 0; j < output_y; j++) {
                builder.append(i).append(j).append(outputState[i][j]);
            }
        }
        if (addBraces) builder.append("}");
        return builder.toString();
    }

    @Override
    public synchronized String toString() {
        StringBuilder builder = new StringBuilder("{");
        builder.append(outputStateString(false));
        builder.append(inputStateString(false));
        builder.append(outSideStateString(false));
        builder.append("}");
        return builder.toString();
    }

    public String inputStateString(boolean addBraces) {
        StringBuilder builder = startString(addBraces);
        builder.append(INPUT_CHAR);
        addOneDimentionalState(builder, inputState);
        if (addBraces) builder.append("}");
        return builder.toString();
    }


    public String outSideStateString(boolean addBraces) {
        StringBuilder builder = startString(addBraces);
        builder.append(OUTSIDE_CHAR);
        addOneDimentionalState(builder, outsideState);
        if (addBraces) builder.append("}");
        return builder.toString();
    }

    private StringBuilder startString(boolean addBraces) {
        StringBuilder builder = new StringBuilder();
        if (addBraces) builder.append("{");
        return builder;
    }

    private void addOneDimentionalState(StringBuilder builder, int[] array) {
        for (int i = 0; i < array.length; i++) {
            builder.append(i).append(array[i]);
        }
    }


    public boolean equalsInputState(String state) {
        return this.inputStateString(false).equals(state);
    }

    public String getInputState() {
        return this.inputStateString(false).replace("" + INPUT_CHAR, "");
    }


    public boolean equalsOutputState(String state) {
        return this.outputStateString(false).equals(state);
    }

    public String getOutputState() {
        return this.outputStateString(false).replace("" + OUTPUT_CHAR, "");
    }

    public boolean equalsOutsideState(String state) {
        return this.outSideStateString(false).equals(state);
    }

    public String getOutsideState() {
        return this.outSideStateString(false).replace("" + OUTSIDE_CHAR, "");

    }

    public StateArray copy() {
        return StateArray.parseString(this.toString());
    }

    private String removeBulk(String input) {
        return input.replace("{", "")
                .replace("}", "").replace("" + INPUT_CHAR, "")
                .replace("" + OUTPUT_CHAR, "").replace("" + OUTSIDE_CHAR, "");
    }

    public void changeOutside(BiFunction<Integer, Integer, Integer> consumer) {
        for (int i = 0; i < outsideState.length; i = i + 1) {
            int s = consumer.apply(i, outsideState[i]);
            outsideState[i] = s;
        }
    }


    public boolean hasOutsideState() {
        return outsideState != null && outsideState.length > 0;
    }
}
