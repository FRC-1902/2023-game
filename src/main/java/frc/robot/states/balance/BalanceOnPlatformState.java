package frc.robot.states.balance;

import edu.wpi.first.math.controller.PIDController;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.states.BalanceState;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IMUSubsystem;

public class BalanceOnPlatformState implements State {
  private String name;
  private BalanceState parent;

  private IMUSubsystem imu;
  private PIDController pitchPID;

  public BalanceOnPlatformState(String name, String parent){
    this.name = name;
    this.parent = (BalanceState) RobotStateManager.getInstance().findState("balance");

    imu = IMUSubsystem.getInstance();
    pitchPID = new PIDController(0, 0, 0);
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
    parent.calculatedForwardSpeed += pitchPID.calculate(imu.getZ(), 0);
  }
}
