package jerry.beans.write;

import jerry.arduino.ReadCallback;
import jerry.beans.KeyValuePair;

public class InputEntity extends WriteEntity {

    public InputEntity(ReadCallback callback) {
        super("sin", callback);
    }

    @Override
    public void response(KeyValuePair pair){
        String value = pair.getValue();
        value = value.substring(1);
        pair.setValue(value);
        this.callback.contentFromArduino(pair);
    }
    @Override
    public String toString() {
        return "WriteEntity{" +
                "writeContent='" + writeContent + '\'' +
                ", callback=" + callback +
                '}';
    }
}
