package jerry.master;

//


import com.google.gson.Gson;
import jerry.arduino.StateArray;
import jerry.viewmodel.pojo.Command;
import jerry.viewmodel.pojo.Setting;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SettingManager {
    Map<String,Setting> settings = new HashMap<>();
    ConcurrentHashMap<String,StateArray> states = new ConcurrentHashMap<>();


    public static SettingManager withSettings(List<String> hosts) throws Exception{
        SettingManager manager = new SettingManager();
        for (String host :hosts){
            manager.initHost(host);
        }
        manager.initState();
        return manager;
    }
    public Map<String,List<Command>>  commands(){
        Map<String,List<Command>> c = new HashMap<>();

        this.settings.keySet().forEach(
                e -> c.put(e,this.settings.get(e).getCommands())
        );
        return c;
    }

    private void initHost(String host) throws Exception{
            String settings = readURLToString("http://"+host+":8090/api/setting/list");
            Gson gson  = new Gson();
            Setting[] set = gson.fromJson(settings, Setting[].class);
            this.settings.put(
                    Objects.requireNonNull(host),
                    Objects.requireNonNull(set[set.length-1])
            );
    }
    private static String readURLToString(String url) throws IOException
    {

        URLConnection urlConn = new URL(url).openConnection();
        urlConn.setConnectTimeout(3000);
        urlConn.setReadTimeout(3000);
        urlConn.setAllowUserInteraction(false);
        urlConn.setDoOutput(true);

        try (InputStream inputStream = urlConn.getInputStream())
        {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8.displayName());
        }
    }

    private void initState(){
           settings.forEach(
                   (k,v) -> this.states.put(k,new StateArray(v.getRows(),v.getColumns(),0))
           );

    }
        public String getSettingKey(String hostname){
        Setting s = settings.get(hostname);
        return "";//s.getSerialport();
        }
    public void setStates(String hostname, String msg) {
        this.states.get(hostname).setState(msg);
        this.states.forEach((e,k)->System.out.println("keyVal : "+e + " k "+k));
    }

}

