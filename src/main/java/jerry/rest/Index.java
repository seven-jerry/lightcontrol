package jerry.rest;

import jerry.arduino.ArduinoController;
import jerry.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Index {
    @Autowired
    ArduinoController arduinoController;


    @GetMapping("/start")
    public String start(Model model) {
        try {
            String info = arduinoController.start();
            model.addAttribute("info",info);

        } catch (Exception e){
            model.addAttribute("error",e.getMessage());
        }
        return "index";
    }

    @GetMapping("/stop")
    public String stop(Model model) {
        try {
            arduinoController.stop();
            model.addAttribute("info","controller stopped");

        } catch (Exception e){
            model.addAttribute("error",e.getMessage());
        }
        return "index";
    }

    @GetMapping("/switch")
    public String switchPage(Model model){
        return "switch";
    }
}
