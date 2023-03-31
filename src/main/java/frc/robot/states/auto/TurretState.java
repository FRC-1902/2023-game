package frc.robot.states.auto;

import frc.robot.statemachine.State;
import edu.wpi.first.wpilibj.DataLogManager;
import frc.robot.statemachine.RobotStateManager;

public class TurretState implements State{
    private String name, parent;
    
    public TurretState(String name, String parent){
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
       * set target turret state for placement
       */
    }
}
