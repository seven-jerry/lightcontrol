package jerry.interaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EventHandler implements IMessageable {

    public static enum Type {
        ERROR,
        TRACE
    }

    Map<LocalDateTime, String> events = new ConcurrentHashMap<>();

    @Override
    public void pushMessage(String message) {
        log.info(message);
        events.put(LocalDateTime.now(), message);
    }

    public void pushMessage(Type type, String message) {
        log.info(type.name() + " : " + message);
        events.put(LocalDateTime.now(), type.name() + " : " + message);
    }

    private void purge() {
        this.events.keySet().removeIf(e -> e.compareTo(LocalDateTime.now().minusMinutes(5)) < 0);

    }

    public Collection<String> getAllEvents() {
        this.purge();
        return this.events.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.toList());
    }
}
