package jerry.viewcontroller;

import jerry.interaction.ClientInteractionManager;
import jerry.pojo.Setting;
import jerry.service.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/setting")
@CrossOrigin
public class SettingRestController {

    @Autowired
    PersistenceService settingService;

    @GetMapping("/list")
    public Setting getSettings() {
        return settingService.getSetting();
    }
}
