package jerry.master;

import com.google.gson.Gson;
import jerry.arduino.WriteMessage;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.nio.charset.Charset;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class ClientStateFetcher extends Thread{


    ArrayBlockingQueue<String> sendQueue = new ArrayBlockingQueue<String>(1);
    AtomicReference<WriteMessage> readValue = new AtomicReference<>(new WriteMessage()  );

    String hostname;
    SettingManager manager;


    ClientStateFetcher(SettingManager manager, String hostname){
        this.hostname = hostname;
        this.manager = manager;
    }


    @Override
    public void run() {
        StringBuilder builder = new StringBuilder();
        String entity = null;
        try {
            while (true) {
                    entity = this.read();
                    if (entity != null) {
                        sendRead(entity);
                    }
                    WriteMessage msg = new Gson().fromJson(this.readStates(),WriteMessage.class);

                    if (msg != null && !msg.isEmpty()) {
                        this.manager.setStates(this.hostname, msg.value);
                    }
                    Thread.sleep(1000);

            }
        }catch (InterruptedException e){
            System.out.println("StateFetcher : "+e.getMessage());
        }


    }

    private String read() {
        System.out.println("take "+this.sendQueue.remainingCapacity());
        String s  = this.sendQueue.poll();
        return s;
    }
    private void sendRead(String entity){
        try {
            String key = manager.getSettingKey(hostname);
            WriteMessage message = new WriteMessage(key,entity);
            Gson g = new Gson();
            String h = g.toJson(message);
            HttpClient client = HttpClientBuilder.create().build();

            HttpPost post = new HttpPost("http://"+hostname+":8090/");
            post.setEntity(new StringEntity(h, Charset.defaultCharset()));
            client.execute(post);
        } catch (Exception e){
            System.out.println("sendRead Exception : "+e.getMessage());
        }
    }

    private String readStates(){
        try {
            String key = manager.getSettingKey(hostname);
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet("http://"+hostname+":8090/api/setting/state/?key="+key);
            HttpResponse response = client.execute(get);
            return IOUtils.toString(response.getEntity().getContent());
        } catch (Exception e){
            System.out.println("ClientStateFetcher "+e.getMessage());
        }
        return "";
    }
}
