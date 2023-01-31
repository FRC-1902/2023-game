package frc.robot.states;

import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.DriveSubsystem;

public class TestState implements State{
    private String name, parent;
    private DriveSubsystem ds;

    public TestState(String name, String parent){
        this.name = name;
        this.parent = parent;
        ds = DriveSubsystem.getInstance();
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
        ds.velocityPID(1, 0);
    }
}
