package frc.robot.states.teleOp;

import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.TurretvatorSubsystem;

public class CenterTurretState implements State{
    private String name, parent;
    TurretvatorSubsystem tvSub;
    
    public CenterTurretState(String name, String parent){
        this.name = name;
        this.parent = parent;
        tvSub = TurretvatorSubsystem.getInstance();
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
        tvSub.turretCenter();
    }
}
