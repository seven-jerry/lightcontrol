package jerry.viewcontroller;

import jerry.interaction.InputControl;
import jerry.interaction.ReadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/command")
public class CommandController {

    @Autowired
    ReadManager readManager;

    @RequestMapping("")
    public String index(Model model){
        model.addAttribute("inputControls",InputControl.values());
        model.addAttribute("inputControl",readManager.getInputControl());
        return "command";
    }
    @PostMapping("/inputcontrol/set")
    public String setInputControl(InputControl control){
        readManager.setInputControl(control);
        return "redirect:/command";
    }
}
