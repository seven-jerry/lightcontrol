package jerry.viewcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Index {
    @Autowired
    jerry.interaction.Controller lifeCycleController;

    @Autowired
    SettingsController settingsController;


    @GetMapping("/switch")
    public String switchPage(Model model) {
        return "switch";
    }
}
