package jerry.service;

import jerry.viewmodel.pojo.Client;
import jerry.viewmodel.pojo.Command;
import jerry.viewmodel.pojo.Input;
import jerry.viewmodel.pojo.Setting;
import jerry.persist.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;


@Service
public class PersistenceService {
    IObjectPersistable<Setting> settingPersist = new FilePersistedObject<Setting>("/setting.xml", Setting.class);
    ICollectionPersistable<Input> inputPersist = new FilePersistedCollection<Input>("/input/", Input.class, "input", "xml");
    ICollectionPersistable<Command> commandPersist = new FilePersistedCollection<Command>("/command/", Command.class, "command", "xml");
    ICollectionPersistable<Client> clientPersist = new FilePersistedCollection<Client>("/client/", Client.class, "client", "xml");

    public Setting getSetting() {
        Optional<Setting> optionalSetting = settingPersist.get();
        optionalSetting.ifPresent(e -> e.setInputs(inputPersist.getAvailabeEntries()));
        optionalSetting.ifPresent(e -> e.setCommands(commandPersist.getAvailabeEntries()));
        return optionalSetting.orElse(Setting.withDefaults());
    }

    public void addSetting(Setting setting) {
        settingPersist.set(setting);
        inputPersist.removeAllEntries();
    }

    public void removeSetting() {
        settingPersist.remove();
        inputPersist.removeAllEntries();
    }
    public void updateSetting(Consumer<Setting> consumer){
        if(!settingPersist.get().isPresent()){
            settingPersist.set(Setting.withDefaults());
            return;
        }
        settingPersist.update(consumer);

    }



    public Input newInput() {
        return Input.withDefaults();
    }

    public Command newCommand() {
        return Command.withDefaults();
    }

    public void addInput(Input input) {
        inputPersist.addEntry(input);
    }
    public void removeInput(String id) {
        inputPersist.removeEntryById(Integer.valueOf(id));
    }

    public void addCommand(Command command) {
        commandPersist.addEntry(command);
    }
    public void removeCommand(String id) {
        commandPersist.removeEntryById(Integer.valueOf(id));
    }

    public void addClient(Client client){
        Objects.requireNonNull(client,"the client provided was null");
        clientPersist.addEntry(client);
    }
    public List<Client> getClients(){
        return clientPersist.getAvailabeEntries();
    }

    public void removeClient(String id) {
        clientPersist.removeEntryById(Integer.valueOf(id));
    }
}
