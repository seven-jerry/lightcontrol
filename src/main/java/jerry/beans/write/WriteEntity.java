package jerry.beans.write;

import jerry.arduino.ReadCallback;
import jerry.beans.KeyValuePair;

public class WriteEntity {
    public String getWriteContent() {
        return writeContent;
    }

    protected String writeContent;
    protected ReadCallback callback;

    public WriteEntity(String writeContent,ReadCallback callback){
        this.writeContent = writeContent;
        this.callback = callback;
    }

    public void response(KeyValuePair pair){
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
