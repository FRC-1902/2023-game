package frc.robot;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.XboxController;

public class Controller {
    public XboxController driveController;
    public XboxController manipController;

    private static Controller instance;
    public static Controller getInstance(){
        if(instance==null){
            instance = new Controller();
        }
        return instance;
    }

    public enum Button{
        A, B, X, Y,
        RB, LB, L, R
        //names of all buttons on controller
    }

    //TODO: check these button maps
    public Map<Enum<Button>, Integer> buttonMap = 
        new HashMap<Enum<Button>, Integer>() {{
            put(Button.A, 1);
            put(Button.B, 2);
            put(Button.X, 3);
            put(Button.Y, 4);
            put(Button.L, 5);
            put(Button.R, 6);
            put(Button.LB, 9);
            put(Button.RB, 10);
        }};

    public enum Axis{
        LX, LY, RX, RY, LT, RT
    }

    //TODO: check these button maps
    public Map<Enum<Axis>, Integer> axisMap = 
        new HashMap<Enum<Axis>, Integer>() {{
            put(Axis.LX, 0);
            put(Axis.LY, 1);
            put(Axis.RX, 2);
            put(Axis.RY, 3);
            put(Axis.LT, 4);
            put(Axis.RT, 5);
    }};

    public enum Action{
        PRESSED,
        HELD,
        RELEASED
    }

    public enum ControllerName{
        DRIVE, MANIP
    }

    /**Checks if specified button is depressed
     * @param name Controller name DRIVE/MANIP
     * @param button Button name
     * @return boolean if button is pressed.
     * If controller is specified incorrectly, returns false
     */
    public boolean get(ControllerName name, Button b){
        switch(name){
        case DRIVE:
            return driveController.getRawButton(buttonMap.get(b));
        case MANIP:
            return manipController.getRawButton(buttonMap.get(b));
        default:
            return false;
        }
    }

    /**Checks if specified button is depressed
     * @param name Controller name DRIVE/MANIP
     * @param axis Axis name
     * @return double of axis value, between -1 and 1
     * If controller is specified incorrectly, returns 0
     */
    public double get(ControllerName name, Axis a){
        switch(name){
        case DRIVE:
            return driveController.getRawAxis(axisMap.get(a));
        case MANIP:
            return manipController.getRawAxis(axisMap.get(a));
        default:
            return 0;
        }
    }

    public static final int DRIVE_CONTROLLER_PORT = 0;
    public static final int MANIP_CONTROLLER_PORT = 1;
}
