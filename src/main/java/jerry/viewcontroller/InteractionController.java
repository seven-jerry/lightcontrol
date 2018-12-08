package jerry.viewcontroller;

import jerry.util.ErrorForwardingConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/interaction")
public class InteractionController {

    @Autowired
    SettingsController settingsController;

    @Autowired
    jerry.interaction.Controller lifeCycleController;


    @GetMapping("/start")
    public String start(Model model) {
        return this.indexPage(model, m -> {
            String info = lifeCycleController.start();
            m.addAttribute("info", info);
        });

    }

    @GetMapping("/hasstarted")
    public String hasstarted(Model model) {
        return this.indexPage(model, m -> {
            m.addAttribute("info", "controller has started :" + lifeCycleController.hasStarted());
        });
    }

    @GetMapping("/stop")
    public String stop(Model model) {
        return this.indexPage(model, m -> {
            lifeCycleController.stop();
            m.addAttribute("info", "controller has stopped.");
        });
    }


    private String indexPage(Model model, ErrorForwardingConsumer<Model> action) {
        try {
            action.accept(model);
        } catch (Exception e) {
            model.addAttribute("error", e.toString());
        }
        return settingsController.index(model);
    }
}
