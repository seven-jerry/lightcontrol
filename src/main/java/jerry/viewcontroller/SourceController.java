package jerry.viewcontroller;

import jerry.pojo.SerialSource;
import jerry.service.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/setting")
public class SourceController {
    @Autowired
    PersistenceService persistenceService;

    @Autowired
    SettingsController settingsController;


    @PostMapping(value = "/inputsource/set")
    public String updateReaderSource(@ModelAttribute SerialSource serialSource,
                                     Model model) {
        persistenceService.updateSetting(e -> e.setInputSource(serialSource));
        return settingsController.details(model);
    }

    @PostMapping(value = "/outputsource/set")
    public String updateWriterSource(@ModelAttribute SerialSource serialSource,
                                     Model model) {
        persistenceService.updateSetting(e -> e.setOutputSource(serialSource));
        return settingsController.details(model);
    }
}
