package jerry.viewcontroller;


import jerry.master.MasterMain;
import jerry.service.PersistenceService;
import jerry.util.ErrorForwardingConsumer;
import jerry.viewmodel.pojo.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.function.Consumer;
import java.util.function.Function;


@Controller
@RequestMapping(value = "/master")
public class MasterController {

    @Autowired
    PersistenceService service;

    @GetMapping("")
    public String index(Model model) {
        return this.masterPage(model, m -> {
        });


    }

    @PostMapping("/add")
    public String add(@ModelAttribute Client client, Model model) {
        service.addClient(client);
        return this.masterPage(model, m->{});
    }

    @GetMapping("/remove")
    public String remove(@RequestParam("id") String id, Model model) {
        service.removeClient(id);
        return this.masterPage(model,m->{});
    }


    private String masterPage(Model model, ErrorForwardingConsumer<Model> action) {
        try {
            model.addAttribute("clients", service.getClients());
            model.addAttribute("started", MasterMain.hasStarted);
            model.addAttribute("newClient", new Client());

            action.accept(model);
        } catch (Exception e) {
            model.addAttribute("error", e.toString());
        }
        return "master";
    }
}

