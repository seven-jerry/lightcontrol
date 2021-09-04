package jerry;


import jerry.interaction.EventHandler;
import jerry.master.IWebSocketResponseHandler;
import jerry.util.WebsocketManager;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.client.WebSocketConnectionManager;

import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client {


    public static void main(String[] args) throws Exception{
        Logger.getGlobal().setLevel(Level.OFF);
        String destUri = "ws://192.168.1.6:1880/local";
        if (args.length > 0)
        {
            destUri = args[0];
        }

        WebsocketManager manager = new WebsocketManager();
        manager.setEventHandler(new EventHandler());
        Integer id = manager.newSocket(destUri,new ResponseHandler());
        manager.checkSessionActive();
        while(true){
            Thread.sleep(3000);
            manager.writeToSocket(id,"test");
            manager.checkSessionActive();
        }

       // manager.stop();
    }

    public static class ResponseHandler implements IWebSocketResponseHandler{
        @Override
        public void onMessageFromSocket(Session session, String message) {
            System.out.println(message);
        }

        @Override
        public String pingPayload() {
            return "";
        }
    }


}
