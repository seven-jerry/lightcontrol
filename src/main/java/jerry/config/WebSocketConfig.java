package jerry.config;

import jerry.viewcontroller.MasterSocketController;
import jerry.viewcontroller.WebSocketController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(webSocketController(), "/webSocket").setAllowedOrigins("*");
		registry.addHandler(masterSocketController(), "/masterSocket").setAllowedOrigins("*");

	}

	@Bean(name = "webSock")
	public WebSocketController webSocketController() {
		return new WebSocketController();
	}

	@Bean(name = "masterSock")
	public MasterSocketController masterSocketController() {
		return new MasterSocketController();
	}

}
