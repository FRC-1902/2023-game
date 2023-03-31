package frc.robot.subsystems;

import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.Constants;

public class LEDSubsystem {
    //TODO: debug me
    private AddressableLED driveLed;
    private AddressableLED turretLed;
    private AddressableLEDBuffer driveLedBuffer;
    private AddressableLEDBuffer driveTmpLedBuffer;
    private AddressableLEDBuffer turretLedBuffer;
    private AddressableLEDBuffer turretTmpLedBuffer;
    private static LEDSubsystem instance;
    private Timer delayTimer;

    private LEDSubsystem(){
        // Reuse buffer
        // Length is expensive to set, so only set it once, then just update data
        driveLed = new AddressableLED(Constants.DRIVE_LED_PORT);
        driveLedBuffer = new AddressableLEDBuffer(60);
        driveTmpLedBuffer = new AddressableLEDBuffer(driveLedBuffer.getLength());
        driveLed.setLength(driveLedBuffer.getLength());

        turretLed = new AddressableLED(Constants.TURRET_LED_PORT);
        turretLedBuffer = new AddressableLEDBuffer(60);
        turretTmpLedBuffer = new AddressableLEDBuffer(turretLedBuffer.getLength());
        turretLed.setLength(driveLedBuffer.getLength());

        // Set the data
        driveLed.setData(driveLedBuffer);
        driveLed.start();
        
        turretLed.setData(driveLedBuffer);
        turretLed.start();

        delayTimer = new Timer();
    }

    private void setRGB(AddressableLEDBuffer buffer, int r, int g, int b){
        for(int i = 0;i < buffer.getLength();i++){
            buffer.setRGB(i, r, g, b);
        }
    }
    
    public void setDriveRGB(int r, int g, int b){
        setRGB(driveLedBuffer, r, g, b);
        driveLed.setData(driveLedBuffer);
    }

    public void setTurretRGB(int r, int g, int b){
        setRGB(turretLedBuffer, r, g, b);
        turretLed.setData(turretLedBuffer);
    }

    public void setTemporaryRGB(int msTimeout, int r, int g, int b){
        setRGB(turretTmpLedBuffer, r, g, b);
        setRGB(driveTmpLedBuffer, r, g, b);
        driveLed.setData(driveTmpLedBuffer);
        driveLed.setData(driveTmpLedBuffer);

        // if scheduled again, it cancels the last timer task
        delayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                driveLed.setData(driveLedBuffer);
                turretLed.setData(turretLedBuffer);
            }
        }, msTimeout);
    }

    public static LEDSubsystem getInstance() {
        if (instance == null) {
          instance = new LEDSubsystem();
        }
    
        return instance;
      }
}
