package frc.robot.states;

import frc.robot.Event;
import frc.robot.RobotStateManager;

public class TeleOpState implements frc.robot.State{

    private String name, parent;
    
    public TeleOpState(String name, String parent){
        this.name = name;
        this.parent = parent;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public void Enter() {
        System.out.println("entered " + name);
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        /*
        move(joystick1);
        turn(joystick2)
        
        */
    }

    @Override
    public boolean handleEvent(Event event, RobotStateManager rs) {
        switch(event.button){
            case A:
                switch(event.action){
                    case PRESSED:
                        rs.setState("disabled");
                    default:
                        break;
                }
                return true;
            default:
                return false;
            }
    }
    
}