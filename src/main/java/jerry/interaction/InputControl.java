package jerry.interaction;

public enum InputControl {
    AUTO("Auto",true,true),
    MANUAL("Hand",false,false),
    AUTO_OFF_MANUAL_ON("Auto Aus Hand Ein",false,true),
    AUTO_ON_MAN_OFF("Auto Ein Hand Aus",true,false);

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
