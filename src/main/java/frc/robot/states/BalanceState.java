package frc.robot.states;

import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.DriveSubsystem.ShiftState;

public class BalanceState implements State{
    private String name, parent;
    private DriveSubsystem ds;
    
    public BalanceState(String name, String parent){
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
        System.out.println("entered" + name);
        ds.shift(ShiftState.LOW);
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {

    }
}
