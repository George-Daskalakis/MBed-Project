import shed.mbed.LCD;
import shed.mbed.MBed;
import shed.mbed.MBedUtils;

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
    
    /**
     * Open a connection to the MBED.
     */
    public Program()
    {
        mbed = MBedUtils.getMBed();
    }
    
    /**
     * The starting point for the interactions.
     */
    public void run()
    {
        // Show a message on the LCD.
        // Where to show the message.
        int x = 0, y = 0;
        LCD lcd = mbed.getLCD();
        lcd.print(x, y, "Hello, world!");
        // Give us time to read the message before closing
        // the interaction.
        sleep(5000);
        // Clear the display.
        lcd.clear();
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
