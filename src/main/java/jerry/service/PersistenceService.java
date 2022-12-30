package jerry.service;

import jerry.pojo.*;
import jerry.persist.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.springframework.util.StringUtils.hasText;


@Service
public class PersistenceService {
    IObjectPersistable<Setting> settingPersist;
    ICollectionPersistable<Input> inputPersist;
    ICollectionPersistable<Command> commandPersist;
    ICollectionPersistable<Client> clientPersist;
    ConcurrentHashMap<String, Command> commandMap = new ConcurrentHashMap<>();
    @Value("${base.folder}")
    public String settingsFolder;

    @PostConstruct
    public void init() {
        if (settingsFolder.equals("home")) {
            settingsFolder = System.getProperty("user.home");
        }
        settingPersist = new FilePersistedObject<Setting>(settingsFolder, "/setting.xml", Setting.class);
        inputPersist = new FilePersistedCollection<Input>(settingsFolder, "/input/", Input.class, "input", "xml");
        commandPersist = new FilePersistedCollection<Command>(settingsFolder, "/command/", Command.class, "command", "xml");
        clientPersist = new FilePersistedCollection<Client>(settingsFolder, "/client/", Client.class, "client", "xml");
    }


    public Setting getSetting() {
        Optional<Setting> optionalSetting = settingPersist.get();
        optionalSetting.ifPresent(e -> e.setInputs(inputPersist.getAvailabeEntries()));
        optionalSetting.ifPresent(e -> e.setCommands(commandPersist.getAvailabeEntries()));
        return optionalSetting.orElse(Setting.withDefaults());
    }

    public List<Command> getCommands() {
        return commandPersist.getAvailabeEntries();
    }

    public List<Input> getInputs() {
        return inputPersist.getAvailabeEntries();
    }

    public void addSetting(Setting setting) {
        settingPersist.set(setting);
        inputPersist.removeAllEntries();
    }

    public void removeSetting() {
        settingPersist.remove();
        inputPersist.removeAllEntries();
    }

    public void updateSetting(Consumer<Setting> consumer) {
        if (!settingPersist.get().isPresent()) {
            settingPersist.set(Setting.withDefaults());
            return;
        }
        settingPersist.update(consumer);
        commandMap.clear();
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
        if (!hasText(command.getCommand()) || !hasText(command.getLabel()) || command.getCommand().length() != 3) {
            throw new IllegalArgumentException("no valid command");
        }

        for (Command availabeEntry : commandPersist.getAvailabeEntries()) {
            if (availabeEntry.getCommand().equals(command.getCommand())) {
                commandPersist.removeEntryById(availabeEntry.getId());
            }
        }
        commandPersist.addEntry(command);
        commandMap.clear();
    }

    public void removeCommand(String id) {
        commandPersist.removeEntryById(Integer.valueOf(id));
        commandMap.clear();
    }

    public void addClient(Client client) {
        Objects.requireNonNull(client, "the client provided was null");
        clientPersist.addEntry(client);
    }

    public List<Client> getClients() {
        return clientPersist.getAvailabeEntries();
    }

    public void removeClient(String id) {
        clientPersist.removeEntryById(Integer.valueOf(id));
    }


    public StateCommandOverwrite newStateControlOverwrite() {
        StateCommandOverwrite overwrite = new StateCommandOverwrite();
        return overwrite;
    }

    public Command getCommand(String key) {
        return commandMap.computeIfAbsent(key, (k) -> {
            for (Command availabeEntry : commandPersist.getAvailabeEntries()) {
                if (availabeEntry.getCommand().equals(k)) {
                    return availabeEntry;
                }
            }
            return null;
        });
    }
}
