package frc.robot.states;

import frc.robot.statemachine.State;
import frc.robot.statemachine.RobotStateManager;
import frc.robot.subsystems.DriveSubsystem;

public class AutoState implements State{
    private String name, parent;
    private DriveSubsystem ds;
    
    public AutoState(String name, String parent){
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
        ds.shift(false);
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        /**
         * if intake is filled -> grab  
         * 
         * maintain odometry
         */
    }

}
