package frc.robot.states.balance;

import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.states.BalanceState;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IMUSubsystem;

public class DriveOntoPlatformState implements State {
    private String name;
    private BalanceState parent;

    private IMUSubsystem imu;
    private DriveSubsystem drive;

    private double desiredYaw;
    
    public DriveOntoPlatformState(String name, String parent){
        this.name = name;
        this.parent = (BalanceState) RobotStateManager.getInstance().findState("balanceState");
        imu = IMUSubsystem.getInstance();
        drive = DriveSubsystem.getInstance();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getParent() {
        return parent.getName();
    }

    @Override
    public void Enter() {
        System.out.println("entered" + name);
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        imu.setOffset(desiredYaw);

        imu.resetHeading();
    }
}
