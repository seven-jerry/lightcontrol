package jerry.arduino;

import jerry.viewmodel.pojo.Input;

import java.util.Objects;


public class StateArray {
    private int[] outsideState;
    private int[][] state;
    private int x;
    private int y;
    private int o = 0;

    public StateArray(int x, int y, int o) {
        this.x = x;
        this.y = y;
        this.o = o;
        this.state = new int[x][y];
        this.outsideState = new int[o];
    }

    public StateArray(String state){
        int o = 0;
        int x = 0;
        int y = 0;
        String stateString = state + "";
        while (stateString.length() > 2) {
            char c = stateString.charAt(0);
            if (c == 'o') {
                o++;
                stateString = stateString.substring(3);
                continue;
            }
            int stateStringX = (int) stateString.charAt(0) - '0';
            int stateStringY = (int) stateString.charAt(1) - '0';
            if(stateStringX > x){
                x = stateStringX;
            }
            if(stateStringY > y){
                y = stateStringY;
            }
            stateString = stateString.substring(3);
        }
        this.x = ++x;
        this.y = ++y;
        this.o = o;
        this.state = new int[x][y];
        this.outsideState = new int[o];
        this.update(state);
    }

    public StateArray(StateArray array) {
        if (array == null) return;
        this.x = array.x;
        this.y = array.y;
        this.o = array.o;
        this.state = new int[x][y];
        this.outsideState = new int[o];

        for (int i = 0; i < x; i = i + 1) {
            for (int j = 0; j < y; j++) {
                state[i][j] = array.getState()[i][j];
            }
        }
        System.arraycopy(array.outsideState, 0, this.outsideState, 0, o);
    }

    public void setState(String stateString) {
        Objects.requireNonNull(stateString, "the state string must nt be null");
        if (stateString.startsWith("s") || stateString.startsWith("{")) {
            throw new IllegalStateException("the state string was not in expected state");
        }
        try {
            update(stateString);
        } catch (RuntimeException e) {
            System.out.println("StateArray got error on update :" + e + " for string <" + stateString + ">");
        }
    }

    public int[][] getState() {
        return this.state;
    }

    @Override
    public String toString() {
        StringBuilder sender = new StringBuilder();
        for (int i = 0; i < state.length; i = i + 1) {
            for (int j = 0; j < state[i].length; j++) {
                sender.append(i);
                sender.append(j);
                sender.append(state[i][j]);
            }
        }
        for (int i = 0; i < outsideState.length; i++) {
            sender.append('o');
            sender.append(i);
            sender.append(outsideState[i]);
        }
        return sender.toString();
    }

    private void update(String stateString) {
        if (stateString.length() < (x * y) * 3) {
            throw  new IllegalArgumentException("the state string was not long enough");
        }
        while (stateString.length() > 2) {
            char c = stateString.charAt(0);
            if (c == 'o') {
                int y = (int) stateString.charAt(1) - '0';
                int s = (int) stateString.charAt(2) - '0';
                outsideState[y] = s;
                stateString = stateString.substring(3);
                continue;
            }
            int x = (int) stateString.charAt(0) - '0';
            int y = (int) stateString.charAt(1) - '0';
            int s = (int) stateString.charAt(2) - '0';
            state[x][y] = s;
            stateString = stateString.substring(3);
        }
    }


    public int stateHashCode() {
        StringBuilder builder = new StringBuilder(this.x*this.y);
        for (int i = 0; i < state.length; i = i + 1) {
            for (int j = 0; j < state[i].length; j++) {
                builder.append(state[i][j]);
            }
        }
        return builder.toString().hashCode();
    }

    public boolean hasAny(ThriPredicate<Integer, Integer, Integer> predicate) {
        for (int i = 0; i < state.length; i = i + 1) {
            for (int j = 0; j < state[i].length; j++) {
                if (predicate.test(i, j, state[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }

    public void walk(ThriConsumer<Integer, Integer,Integer> consumer) {
        for (int i = 0; i < state.length; i = i + 1) {
            for (int j = 0; j < state[i].length; j++) {
                consumer.accept(i,j,state[i][j]);
            }
        }
    }
}
