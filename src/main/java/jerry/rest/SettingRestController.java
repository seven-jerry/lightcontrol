package jerry.rest;

import jerry.beans.Setting;
import jerry.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/setting")
@CrossOrigin
public class SettingRestController {

    @Autowired
    SettingService settingService;


    @GetMapping("/list")
    public List<Setting> getSettings() {
        return settingService.availableSettings();
    }
}
