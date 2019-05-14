package jerry.consumer;

import com.google.gson.Gson;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class ClientRequestMessage {
    public RequestType type;

    Object argument;


    public static String fetchString(String ... arguments) {
        return new Gson().toJson(fetch(),ClientRequestMessage.class);
    }
        public static ClientRequestMessage fetch(String ... arguments){
        ClientRequestMessage message = new ClientRequestMessage();
        message.type = RequestType.FETCH;
        message.argument = arguments;
        return message;
    }


    public String[] argumentsAsArray() {
        if (argument instanceof Object[]) {
            return this.toStringArray((Object[])argument);
        }
        if (argument instanceof List) {
            return this.toStringArray(((List) argument).toArray());
        }
        throw new RuntimeException("could not convert argument into array");
    }

    public String argumentAsString() {
        return String.valueOf(argument);
    }

    private String[] toStringArray(Object[] input) {
        List<String> result = new ArrayList<>();
        for (Object o : input) {
            result.add(String.valueOf(o));
        }
        return result.stream().toArray(String[]::new);
    }

}
