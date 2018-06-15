package jerry.arduino;

import com.fazecast.jSerialComm.SerialPort;
import jerry.beans.Input;
import jerry.beans.InputType;
import jerry.beans.KeyValuePair;
import jerry.beans.Setting;
import jerry.beans.write.HeartBeat;
import jerry.beans.write.InputEntity;
import jerry.beans.write.WriteEntity;
import jerry.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Component
public class ArduinoController implements ReadCallback {

    @Autowired
    SettingService settingService;

    Map<String,int[][]> state = new HashMap<>();
    Map<String,Map<Integer,Input>> inputs = new HashMap<>();

    AtomicInteger skip = new AtomicInteger();

    boolean started;
    Map<String,ReadWriteThread> readers = new HashMap<>();
    ArrayList<WebSocketSession> consumers = new ArrayList<>();

    Timer timer;
    Timer heartBeatTimer;

    HeartBeatCallback heartBeatCallback = new HeartBeatCallback();
    InputCallback inputCallback = new InputCallback();


    public synchronized String start() throws Exception {
        String info = "";
        try {
            if (consumers.size() == 0) {
                info += "no active consumers;\n";
            }
            startArduinos();
            startReaders();
            startTimer();
            info += "started";
            started = true;
        } catch (Exception e) {
            this.stop();
            throw e;
        }

        return info;
    }

    private void startArduinos() throws Exception{
        for (Setting setting : settingService.availableSettings()) {
            Arduino arduino = new Arduino(setting.getSerialport(), setting.getBaundRate());
            arduino.openConnection();
            if (!arduino.getSerialPort().isOpen()) {
                System.out.println("serial port could not be opened " + setting.getSerialport());
                throw new Exception("serial port could not be opened " + setting.getSerialport());
            }
            readers.put(setting.getSerialport(),new ReadWriteThread(arduino,setting.getSerialport()));
            state.put(setting.getSerialport(),new int[setting.getOutputRow()][setting.getOutputColumn()]);

            inputs.put(setting.getSerialport(),setting.getInputArray());
        }
    }

