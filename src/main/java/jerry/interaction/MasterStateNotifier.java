package jerry.interaction;

import com.google.gson.Gson;
import jerry.consumer.ClientRequestMessage;
import jerry.consumer.IConsumer;
import jerry.master.ClientStateUpdater;
import jerry.master.IMasterChangeNotifiable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

@Component
@Slf4j
public class MasterStateNotifier extends AbstractStateNotifier implements IMasterChangeNotifiable {


    @Autowired
    ClientStateUpdater clientStateUpdater;


    @Override
    public void addConsumer(IConsumer consumer) {
        super.addConsumer(consumer);
        writeToConsumer(consumer, clientStateUpdater.getClientStateJson());
        clientStateUpdater.setNotifier(this);
    }




    @Override
    public void handleConsumerMessage(TextMessage message) {
        ClientRequestMessage requestMessage = transformMessage(message);
        switch (requestMessage.type) {
            case FETCH:
                messages.offer(clientStateUpdater.getClientStateJson(requestMessage.argumentsAsArray()));
                break;
            case CHANGE:
                interactionManager.writeToProducer(requestMessage.argumentAsMap());
                break;
        }
    }

    private ClientRequestMessage transformMessage(TextMessage message){
        String payload = message.getPayload();
        return new Gson().fromJson(payload,ClientRequestMessage.class);
    }

    @Override
    public void hasUpdatedClientState(String state) {
        log.trace("state : {} started : {}", state, started);
        if (!started) return;
        messages.offer(state);
    }
}
