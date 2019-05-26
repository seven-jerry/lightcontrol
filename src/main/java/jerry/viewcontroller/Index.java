package jerry.viewcontroller;

import jerry.interaction.AbstractInteractionManager;
import jerry.interaction.InputControl;
import jerry.interaction.ReadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
public class Index {
    @Autowired
    @Qualifier("contextAdjustingInteractionManager")
    AbstractInteractionManager lifeCycleClientInteractionManager;


    @Autowired
    SettingsController settingsController;

    @Autowired
    ReadManager readManager;


    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login";//You can redirect wherever you want, but generally it's a good practice to show login screen again.
    }

    @GetMapping(value = {"/switch", "/switch/{host}/"})
    public String switchPage(@PathVariable("host") Optional<String> host, Model model) {
        model.addAttribute("inputControls", InputControl.values());
        model.addAttribute("inputControl", readManager.getInputControl());

        if(host.isPresent()){
            model.addAttribute("host",host.get());
        }else {
            model.addAttribute("host","local");
        }
        return "switch";
    }

    @PostMapping("/switch/inputcontrol/set")
    public String setInputControl(InputControl control) {
        readManager.setInputControl(control);
        return "redirect:/switch";
    }

    @GetMapping("favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
    }
}
