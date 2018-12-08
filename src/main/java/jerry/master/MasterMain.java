package jerry.master;

import jerry.viewmodel.pojo.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jerry.master.SettingManager.withSettings;

public class MasterMain {

    private static MasterMain masterMain;
    public static volatile boolean hasStarted;
    SettingManager manager;
    Map<String,ClientStateFetcher> clientList = new HashMap<>();

    public static synchronized void stop(){
        if(!hasStarted) return;
        masterMain.clientList.values().forEach(Thread::interrupt);
        hasStarted = false;
    }

    public static synchronized void startMaster(List<String> args) throws Exception {
        if(hasStarted) return;
        masterMain = new MasterMain();
        masterMain.manager = withSettings(args);
        masterMain.clientList = startClients(masterMain.manager,args);
        hasStarted = true;
    }
    public static Map<String,List<Command>> getCommands(){
            return masterMain.manager.commands();
    }

    private static Map<String,ClientStateFetcher> startClients(SettingManager manager, List<String> args){
        Map<String,ClientStateFetcher> clientList = new HashMap<>();

        for(String arg:args){
            clientList.put(arg,new ClientStateFetcher(manager,arg));
        }
        for(ClientStateFetcher c : clientList.values()){
            c.start();
        }
        return clientList;
    }

}
