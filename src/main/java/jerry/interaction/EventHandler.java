package jerry.interaction;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventHandler implements IMessageable{

    public static enum Type{
        ERROR
    }

    Map<LocalDateTime, String> events = new ConcurrentHashMap<>();

    @Override
    public void pushMessage(String message){
        events.put(LocalDateTime.now(), message);
    }

    public void pushMessage(Type type,String message){
        events.put(LocalDateTime.now(), type.name() + " : "+message);
    }

    private void purge(){
        this.events.keySet().removeIf(e -> e.compareTo(LocalDateTime.now().minusMinutes(5)) < 0);

    }

    public Collection<String> getAllEvents() {
        this.purge();
        return this.events.values();
    }
}
