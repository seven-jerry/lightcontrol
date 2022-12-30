package jerry.device;

import jerry.master.IWebSocketResponseHandler;
import jerry.util.WebsocketManager;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class WebsocketTest implements IWebSocketResponseHandler {

    @Autowired
    WebsocketManager websocketManager;

    @Test
    public void test() throws Exception{
        Integer id = websocketManager.newSocket("ws://192.168.1.6:1880/local",this);
    }

    @Override
    public void onMessageFromSocket(Session session, String message) {

    }

    @Override
    public String pingPayload() {
        return "";
    }
}
