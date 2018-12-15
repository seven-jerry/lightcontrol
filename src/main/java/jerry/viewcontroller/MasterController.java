package jerry.viewcontroller;


import jerry.master.ClientStateFetcher;
import jerry.service.PersistenceService;
import jerry.util.ErrorForwardingConsumer;
import jerry.viewmodel.pojo.Client;
import jerry.viewmodel.pojo.ClientState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;



@Controller
@RequestMapping(value = "/master")
public class MasterController {

    @Autowired
    PersistenceService service;

    @Autowired
    ClientController clientController;

    @Value("${spring.profiles.active}")
    private String profile;

    private ClientStateFetcher fetcher;

    @RequestMapping("")
    public String index(Model model) {
        if(profile.equals("client")){
            return "redirect:/client";
        }

        if(profile.equals("master")) {
            return this.masterPage(model, m -> {
            });
        }
        throw new RuntimeException("unknown profile "+profile);
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Client client, Model model) {
        Objects.requireNonNull(client.getIpAddress(), "test");
        service.addClient(client);
        return this.masterPage(model, m -> {
        });
    }

    @GetMapping("/remove")
    public String remove(@RequestParam("id") String id, Model model) {
        service.removeClient(id);
        return this.masterPage(model, m -> {
        });
    }

    @GetMapping("/start")
    public String start(Model model) {
        return this.masterPage(model, m -> {
            if (this.fetcher == null) {
                this.fetcher = new ClientStateFetcher(service.getClients());
                this.fetcher.start();
                m.addAttribute("started", true);
            }

        });
    }

    @GetMapping(value = "/execute")
    @ResponseBody
    public String executeCommand(@RequestParam(value = "command", required = true) String command,@RequestParam(value = "ids", required = true) String ids, Model model) {
        fetcher.write(ids,command);
        return "{label:ok}";
    }

    @GetMapping("/stop")
    public String stop(Model model) {
        return this.masterPage(model, m -> {
            Optional.ofNullable(fetcher).ifPresent(Thread::interrupt);
            fetcher = null;
            m.addAttribute("started", false);
        });
    }

    @GetMapping("/list")
    @ResponseBody
    public String list() {
        if(fetcher == null){
            return "";
        }
        return fetcher.getStates();
    }

    public String masterPage(Model model, ErrorForwardingConsumer<Model> action) {
        try {
            model.addAttribute("clients", service.getClients());
            model.addAttribute("newClient", new Client());
            model.addAttribute("started", false);
            model.addAttribute("profile", profile);
            action.accept(model);
            Optional.ofNullable(fetcher).ifPresent(
                    e -> model.addAttribute("started", fetcher.hasStarted())
            );
        } catch (Exception e) {
            model.addAttribute("error", String.valueOf(e));
        }
        return "master";
    }


    @PostMapping("/receiver/add")
    public String receiverAdd(@ModelAttribute Client client, Model model) {
        Objects.requireNonNull(client.getIpAddress(), "test");
        service.addClient(client);
        return this.masterPage(model, m -> {
        });
    }

}

