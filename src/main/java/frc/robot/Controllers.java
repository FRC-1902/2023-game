package frc.robot;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.XboxController;

public class Controllers {
    public XboxController driveController;
    public XboxController manipController;

    private static Controllers instance;
    
    public static Controllers getInstance(){
        if(instance==null){
            instance = new Controllers();
        }
        return instance;
    }

    private Controllers(){
        driveController = new XboxController(DRIVE_CONTROLLER_PORT);
        manipController = new XboxController(MANIP_CONTROLLER_PORT);
    }



    public enum Button{
        A, B, X, Y,
        RB, LB, LS, RS
        //names of all buttons on controller
    }

    public Map<Enum<Button>, Integer> buttonMap = 
        new HashMap<Enum<Button>, Integer>() {{
            put(Button.A, 1);
            put(Button.B, 2);
            put(Button.X, 3);
            put(Button.Y, 4);
            put(Button.LS, 9);
            put(Button.RS, 10);
            put(Button.LB, 5);
            put(Button.RB, 6);
        }};

    public enum Axis{
        LX, LY, RX, RY, LT, RT
    }

    public Map<Enum<Axis>, Integer> axisMap = 
        new HashMap<Enum<Axis>, Integer>() {{
            put(Axis.LX, 0);
            put(Axis.LY, 1);
            put(Axis.RX, 4);
            put(Axis.RY, 5);
            put(Axis.LT, 2);
            put(Axis.RT, 3);
    }};

    public enum Action{
        PRESSED,
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
