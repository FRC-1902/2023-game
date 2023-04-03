package frc.robot.states.auto;

import frc.robot.statemachine.State;
import frc.robot.statemachine.RobotStateManager;

public class PickupState implements State{
    private String name, parent;
    
    public PickupState(String name, String parent){
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
    public void enter() {
    }

    @Override
    public void leave() {
    }

    @Override
    public void periodic(RobotStateManager rs) {
        /**
         * possible to implement auto driving to pick up here
         */
    }
}
