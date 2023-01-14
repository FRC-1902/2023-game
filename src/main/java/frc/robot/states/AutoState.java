package frc.robot.states;

import frc.robot.Event;
import frc.robot.RobotStateManager;
import frc.robot.State;

public class AutoState implements State{
    private String name, parent;
    
    public AutoState(String name, String parent){
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
        System.out.println("entered auto");
    }

    @Override
    public void Leave() {
        System.out.println("left auto");
    }

    @Override
    public void Periodic(RobotStateManager rs) {

    }

    @Override
    public boolean handleEvent(Event event, RobotStateManager rs) {
        return false;
    }

    
}
