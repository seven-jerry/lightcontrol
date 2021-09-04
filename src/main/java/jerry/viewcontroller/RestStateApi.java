package jerry.viewcontroller;

import jerry.interaction.AbstractStateNotifier;
import jerry.interaction.ExternalReadConsumers;
import jerry.interaction.NodeUpdater;
import jerry.interaction.ReadManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Node;

@RestController
@Slf4j
@RequestMapping("/api/rest")
public class RestStateApi {
    @Autowired
    @Qualifier("contextAwareClientStateNotifier")
    AbstractStateNotifier notifier;

    @Autowired
    ReadManager readManager;


    @Autowired
    NodeUpdater nodeUpdater;


    @RequestMapping("/ping")
    public String ping(){
        nodeUpdater.startLifecycle();
        return "OK";
    }

    @RequestMapping("/command")
    public String doCommand(@RequestBody String body) {
        log.info(body);
        notifier.handleConsumerMessage(body);
        return "OK";
    }
}
