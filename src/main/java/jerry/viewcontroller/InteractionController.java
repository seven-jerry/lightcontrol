package jerry.viewcontroller;

import jerry.interaction.AbstractInteractionManager;
import jerry.util.ErrorForwardingConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("contextAdjustingInteractionManager")
    AbstractInteractionManager lifeCycleClientInteractionManager;




    @GetMapping("/start")
    public String start(Model model) {
        return this.indexPage(model, m -> {
            String info = lifeCycleClientInteractionManager.start();
            m.addAttribute("info", info);
        });

    }

    @GetMapping("/hasstarted")
    public String hasstarted(Model model) {
        return this.indexPage(model, m -> {
            m.addAttribute("info", "has started :" + lifeCycleClientInteractionManager.hasStarted());
        });
    }

    @GetMapping("/stop")
    public String stop(Model model) {
        return this.indexPage(model, m -> {
            lifeCycleClientInteractionManager.stop();
            m.addAttribute("info", "singleLightController has stopped.");
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
