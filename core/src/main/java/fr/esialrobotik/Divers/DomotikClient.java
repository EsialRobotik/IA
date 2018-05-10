package fr.esialrobotik.Divers;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;

public class DomotikClient {
    File rfcommFile;

    public DomotikClient(){
        rfcommFile = new File("/dev/rfcomm0");
    }

    public boolean DomotikInitialise() {
        System.out.println("init");
        boolean rfcommExists = false;
        try {
            rfcommExists = rfcommFile.exists();
            System.out.println("RFCOMM exists!");
        }catch (Exception e){
            System.err.println("Fail to test rfcomm existence" + e.getMessage());
            return false;
        }
        if(!rfcommExists) {
            System.err.println("RFCOMM doesn't exists, not ready!");
            return false;
        }
        return true;
    }

    public void UpdateInfo(String time, String score){
        String display = time + ";" + score + '\n';
        try{
            BufferedWriter bw = Files.newBufferedWriter(rfcommFile.toPath());
            bw.write(display, 0, display.length());
            System.out.printf("display:" + display);
            bw.flush();
        }
            catch (Exception ex){
            System.err.println("Error when writting to Serial:" + ex.getMessage());
        }
    }
}
