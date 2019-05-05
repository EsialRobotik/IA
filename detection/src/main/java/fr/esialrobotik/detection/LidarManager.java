package fr.esialrobotik.detection;

import esialrobotik.ia.utils.rplidar.RpLidarListener;
import esialrobotik.ia.utils.rplidar.RpLidarLowLevelDriver;

import javax.inject.Inject;

/**
 * Created by icule on 12/05/17.
 */
public class LidarManager {
    private RpLidarLowLevelDriver driver;

    @Inject
    public LidarManager(RpLidarLowLevelDriver driver, RpLidarListener rpLidarListenerner) {
        this.driver = driver;
    }

    public void start() {
        driver.setVerbose(false);

        driver.sendReset();
        driver.pause(100);

        //for v2 only - I guess this command is ignored by v1
        driver.sendStartMotor(1023);
        driver.sendScan(500);
    }

    public void stop() {
        driver.sendReset();
        driver.pause(1000);
        driver.sendStartMotor(0);
        driver.pause(1000);
        driver.shutdown();
        driver.pause(1000);
    }
}