    private void startReaders(){
        for (ReadWriteThread thread : readers.values()) {
            thread.start();
        }
    }
    private void startTimer(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ArduinoController.this.readInputs();
            }
        },500,2000);
        heartBeatTimer = new Timer();
        heartBeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ArduinoController.this.heartBeat();
            }
        },500,10000);
    }

    public void readInputs(){

        if(skip.getAndDecrement() > 0){
            System.out.println("ignore");
            return;
        }
        skip.set(0);
        for(ReadWriteThread thread : this.readers.values()){
            try {
                thread.writeQueue.put(new InputEntity(this.inputCallback));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void stop() {

        timer.cancel();
        heartBeatTimer.cancel();

        started = false;
        for (ReadWriteThread reader : this.readers.values()) {
            reader.interrupt();
        }
        for (ReadWriteThread thread : readers.values()) {
            thread.getArduino().getSerialPort().closePort();
        }
        readers = new HashMap<>();

    }

    public synchronized void addConsumer(WebSocketSession session) {
        this.consumers.add(session);
    }

    public synchronized void removeConsumer(WebSocketSession session) {
        this.consumers.remove(session);
    }

    public synchronized boolean hasStarted(){
        return started;
    }
    public void handleWrite(WebSocketSession consumer,TextMessage message){
        skip.getAndIncrement();
        KeyValuePair pair = KeyValuePair.fromJson(message.getPayload());
        if(pair == null){
            return;
        }

        try {
            this.updateState(pair);
            KeyValuePair readPair = new KeyValuePair(pair.getKey(),"1");
            handleRead(readPair);

            this.readers.get(pair.getKey()).writeQueue.put(new WriteEntity(pair.getValue(),this));
        } catch (Exception e){
            try {
                consumer.sendMessage(new TextMessage(e.getMessage()));
            } catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
    }
    public void updateInputs(KeyValuePair content){
        Map<Integer, Input> inputArr = this.inputs.get(content.getKey());
        String value = content.getValue();

        while(value.length() > 0){
            Integer x = (int)value.charAt(0) - '0';
            int s = (int)value.charAt(1) - '0';
            Input input = inputArr.get(x);
            if(input != null){
                int state = input.getState();
                if(state != s){
                    input.setState(s);
                    try {
                        if (state == 0) {
                            KeyValuePair pair = new KeyValuePair(content.getKey(), "sal");
                            handleRead(pair);
                            this.readers.get(pair.getKey()).writeQueue.put(new WriteEntity(pair.getValue(), this));
                        } else if (state == 1) {
                            if(input.getType() == InputType.MANUAL){
                                KeyValuePair pair = new KeyValuePair(content.getKey(), "shh");
                                handleRead(pair);
                                this.readers.get(pair.getKey()).writeQueue.put(new WriteEntity(pair.getValue(), this));
                            }
                            if(input.getType() == InputType.MOTION){
                                KeyValuePair pair =  new KeyValuePair(content.getKey(), "shh");
                                handleRead(pair);
                                this.readers.get(pair.getKey()).writeQueue.put(new WriteEntity(pair.getValue(), this));
                            }
                        }
                    }catch(Exception e){
                        System.out.println(e.getMessage());
                    }
                }
            }
            value = value.substring(2,value.length());
        }

    }

    private void updateState(KeyValuePair content){
        int[][] stateArr = this.state.get(content.getKey());
        String value = content.getValue();

        if(value.charAt(0) == 's'){
            value = this.commandProccessing(stateArr,value);
        }
        while(value.length() > 0){
            int x = (int)value.charAt(0) - '0';
            int y = (int)value.charAt(1) - '0';
            int s = (int)value.charAt(2) - '0';
            stateArr[x][y] = s;
            value = value.substring(3,value.length());
        }
    }

    private String getState(String key){
        int[][] stateArr = this.state.get(key);

        StringBuilder sender = new StringBuilder("");
        for (int i = 0; i < stateArr.length; i = i + 1) {
            for(int j = 0;j < stateArr[i].length;j++){
                sender.append(i);
                sender.append(j);
                sender.append(stateArr[i][j]);
            }
        }
        return sender.toString();
    }

    public void handleRead(KeyValuePair content) {

        KeyValuePair consumerContent = new KeyValuePair(content.getKey(),getState(content.getKey()));

        for (WebSocketSession session : consumers) {
            try {
                session.sendMessage(new TextMessage(consumerContent.toJson()));
            } catch (IOException e) {
                this.removeConsumer(session);
            }
        }
    }

    public void heartBeat() {
        if(skip.getAndDecrement() > 0){
            System.out.println("ignore");
            return;
        }
        skip.set(0);
        for(ReadWriteThread thread : this.readers.values()){
            try {
                thread.writeQueue.put(new HeartBeat(this.heartBeatCallback));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    String commandProccessing(int[][] array, String value){

        value = value.substring(1);
        String command = value.substring(0,2);

        if(command.equals("ah")){
            turnAllOn(array);
        }
        if(command.equals("al")){
            turnAllOff(array);
        }
        if(command.equals("hh")){
            turnHalfOn(array);
        }

        return value.substring(2);
    }

    private void turnAllOn(int[][] array){
        for (int i = 0; i < array.length; i = i + 1) {
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = 1;
            }
        }
    }

    private void turnAllOff(int[][] array){
        for (int i = 0; i < array.length; i = i + 1) {
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = 0;
            }
        }
    }

    private void turnHalfOn(int[][] array){
        int  on = 1;
        for (int i = 0; i < array.length; i = i + 1) {
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = on;
                if (on == 1) {
                    on = 0;
                } else if (on == 0) {
                    on = 1;
                }
            }
        }
    }

    @Override
    public void contentFromArduino(KeyValuePair content) {
        handleRead(content);
    }


    private class HeartBeatCallback implements ReadCallback{

        @Override
        public void contentFromArduino(KeyValuePair content) {
            if(content.getValue().equals(Arduino.OK_RESPONSE)){
                return;
            }
            ArduinoController.this.updateState(content);
            content.setValue(Arduino.OK_RESPONSE);
            handleRead(content);
        }
    }

    private class InputCallback implements ReadCallback{

        @Override
        public void contentFromArduino(KeyValuePair content) {
            if(content.getValue().equals(Arduino.OK_RESPONSE)){
                return;
            }

            ArduinoController.this.updateInputs(content);
            content.setValue(Arduino.OK_RESPONSE);
            handleRead(content);
        }
    }
}
