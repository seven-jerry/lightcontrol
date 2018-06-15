package jerry.beans;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KeyValuePair {
    String key;
    String value;


    public static KeyValuePair fromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            KeyValuePair pair = objectMapper.readValue(json, KeyValuePair.class);
            return pair;

        } catch (Exception e) {
            return null;
        }
    }
    public KeyValuePair(){
        super();
    }
    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String toJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
            return json;
        } catch (Exception e) {
            return null;
        }


    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
