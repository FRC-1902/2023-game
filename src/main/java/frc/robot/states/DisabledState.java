package frc.robot.states;

import frc.robot.statemachine.State;
import frc.robot.statemachine.RobotStateManager;
import frc.robot.subsystems.DriveSubsystem;

public class DisabledState implements State{
    private String name, parent;
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
    public void Enter() {
        System.out.println("entered " + name);
        driveSubsystem.tankDrive(0, 0);
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {

    }
}
