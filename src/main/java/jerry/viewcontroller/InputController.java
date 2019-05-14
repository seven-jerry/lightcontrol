package jerry.viewcontroller;

import jerry.service.ClientStateRepository;
import jerry.pojo.Input;
import jerry.service.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/setting/input")
public class InputController {
    @Autowired
    SettingsController settingsController;

    @Autowired
    PersistenceService persistenceService;

    @Autowired
    ClientStateRepository clientStateRepository;

    @PostMapping(value = "/add")
    public String addInput(@ModelAttribute Input input, Model model) {
        persistenceService.addInput(input);
        return settingsController.details(model);
    }

    @GetMapping(value = "/delete")
    public String deleteInput(@RequestParam(value = "id", required = true) String id, Model model) {
        persistenceService.removeInput(id);
        return settingsController.details(model);
    }
}
