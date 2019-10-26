
import shed.mbed.LCD;
import shed.mbed.MBed;
import shed.mbed.MBedUtils;
import shed.mbed.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * A template program for using the Shed API
 * to interact with the FRDM/MBED
 * 
 * @author djb
 * @version 2017.01.11
 */
public class Program
{
    // The object for interacting with the FRDM/MBED.
    private MBed mbed;
    private Accelerometer acc;
    private Magnemometer mag;
    private Scanner input;
    private Piezo buzzer;
    private double[] accLog;
    private double[] magLog;
    private Piezo speaker;
    private boolean buttonPressed;
    private LCD lcd;
   
    
    /**
     * Open a connection to the MBED.
     */
    public Program() throws FileNotFoundException
    {
        mbed = MBedUtils.getMBed();
        lcd = mbed.getLCD();
        acc = mbed.getAccelerometerBoard();
        mag = mbed.getMagnemometer();
        input = new Scanner(System.in);
        buzzer = mbed.getPiezo();
        accLog = new double[50];
        magLog = new double[50];
        speaker = mbed.getPiezo();
        buttonPressed = false;
        mbed.getJoystickFire().addListener(
            isPressed -> {
                if(isPressed) {
                    buttonPressed = true;
                }
            });
    }
    
    /**
     * The starting point for the interactions.
     */
    public void run()
    {
        int accIndex = 0;
        int magIndex = 0;
        double average = 0;
        
        boolean fastAcc = false;
        boolean fastMag = false;
        
        //fill out initial values to prevent an immediate false trigger
        reset();
        
        while(mbed.isOpen()){
            lcd.clear();
            lcd.print(0, 0, "Press button to send alert");
            
            XYZData accData = acc.getAcceleration();
            XYZData magData = mag.getMagnetism();
            accLog[accIndex] = accData.getMagnitude();
            magLog[magIndex] = magData.getMagnitude();
            
            //calculate average accelaration over last 500ms
            for(double value : accLog){
                average += value;
            }
            average = average / 50;
            
            accIndex++;
            if(accIndex > 49){
                accIndex = 0;
            }
            
            if(average > 1.25 || average < 0.75){
                fastAcc = true;
            }
            
            //calculate max change in orientation
            for(int i = 1; i < 49; i++){
                if(magLog[i] - magLog[i+1] > 40 || magLog[i] - magLog[i+1] < - 40){
                    fastMag = true;
                }
            }
            
            magIndex++;
            if(magIndex > 49){
                magIndex = 0;
            }

            //automatic trigger
            if(fastAcc && fastMag){
                trigger();
            }
            
            //manual trigger
            if(buttonPressed){
                trigger();
            }

            //reset values
            fastAcc = false;
            fastMag = false;
            buttonPressed = false;            
            average = 0;
            
            sleep(10);
        }

        System.out.println("Done!");
    }
    
    public void reset()
    {
        for(int i = 0; i < magLog.length; i++){
            magLog[i] = mag.getMagnetism().getMagnitude();
        }
        
        for(int i = 0; i < accLog.length; i++){
            accLog[i] = acc.getAcceleration().getMagnitude();
        }
    }
    
    public void trigger()
    {
        long triggerTime = System.currentTimeMillis() / 1000L;
        buttonPressed = false;

        while((System.currentTimeMillis() / 1000L) - triggerTime < 20 && !buttonPressed){
            speaker.playSound(1, 800);
            sleep(50);
            speaker.silence();
            sleep(50);
            lcd.clear();
            lcd.print(0, 0, "Fall detected");
            lcd.print(0, 10, "press button to cancel");
            lcd.print(0, 20, "sending alert in: " + (20 - ((System.currentTimeMillis() / 1000L) - triggerTime))) ;
        }
        lcd.clear();
        if(buttonPressed){
            lcd.print(0, 0, "Alert cancelled");
            sleep(2000);
        }
        else{
            lcd.print(0, 0, "Alert sent");
            sleep(2000);
        }
        buttonPressed = false;
        reset();
    }
    
    /**
     * Close the connection to the MBED.
     */
    public void finish()
    {
        mbed.close();
    }
    
    /**
     * A simple support method for sleeping the program.
     * @param millis The number of milliseconds to sleep for.
     */
    private void sleep(long millis)
    {
        try {
            Thread.sleep(millis);
        } 
        catch (InterruptedException ex) {
            // Nothing we can do.
        }
    }
    
}
