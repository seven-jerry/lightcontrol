package jerry.master;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jerry.viewmodel.pojo.Client;
import jerry.viewmodel.pojo.ClientState;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ClientStateFetcher extends Thread {


    ConcurrentHashMap<String, ArrayBlockingQueue<String>> sendQueue = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, ClientState> labels = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, String> ips = new ConcurrentHashMap<>();

    private volatile boolean started;

    public ClientStateFetcher(Collection<Client> clients) {
        started = true;
        clients.forEach(e -> sendQueue.put(e.getLabel(), new ArrayBlockingQueue<String>(10)));
        clients.forEach(e -> labels.put(e.getLabel(), new ClientState()));
        clients.forEach(e -> ips.put(e.getLabel(), e.getIpAddress()));

    }

    public boolean hasStarted() {
        return started;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    for (String label : sendQueue.keySet()) {
                        ArrayBlockingQueue<String> queue = sendQueue.get(label);
                        String write = queue.poll();
                        if (write != null) {
                            String ip = ips.get(label);
                            System.out.println(writeCommand(ip, write));
                        }
                    }

                    for (String label : ips.keySet()) {
                        String ip = ips.get(label);
                        ClientState s = readStates(ip);
                        s.id = label;
                        labels.put(label, s);
                    }
                } catch (RuntimeException e) {
                    System.out.println(this.getClass() + " : git exception " + e);
                }
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            System.out.println(e);
            Thread.currentThread().interrupt();
        }

    }

    public String getStates() {
        try {
            GsonBuilder builder = new GsonBuilder();
            return new Gson().toJson(labels);
        } catch (Exception e) {
            return "";
        }
    }

    private ClientState readStates(String h) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(h);
            HttpResponse response = client.execute(get);
            String content = IOUtils.toString(response.getEntity().getContent());

            return new Gson().fromJson(content, ClientState.class);
        } catch (Exception e) {
            System.out.println("ClientStateFetcher " + e.getMessage());
        }
        return new ClientState();
    }

    private String writeCommand(String h, String command) {
        try {

            HttpClient client = HttpClientBuilder.create().build();
            String baseUrl = h.substring(0, h.lastIndexOf("/"));

            HttpGet get = new HttpGet(baseUrl + "/execute?command=" + command);
            HttpResponse response = client.execute(get);

            return "" + response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            System.out.println("ClientStateFetcher write " + e.getMessage());
        }
        return null;
    }

    public void write(String ids, String command) {
        String[] idArray = ids.split(",");

        for (String id : idArray) {
            if (sendQueue.containsKey(id)) {
                sendQueue.get(id).offer(command);
                continue;
            }
            System.out.println("ClientStateFetcher : no queue found " + id);
        }
    }

}
