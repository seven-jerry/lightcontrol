package jerry.viewcontroller;


import jerry.service.PersistenceService;
import jerry.util.ErrorForwardingConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping(value = "/client")
public class ClientController {

    @Autowired
    PersistenceService service;

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private MasterController controller;


    @RequestMapping("")
    public String index(Model model) {
        if(profile.equals("master")) {
            return "redirect:/master";
        }
        if(!profile.equals("client")){
            throw new RuntimeException("unknown profile "+profile);
        }

        return this.clientPage(model, m -> {
            String masterUrl = service.getSetting().getMasterUrl();
            m.addAttribute("masterUrl",masterUrl);
        });
    }

    public String clientPage(Model model, ErrorForwardingConsumer<Model> action) {
        try {
            action.accept(model);
        } catch (Exception e) {
            model.addAttribute("error", String.valueOf(e));
        }
        return "client";
    }
}

