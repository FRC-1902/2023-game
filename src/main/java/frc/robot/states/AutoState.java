package frc.robot.states;

import frc.robot.statemachine.State;
import frc.robot.statemachine.RobotStateManager;
import frc.robot.subsystems.DriveSubsystem;

public class AutoState implements State{
    private String name;
    private String parent;
    private DriveSubsystem driveSubsystem;
    
    public AutoState(String name, String parent){
        this.name = name;
        this.parent = parent;
        driveSubsystem = DriveSubsystem.getInstance();
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
        driveSubsystem.shift(false);
    }

    @Override
    public void leave() {
    }

    @Override
    public void periodic(RobotStateManager rs) {
    }

}
