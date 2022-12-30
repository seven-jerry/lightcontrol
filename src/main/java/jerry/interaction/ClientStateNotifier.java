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
    public void handleConsumerMessage(String message) {
        ClientRequestMessage requestMessage = transformMessage(message);
        switch (requestMessage.type) {

            case FETCH:
                messages.offer(clientStateRepository.getStateJson(requestMessage.argumentsAsArray()));
                break;
            case DISABLE:
                String args = requestMessage.argumentAsString();
                if (args.length() != 2) {
                    return;
                }
                int x = Character.getNumericValue(args.charAt(0));
                int y = Character.getNumericValue(args.charAt(1));
                int state = clientStateRepository.getState().getState().getStateForIndex(x, y);
                if (state == StateArray.DYNAMIC_DISABLED_STATE) {
                    interactionManager.writeToProducer("e" + x + "" + y);
                } else {
                    interactionManager.writeToProducer("d" + x + "" + y);
                }
                break;
            case CHANGE:
                interactionManager.writeToProducer(requestMessage.argumentAsString());
                break;
        }
    }

    private ClientRequestMessage transformMessage(String payload) {
        return new Gson().fromJson(payload, ClientRequestMessage.class);
    }
}
