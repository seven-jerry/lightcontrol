package jerry.viewcontroller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jerry.arduino.StateNotifier;
import jerry.interaction.InputControl;
import jerry.interaction.ReadManager;
import jerry.viewmodel.pojo.Command;
import jerry.service.PersistenceService;
import jerry.viewmodel.pojo.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/setting/command")
public class SettingCommandController {
    @Autowired
    PersistenceService persistenceService;

    @Autowired
    SettingsController settingsController;

    @Autowired
    StateNotifier notifier;

    @Autowired
    jerry.interaction.Controller lifeCycleController;

    @Autowired
    ReadManager readManager;

    @PostMapping(value = "/add")
    public String addInput(@ModelAttribute Command command, Model model) {
        persistenceService.addCommand(command);
        return settingsController.details(model);
    }

    @GetMapping(value = "/delete")
    public String deleteInput(@RequestParam(value = "id", required = true) String id, Model model) {
        persistenceService.removeCommand(id);
        return settingsController.details(model);
    }

    @GetMapping(value = "/execute")
    @ResponseBody
    public String executeCommand(@RequestParam(value = "command", required = true) String id, Model model) {
       notifier.produceOnce(id);
        return "{label:ok}";
    }

    @GetMapping("/list")
    @ResponseBody
    public String listComands(){
        try {
            lifeCycleController.start();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("commands"+e);
        }

        Setting s = persistenceService.getSetting();
        JsonObject jobj = new JsonObject();
        jobj.add("commands",new Gson().toJsonTree(s.getCommands()));
        jobj.add("state",new Gson().toJsonTree(notifier.getLastState()));
        jobj.add("inputs",new Gson().toJsonTree(readManager.getLastState().toString()));
        jobj.add("labeledInputs",new Gson().toJsonTree(s.labeledInput(readManager.getLastState())));
        return jobj.toString();
    }

}
