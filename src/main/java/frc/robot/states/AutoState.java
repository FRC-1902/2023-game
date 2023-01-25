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
        System.out.println("entered " + name);
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        /**
         * if intake is filled -> grab  
         */
    }

}
