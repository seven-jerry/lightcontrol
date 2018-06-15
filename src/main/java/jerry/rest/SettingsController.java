package jerry.rest;

import com.fazecast.jSerialComm.SerialPort;
import jerry.arduino.Arduino;
import jerry.beans.Input;
import jerry.beans.InputType;
import jerry.beans.Setting;
import jerry.service.SettingService;
import jerry.util.InetAddr;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

@Controller
@RequestMapping(value="setting")
public class SettingsController {

    @Autowired
    SettingService service;

    @GetMapping("")
    public String  index(Model model){
        model.addAttribute("settings",service.availableSettings());
        return "setting";
    }

 @GetMapping(value = "/add")
    public String add(Model model){
     model.addAttribute("setting", new Setting());
     model.addAttribute("ports", Arduino.allPorts());
     return "setting/add";
 }

    @GetMapping(value = "/detail")
    public String details(@RequestParam(name="id") String id, Model model){
        Setting setting = service.getSetting(id);
        Input input = new Input();
        input.setSettingId(setting.getId());
        model.addAttribute("setting",setting);
        model.addAttribute("newInput", input);
        model.addAttribute("types",InputType.values());
        model.addAttribute("ports", Arduino.allPorts());
        return "setting/detail";
    }

    @PostMapping(value = "/add")
    public String add(@ModelAttribute Setting setting,Model model){
        service.addSetting(setting);
        return this.details(setting.getId(),model);
    }

    @PostMapping(value = "/update")
    public String update(@ModelAttribute Setting setting,Model model,
                         @RequestParam(value="action", required=true) String action){

        if(action.equals("update")){
            service.updateSetting(setting);
        }
        if(action.equals("delete")){
            service.removeSetting(setting);
        }
        model.addAttribute("settings",service.availableSettings());
        return "setting";
    }

    @PostMapping(value = "/input/add")
    public String addInput(@ModelAttribute Input input,Model model){
        service.addInput(input);
        Setting s = service.getSetting(input.getSettingId());

        Input newInput = new Input();
        newInput.setSettingId(s.getId());
        model.addAttribute("setting",s);
        model.addAttribute("newInput", newInput);
        model.addAttribute("types",InputType.values());


        return "setting/detail";
    }

    @PostMapping(value = "/input/update")
    public String updateInput(@ModelAttribute Input input,Model model,
                              @RequestParam(value="action", required=true) String action){
        String settingId = input.getSettingId();
        if(action.equals("update")){
            service.updateInput(input);
        }
        if(action.equals("delete")){
            service.removeInput(input);
        }
        return this.details(settingId,model);
    }





}
