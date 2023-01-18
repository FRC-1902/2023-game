package frc.robot.states;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import frc.robot.Constants;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IMUSubsystem;

public class BalanceState implements State{
    private String name, parent;
    private IMUSubsystem imu;
    private DriveSubsystem drive;
    private PIDController yawPID, pitchPID;

    private double desiredYaw;
    
    public BalanceState(String name, String parent){
        this.name = name;
        this.parent = parent;
        imu = IMUSubsystem.getInstance();
        drive = DriveSubsystem.getInstance();

        yawPID = new PIDController(0, 0, 0);
        pitchPID = new PIDController(0, 0, 0);
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

        desiredYaw = Constants.PLATFORM_YAW_DEG;
        // Checks whether or not the robot is coming from the back of the platform.
        if (Math.abs(imu.getX() - Constants.PLATFORM_YAW_DEG) > 180) {
            desiredYaw = (desiredYaw + 180) % 360;
        }
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        double currentPitch = imu.getZ(), currentYaw = imu.getX();
        double yawPlatformRelative, calculatedYawSpeed = yawPID.calculate(currentYaw, desiredYaw), calculatedForwardSpeed = 0;

        System.out.format("Pitch: %f, Yaw: %f\n", currentPitch, currentYaw);

        // Pitch correction thingy
        yawPlatformRelative = currentYaw - desiredYaw;
        if (yawPlatformRelative < Constants.PLATFORM_BALANCE_YAW_EPSILON_DEG &&
            yawPlatformRelative > -Constants.PLATFORM_BALANCE_YAW_EPSILON_DEG)
        {
            calculatedForwardSpeed = pitchPID.calculate(currentPitch, 0);
        }

        drive.arcadeDrive(calculatedForwardSpeed, calculatedYawSpeed);
    }
}
