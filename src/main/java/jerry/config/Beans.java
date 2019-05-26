package jerry.config;

import jerry.interaction.AbstractInteractionManager;
import jerry.interaction.AbstractStateNotifier;
import jerry.interaction.ClientInteractionManager;
import jerry.interaction.ClientStateNotifier;
import jerry.master.MasterUpdater;
import jerry.service.IClientStateChangeNotifiable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("client")
public class Beans {


    @Bean(name = "contextAdjustingInteractionManager")
    public AbstractInteractionManager interactionManager() {
        return new ClientInteractionManager();
    }

    @Bean(name = "contextAwareClientStateNotifier")
    public AbstractStateNotifier abstractClinetStateNotifier() {
        return new ClientStateNotifier();
    }


    @Bean(name="internet")
    public MasterUpdater internet(){
        MasterUpdater updater = new MasterUpdater();
        updater.type="internet";
        return updater;
    }

}
