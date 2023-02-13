package frc.robot.states;

import frc.robot.RobotStateManager;
import frc.robot.State;

public class TestState implements State{
    private String name, parent;
    
    public TestState(String name, String parent){
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
        
    }

    @Override
    public void Leave() {
        
    }

    @Override
    public void Periodic(RobotStateManager rs) {

    }
}
