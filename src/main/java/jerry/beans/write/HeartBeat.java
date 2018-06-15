package jerry.beans.write;

import jerry.arduino.ReadCallback;

public class HeartBeat extends WriteEntity{
    public HeartBeat(ReadCallback callback){
        super("shb",callback);
    }

    @Override
    public String toString() {
        return "WriteEntity{" +
                "writeContent='" + writeContent + '\'' +
                ", callback=" + callback +
                '}';
    }
}
