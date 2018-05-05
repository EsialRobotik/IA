package fr.esialrobotik.Divers;

import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.*;

import java.io.IOException;

public class DomotikClient {

    private static String domotikBTAddress = "AA:AA:AA:AA";
    private static final Serial serial = SerialFactory.createInstance();;

    public static void DomotikInitialise() {
        SerialConfig config = new SerialConfig();
        try {
            config.device(SerialPort.getDefaultPort())
                    .baud(Baud._9600)
                    .dataBits(DataBits._8)
                    .parity(Parity.NONE)
                    .stopBits(StopBits._1)
                    .flowControl(FlowControl.NONE);
            serial.open(config);
        }
        catch (Exception ex){
            System.err.println("Error when initializing Serial:" + ex.getMessage());
        }
    }

    public static void UpdateInfo(String time, String score){
        if(!serial.isOpen()){
            System.err.println("Serial not initialized bastard!");
            return;
        }
        try{
            serial.write(time + ";" + score);
        }
            catch (Exception ex){
            System.err.println("Error when writting to Serial:" + ex.getMessage());
        }
    }
}
