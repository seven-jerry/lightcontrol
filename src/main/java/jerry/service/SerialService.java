package jerry.service;

import com.fazecast.jSerialComm.SerialPort;
import jerry.arduino.MockArduino;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SerialService {
    public static ArrayList<String> allSerialPorts(){
        ArrayList<String> ports = new ArrayList<String>();
        for(SerialPort port : SerialPort.getCommPorts()){
            ports.add(port.getSystemPortName());
        }
        ports.add("mock port");
        return ports;
    }
}
