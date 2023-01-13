package frc.robot.states;

import frc.robot.RobotStateManager;
import frc.robot.State;

public class Disabled implements State{
    private String name, parent;
    
    public Disabled(String name, String parent){
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
        System.out.println("entered disabled");
    }

    @Override
    public void Leave() {
        System.out.println("left disabled");
    }

    @Override
    public void Periodic(RobotStateManager rs) {

    }
}
