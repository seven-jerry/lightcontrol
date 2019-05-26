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

import java.util.Optional;


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

    @RequestMapping(value = {"", "/", "/{host}/"})
    public String index(Model model, @PathVariable("host") Optional<String> host) {
        if (profile.contains("client")) {
            return "/";
        }

        if (profile.contains("master")) {
            model.addAttribute("started", manager.hasStarted());
            model.addAttribute("states", clientStateUpdater.getClientStateMap());
            if (host.isPresent()) {
                model.addAttribute("host", host.get());
            } else {
                model.addAttribute("host", "local");
            }
            return "master";
        }
        throw new RuntimeException("unknown profile " + profile);
    }

}

