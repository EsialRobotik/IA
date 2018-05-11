package fr.esialrobotik.miscallenious;

import com.pi4j.io.serial.Baud;
import esialrobotik.ia.utils.communication.raspberry.Serial;
import esialrobotik.ia.utils.log.LoggerFactory;
import org.apache.logging.log4j.Logger;

public class DomotikClient {

    private Serial serial;
    private Logger logger;

    public DomotikClient() {
        this.serial = new Serial("/dev/serial/by-id/usb-FTDI_FT232R_USB_UART_A9M9DV3R-if00-port0", Baud._38400);
        this.logger = LoggerFactory.getLogger(DomotikClient.class);
    }

    public void updateInfo(String time, String score) {
        String display = time + ";" + score + '\n';
        System.out.println("On affiche : " + display);
        this.serial.write(display);
    }
}
