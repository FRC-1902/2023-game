package frc.robot.states;

import edu.wpi.first.math.controller.PIDController;
import frc.robot.Constants;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.HeaderWrapper;
import frc.robot.subsystems.IMUSubsystem;

public class BalanceState implements State {
  private String name, parent;

  private DriveSubsystem drive;

  private PIDController yawPID;
  private IMUSubsystem imu;
  private HeaderWrapper compass;

  private double desiredYaw;

  // Child states are supposed to modify these, so Periodic() can set the appropriate speed
  // without things getting all messed up.
  public double calculatedForwardSpeed, calculatedYawSpeed;
  
  public BalanceState(String name, String parent){
    this.name = name;
    this.parent = parent;
    compass = new HeaderWrapper(0);
    drive = DriveSubsystem.getInstance();

    yawPID = new PIDController(0, 0, 0);
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

    compass.setHeadingOffset(desiredYaw);
  }

  @Override
  public void Leave() {
    System.out.println("left " + name);
  }

  @Override
  public void Periodic(RobotStateManager rs) {
    double currentYaw = compass.getHeading();

    System.out.format("Yaw: %3.1f, Pitch: %3.1f\n", currentYaw, imu.getZ());

    calculatedYawSpeed += yawPID.calculate(currentYaw);

    drive.arcadeDrive(calculatedForwardSpeed, calculatedYawSpeed);
    
    calculatedForwardSpeed = 0;
    calculatedYawSpeed = 0;
  }
}
