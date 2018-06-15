package jerry.arduino;

import com.fazecast.jSerialComm.SerialPort;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) throws Exception {

        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(100);

        Arduino arduino = new Arduino("/dev/cu.usbmodemFA131", 19200);
        arduino.openConnection();
        SerialPort port = arduino.getSerialPort();
       // new ReadWriteThread(port, this).start();

        int take = 10;
        while (take > 0) {
            System.out.println("take " + queue.take());
            Thread.sleep(1000);
            take--;
        }
    }
}
