package frc.robot.subsystems;

import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.Constants;

public class LEDSubsystem {
    private AddressableLED led;
    private AddressableLEDBuffer ledBuffer;
    private AddressableLEDBuffer tmpLedBuffer;
    private static LEDSubsystem instance;
    private Timer delayTimer;

    private LEDSubsystem(){
        // Reuse buffer
        // Length is expensive to set, so only set it once, then just update data
        ledBuffer = new AddressableLEDBuffer(60);
        tmpLedBuffer = new AddressableLEDBuffer(ledBuffer.getLength());

        //Can't easily create more than one AddressableLED object, they break in wpilib
        led = new AddressableLED(Constants.LED_PORT);
        led.setLength(ledBuffer.getLength());
        led.setData(ledBuffer);
        led.start();
        
        delayTimer = new Timer();
    }

    private void setRGBBuffer(AddressableLEDBuffer buffer, int r, int g, int b){
        for(int i = 0;i < buffer.getLength();i++){
            buffer.setRGB(i, r, g, b);
        }
    }
    
    public void setRGB(int r, int g, int b){
        setRGBBuffer(ledBuffer, r, g, b);
        delayTimer.cancel();
        led.setData(ledBuffer);
    }

    public void setTemporaryRGB(int msTimeout, int r, int g, int b){
        setRGBBuffer(tmpLedBuffer, r, g, b);
        led.setData(tmpLedBuffer);

        // if scheduled again, it cancels the last timer task
        delayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                led.setData(ledBuffer);
                led.setData(ledBuffer);
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
