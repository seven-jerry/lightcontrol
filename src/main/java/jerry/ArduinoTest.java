package jerry;

import jerry.device.SerialDevice;

import java.util.Scanner;

public class ArduinoTest {

    public static void main(String[] args) {
        SerialDevice serialDevice = new SerialDevice(args[0],9600,true);
        serialDevice.startLifecycle();
        new Scanner(System.in).next();

    }
}
