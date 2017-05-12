package fr.esialrobotik.detection;


import esialrobotik.ia.detection.DetectionInterface;

import javax.inject.Inject;

/**
 * Created by icule on 12/05/17.
 */
public class UltraSoundManager {
    private DetectionInterface detectionInterface;
    private int threshold;

    private Thread thread;
    private boolean hasBeenDetected;

    @Inject
    public UltraSoundManager(DetectionInterface detectionInterface) {
        this.hasBeenDetected = false;

    }

    public void start() {
        thread = new Thread(new Runnable() {
            public void run() {
                while(!thread.isInterrupted()){
                    final int[] pull = detectionInterface.ultraSoundDetection();
                    boolean pullRes = false;
                    for(int value : pull) {
                        if(value < threshold) {
                            pullRes = true;
                        }
                    }
                    hasBeenDetected = pullRes;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        thread.interrupt();
        try{
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean hasBeenDetected() {
        return this.hasBeenDetected;
    }
}
