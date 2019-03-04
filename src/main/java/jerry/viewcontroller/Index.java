package jerry.viewcontroller;

import jerry.interaction.InputControl;
import jerry.interaction.ReadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Index {
    @Autowired
    jerry.interaction.Controller lifeCycleController;


    @Autowired
    SettingsController settingsController;

    @Autowired
    ReadManager readManager;

    @GetMapping("/switch")
    public String switchPage(Model model) {
        model.addAttribute("inputControls", InputControl.values());
        model.addAttribute("inputControl",readManager.getInputControl());
        return "switch";
    }

    @PostMapping("/switch/inputcontrol/set")
    public String setInputControl(InputControl control){
        readManager.setInputControl(control);
        return "redirect:/switch";
    }
}
