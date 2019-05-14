package jerry.viewcontroller;


import jerry.interaction.AbstractInteractionManager;
import jerry.master.ClientStateUpdater;
import jerry.service.PersistenceService;
import jerry.util.ErrorForwardingConsumer;
import jerry.pojo.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping(value = "/master")
public class MasterController {

    @Autowired
    ClientStateUpdater clientStateUpdater;

    @Autowired
    @Qualifier("contextAdjustingInteractionManager")
    AbstractInteractionManager manager;


    @Value("${spring.profiles.active}")
    private String profile;

    @RequestMapping("")
    public String index(Model model) {
        if (profile.equals("client")) {
            return "/";
        }

        if (profile.equals("master")) {
            model.addAttribute("started",manager.hasStarted());
            model.addAttribute("states",clientStateUpdater.getClientStates());
            return "master";
        }
        throw new RuntimeException("unknown profile " + profile);
    }

}

