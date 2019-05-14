package jerry.config;

import jerry.interaction.*;
import jerry.master.ClientStateUpdater;
import jerry.service.IClientStateChangeNotifiable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("master")
public class MasterBeans {


    @Bean(name = "contextAdjustingInteractionManager")
    public AbstractInteractionManager interactionManager() {
        return new MasterInteractionManager();
    }

    @Bean(name = "contextAwareClientStateNotifier")
    public AbstractStateNotifier abstractClinetStateNotifier() {
        return new MasterStateNotifier();
    }

}