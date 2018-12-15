package jerry.viewcontroller;

import jerry.arduino.StateArray;
import jerry.interaction.ReadManager;
import jerry.service.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/mock")
@RestController
public class MockStateEndpoint {

    @Autowired
    PersistenceService service;

    @Autowired
    ReadManager readManager;

    @RequestMapping("/setInputState")
    public String setInputState(@RequestParam("state") String state){
        readManager.handleMessage(new StateArray(state));
        return "OK";
    }
}
