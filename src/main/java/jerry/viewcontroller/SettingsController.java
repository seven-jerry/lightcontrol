package jerry.viewcontroller;

import jerry.interaction.*;
import jerry.device.SerialSources;
import jerry.pojo.InputType;
import jerry.service.PersistenceService;
import jerry.service.SerialService;
import jerry.pojo.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "")
public class SettingsController {

    @Autowired
    PersistenceService persistenceService;

    @Autowired
    @Qualifier("contextAdjustingInteractionManager")
    AbstractInteractionManager lifeCycleClientInteractionManager;

    @Autowired
    EventHandler eventHandler;

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("setting", persistenceService.getSetting());
        if (!model.containsAttribute("info")) {
            model.addAttribute("info", "has started :" + lifeCycleClientInteractionManager.hasStarted());
        }
        model.addAttribute("events", eventHandler.getAllEvents());
        return "setting";
    }

    @GetMapping(value = "/setting/detail")
    public String details(Model model) {
        model.addAttribute("setting", persistenceService.getSetting());
        model.addAttribute("newInput", persistenceService.newInput());
        model.addAttribute("newCommand", persistenceService.newCommand());
        model.addAttribute("types", InputType.values());
        model.addAttribute("ports", SerialService.allSerialPorts());
        model.addAttribute("sources", SerialSources.values());
        model.addAttribute("inputCommands", InputCommand.values());
        model.addAttribute("inputControls", InputControl.values());

        model.addAttribute("stateCommands", StateCommand.values());
        model.addAttribute("stateCommandOverwrites", persistenceService.getStateCommandOverwrites());
        model.addAttribute("newStateCommandOverwrite", persistenceService.newStateControlOverwrite());

        return "setting/detail";
    }


    @PostMapping(value = "/setting/update")
    public String update(final @ModelAttribute Setting setting, Model model) {

        persistenceService.updateSetting(s -> {
            s.setRows(setting.getRows());
            s.setColumns(setting.getColumns());
            s.setOutside(setting.getOutside());
            s.setInputCommand(setting.getInputCommand());
            s.setMasterUrl(setting.getMasterUrl());
            s.setControl(setting.getControl());
        });
        return this.details(model);
    }

}
