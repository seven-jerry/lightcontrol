package jerry.viewcontroller;

import jerry.interaction.AbstractInteractionManager;
import jerry.interaction.ReadManager;
import jerry.pojo.StateCommandOverwrite;
import jerry.service.ClientStateRepository;
import jerry.pojo.Command;
import jerry.service.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/setting")
public class SettingCommandController {
    @Autowired
    PersistenceService persistenceService;

    @Autowired
    SettingsController settingsController;

    @Autowired
    ClientStateRepository clientStateRepository;

    @Autowired
    @Qualifier("contextAdjustingInteractionManager")
    AbstractInteractionManager lifeCycleClientInteractionManager;

    @Autowired
    ReadManager readManager;

    @PostMapping(value = "/command/add")
    public String addInput(@ModelAttribute Command command, Model model) {
        persistenceService.addCommand(command);
        clientStateRepository.updateCommands(persistenceService.getCommands());
        return settingsController.details(model);
    }

    @GetMapping(value = "/command/delete")
    public String deleteInput(@RequestParam(value = "id", required = true) String id, Model model) {
        persistenceService.removeCommand(id);
        clientStateRepository.updateCommands(persistenceService.getCommands());
        return settingsController.details(model);
    }




    @PostMapping(value = "/statecontroloverwrite/add")
    public String addStateCommandOverwrite(@ModelAttribute StateCommandOverwrite overwrite, Model model) {
        persistenceService.addStateCommandOverwrite(overwrite);
        return settingsController.details(model);
    }

    @GetMapping(value = "/statecontroloverwrite/delete")
    public String deleteStateControlOverwrite(@RequestParam(value = "id", required = true) String id, Model model) {
        persistenceService.removeStateCommandOverwrite(id);
        return settingsController.details(model);
    }

}
