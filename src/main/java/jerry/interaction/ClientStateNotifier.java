package jerry.interaction;

import com.google.gson.Gson;
import jerry.consumer.ClientRequestMessage;
import jerry.consumer.IConsumer;
import jerry.service.ClientStateRepository;
import jerry.service.IClientStateChangeNotifiable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class ClientStateNotifier extends AbstractStateNotifier implements IClientStateChangeNotifiable {

    @Autowired
    ClientStateRepository clientStateRepository;


    @Override
    public void addConsumer(IConsumer consumer) {
        super.addConsumer(consumer);
        writeToConsumer(consumer, clientStateRepository.getStateJson());
        clientStateRepository.setNotifier(this);
    }


    public void hasUpdatedClientState(String... keys) {
        log.trace("keys : {} started : {}", keys, started);

        if (!started) return;
        messages.offer(clientStateRepository.getStateJson(keys));
    }


    @Override
    public void handleConsumerMessage(TextMessage message) {
        ClientRequestMessage requestMessage = transformMessage(message);
        switch (requestMessage.type) {

            case FETCH:
                messages.offer(clientStateRepository.getStateJson(requestMessage.argumentsAsArray()));
                break;
            case CHANGE:
                clientInteractionManager.writeToProducer(requestMessage.argumentAsString());
                break;
        }
    }

    private ClientRequestMessage transformMessage(TextMessage message){
        String payload = message.getPayload();
        return new Gson().fromJson(payload,ClientRequestMessage.class);
    }
}
