package jerry.interaction;

public enum InputControl {
    ISLAND_MODE("Auto",true,true),
    LOCAL("locale",true,true),
    MANUAL("Hand",false,false),
    HOME_ASSIST("Home Assist",true,true);
    boolean high;
    boolean low;
    InputControl(String label,boolean high,boolean low){
        this.low = low;
        this.high = high;
    }

    public boolean shouldTurnHigh(){
        return high;
    }
    public boolean shouldTUrnLow(){
        return low;
    }
}
