package frc.robot.states;

import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.DriveSubsystem;

public class DisabledState implements State{
    private String name, parent;
    private DriveSubsystem driveSub;
    
    public DisabledState(String name, String parent){
        this.name = name;
        this.parent = parent;
        driveSub = DriveSubsystem.getInstance();
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
        
        driveSub.tankDrive(0, 0);
    }

    @Override
    public void Leave() {
        
    }

    @Override
    public void Periodic(RobotStateManager rs) {

    }
}
