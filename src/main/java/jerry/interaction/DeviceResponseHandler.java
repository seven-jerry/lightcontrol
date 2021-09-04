package jerry.interaction;

import jerry.service.ClientStateRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class DeviceResponseHandler implements IResponseHandler {

    @Autowired
    ClientStateRepository clientStateRepository;

    @Autowired
    ReadManager readManager;

    private StringBuilder messageBuilder = new StringBuilder();

    /**
     * @param data the pojo returned from the device
     */
    @Override
    public void handleMessage(String data) {
        log.trace(data);
        messageBuilder.append(data);
        String rawMessage = messageBuilder.toString();
        if (!rawMessage.contains("{") || !rawMessage.contains("}")) {
            return;
        }

        while (rawMessage.contains("{") && rawMessage.contains("}") && rawMessage.indexOf("{") < rawMessage.indexOf("}")) {
            try {
                System.out.println(rawMessage);
                String message = rawMessage.substring(rawMessage.indexOf("{") + 1, rawMessage.indexOf("}", rawMessage.indexOf("{")));

                this.proccessMessage(message);
            } catch (RuntimeException e) {
                log.error(e.toString());
            } finally {
                rawMessage = rawMessage.substring(rawMessage.indexOf("}", rawMessage.indexOf("{")) + 1);
            }
        }
        messageBuilder = new StringBuilder();


    }

    private void proccessMessage(String message) {
        log.trace(message);
        char type_char = message.charAt(0);

        if (type_char == 's') {
            StateArray stateArray = clientStateRepository.getState().getState().copy();
            StateCommand.fromCommand(message).callConsumer(stateArray);
            clientStateRepository.updateOutsideState(stateArray.outSideStateString(false));
            clientStateRepository.updateOutputState(stateArray.outputStateString(false));
            return;
        }
        if (Character.isDigit(type_char)) {
            clientStateRepository.updateOutputState(message);
            return;
        }

        StateArray stateArray = StateArray.parseString(message);
        boolean hasUpdated = clientStateRepository.updateInputState(stateArray.inputStateString(false));
        if (hasUpdated) {
            readManager.handleMessage(stateArray);
        }
        clientStateRepository.updateOutputState(stateArray.outputStateString(false));
        clientStateRepository.updateOutsideState(stateArray.outSideStateString(false));
    }


    public boolean updateInputState(StateArray message) {

        return true;
    }


}


