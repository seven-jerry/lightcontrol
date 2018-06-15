package jerry.arduino;

import com.fazecast.jSerialComm.SerialPort;
import jerry.beans.KeyValuePair;
import jerry.beans.write.WriteEntity;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;


public class ReadWriteThread extends Thread {
    private Arduino arduino;
    private String key;
    public BlockingQueue<WriteEntity> writeQueue = new ArrayBlockingQueue<>(10);
    private boolean heartBeat;

    ReadWriteThread(Arduino arduino, String key) {
        this.arduino = arduino;
        this.key = key;
    }


    public void run() {
        StringBuilder builder = new StringBuilder();
        WriteEntity entity = null;

        while (true) {

            try {

                if (entity == null) {
                    entity = this.read();
                    this.arduino.serialWrite("{"+entity.getWriteContent()+"}");
                }


                while (arduino.getSerialPort().getInputStream().available() > 0) {
                    char readChar = (char) arduino.getSerialPort().getInputStream().read();
                    if(startOfMessage(readChar)){
                        builder = new StringBuilder();
                        continue;
                    }

                    if(endOfMessage(readChar)){
                        KeyValuePair pair = new KeyValuePair(this.key, builder.toString());
                        System.out.println(pair.getValue());
                        entity.response(pair);
                        entity = null;
                    }

                    builder.append(Character.valueOf(readChar));
                }

            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                break;

            } catch (IOException e) {
                System.out.println(e.getMessage());
                arduino.getSerialPort().closePort();
                break;
            }

        }
    }

    private WriteEntity read() throws InterruptedException {
        WriteEntity value;
        System.out.println("take "+this.writeQueue.remainingCapacity());
        value = this.writeQueue.take();
        System.out.println("value " + value);

        if (value.equals("{shb}")) {
            heartBeat = true;
        }
        return value;
    }


    public Arduino getArduino() {
        return arduino;
    }


    private boolean startOfMessage(char readChar){
        return readChar == '{';

    }
    private boolean endOfMessage(char readChar){
        return readChar == '}';

    }
}
