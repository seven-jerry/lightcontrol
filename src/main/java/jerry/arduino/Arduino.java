package jerry.arduino;

import com.fazecast.jSerialComm.*;

import java.io.PrintWriter;
import java.util.ArrayList;


public class Arduino {
    public static String OK_RESPONSE = "1";
    private SerialPort comPort;
    private String portDescription;
    private int baudRate;

    public static ArrayList<String> allPorts(){
        ArrayList<String> ports = new ArrayList<String>();
        for(SerialPort port : SerialPort.getCommPorts()){
            ports.add(port.getSystemPortName());
        }
        return ports;
    }

    Arduino(String portDescription, String baud_rate) {
       this(portDescription,Integer.valueOf(baud_rate));
    }


    Arduino(String portDescription, int baud_rate) {
        this.portDescription = portDescription;
        comPort = SerialPort.getCommPort(this.portDescription);
        this.baudRate = baud_rate;
        comPort.setBaudRate(this.baudRate);
    }

    public void openConnection(){
        if(comPort.isOpen()){
            return;
        }
        if(comPort.openPort()){
            try {Thread.sleep(1000);} catch(Exception e){}
        }
    }

    public SerialPort getSerialPort(){
        return comPort;
    }

    public String getPortDescription() {
        return portDescription;
    }

    public int getBaudRate() {
        return this.baudRate;
    }


    public void serialWrite(String s){
        //writes the entire string at once.
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        try{Thread.sleep(5);} catch(Exception e){}
        PrintWriter pout = new PrintWriter(comPort.getOutputStream());
        pout.print(s);
        pout.flush();

    }

}