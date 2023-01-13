package frc.robot;

import java.util.HashMap;
import java.util.Map;

public class Controller {

    private static Controller instance;
    public static Controller getInstance(){
        if(instance==null){
            instance = new Controller();
        }
        return instance;
    }

    public Map<Enum<Button>, Integer> buttonMap = 
        new HashMap<Enum<Button>, Integer>() {{
            put(Button.A, 1);
            put(Button.B, 2);
            put(Button.X, 3);
            put(Button.Y, 4);
            put(Button.RB, 5);
            put(Button.LB, 6);
        }};
     
    public enum Button{
        A, B, X, Y,
        RB, LB,
        //names of all buttons on controller
    }

    public enum Action{
        PRESSED,
        HELD,
        RELEASED
    }

    public static final int DRIVE_CONTROLLER_PORT = 0;
    public static final int MANIP_CONTROLLER_PORT = 1;
}
