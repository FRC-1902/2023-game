package frc.robot.statemachine;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Constants;

public class Controllers {
    private XboxController driveController;
    private XboxController manipController;
    private RobotStateManager rs;

    private static Controllers instance;

    public enum Button{
        A, B, X, Y,
        RB, LB, LS, RS
        //names of all buttons on controller
    }

    private Map<Enum<Button>, Integer> buttonMap;

    public enum Axis{
        LX, LY, RX, RY, LT, RT
    }

    private Map<Enum<Axis>, Integer> axisMap;

    public enum Action{
        PRESSED,
        RELEASED
    }

    public enum ControllerName{
        DRIVE, MANIP
    }

    private Controllers(){
        rs = RobotStateManager.getInstance();
        driveController = new XboxController(Constants.DRIVE_CONTROLLER_PORT);
        manipController = new XboxController(Constants.MANIP_CONTROLLER_PORT);

        buttonMap = new HashMap<>();
        buttonMap.put(Button.A, 1);
        buttonMap.put(Button.B, 2);
        buttonMap.put(Button.X, 3);
        buttonMap.put(Button.Y, 4);
        buttonMap.put(Button.LS, 9);
        buttonMap.put(Button.RS, 10);
        buttonMap.put(Button.LB, 5);
        buttonMap.put(Button.RB, 6);

        axisMap = new HashMap<>();
        axisMap.put(Axis.LX, 0);
        axisMap.put(Axis.LY, 1);
        axisMap.put(Axis.RX, 4);
        axisMap.put(Axis.RY, 5);
        axisMap.put(Axis.LT, 2);
        axisMap.put(Axis.RT, 3);
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
            return 0.0;
        }
    }

    /**Returns DPAD's POV degree value
     * @param name Controller name DRIVE/MANIP
     * @return integer of DPAD value
     * If controller is specified incorrectly, returns 0
     */
    public int getDPAD(ControllerName name) {
        switch(name) {
            case DRIVE:
                return driveController.getPOV();
            case MANIP:
                return manipController.getPOV();
            default:
                return 0;
        }
    }

    public void eventPeriodic(){
        for(Map.Entry<Enum<Controllers.Button>, Integer> entry : buttonMap.entrySet()) {
            if(driveController.getRawButtonPressed(entry.getValue())){
              rs.handleEvent(new Event((Button) entry.getKey(), Action.PRESSED, ControllerName.DRIVE));
            }
            if(driveController.getRawButtonReleased(entry.getValue())){
              rs.handleEvent(new Event((Button) entry.getKey(), Action.RELEASED, ControllerName.DRIVE));
            }
            
            if(manipController.getRawButtonPressed(entry.getValue())){
              rs.handleEvent(new Event((Button) entry.getKey(), Action.PRESSED, ControllerName.MANIP));
            }
            if(manipController.getRawButtonReleased(entry.getValue())){
              rs.handleEvent(new Event((Button) entry.getKey(), Action.RELEASED, ControllerName.MANIP));
            }
          }
    }

    public static Controllers getInstance(){
        if(instance==null){
            instance = new Controllers();
        }
        return instance;
    }
}
