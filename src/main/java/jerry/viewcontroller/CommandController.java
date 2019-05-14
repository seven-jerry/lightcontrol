package jerry.viewcontroller;

import jerry.interaction.InputControl;
import jerry.interaction.ReadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/command")
public class CommandController {

    @Autowired
    ReadManager readManager;

    @RequestMapping(value = {"", "/", "/{host}/"})
    public String index(Model model, @PathVariable("host") Optional<String> host) {
        model.addAttribute("inputControls", InputControl.values());
        model.addAttribute("inputControl", readManager.getInputControl());
        if (host.isPresent()) {
            model.addAttribute("host", host.get());
        } else {
            model.addAttribute("host", "local");
        }
        return "command";
    }

    @PostMapping("/inputcontrol/set")
    public String setInputControl(InputControl control) {
        readManager.setInputControl(control);
        return "redirect:/command";
    }
}
