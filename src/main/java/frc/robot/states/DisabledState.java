package frc.robot.states;

import frc.robot.statemachine.State;
import frc.robot.statemachine.RobotStateManager;
import frc.robot.subsystems.DriveSubsystem;

public class DisabledState implements State{
    private String name;
    private String parent;
    private DriveSubsystem driveSubsystem;
    
    public DisabledState(String name, String parent){
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
        driveSubsystem.tankDrive(0, 0);
    }

    @Override
    public void leave() {
    }

    @Override
    public void periodic(RobotStateManager rs) {

    }
}
