package jerry.viewcontroller;

import jerry.arduino.InputCommand;
import jerry.arduino.SerialSources;
import jerry.viewmodel.InputType;
import jerry.service.PersistenceService;
import jerry.service.SerialService;
import jerry.viewmodel.pojo.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "")
public class SettingsController {

    @Autowired
    PersistenceService persistenceService;

    @Autowired
    jerry.interaction.Controller lifeCycleController;


    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("setting", persistenceService.getSetting());

            if(!model.containsAttribute("info")) {
            model.addAttribute("info", "controller has started :" + lifeCycleController.hasStarted());
        }
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
        return "setting/detail";
    }


    @PostMapping(value = "/setting/update")
    public String update(final @ModelAttribute Setting setting, Model model) {

            persistenceService.updateSetting(s -> {
                s.setRows(setting.getRows());
                s.setColumns(setting.getColumns());
                s.setOutside(setting.getOutside());
                s.setInputCommand(setting.getInputCommand());

            });
        return this.details(model);
    }

}
